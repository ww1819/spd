# -*- coding: utf-8 -*-
"""
Fix MyBatis shadowing: <bind name="tenantId"> makes #{tenantId} refer to bind, not entity.
1) Rename bind to scopedTenantId
2) Replace tenant-column #{tenantId} with #{scopedTenantId} on lines that are NOT optional
   entity filters (skip lines containing <if test="tenantId)
3) Skip replacement inside <select id="..."> that use explicit @Param("tenantId") (no bind)
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]  # spd/
MAPPER_GLOB = "**/*.xml"

SKIP_FILES = frozenset()  # add filenames if needed

# Mapper <select id> that take tenant via @Param("tenantId") — must keep #{tenantId}
EXPLICIT_TENANT_PARAM_SELECT_IDS = frozenset(
    {
        "selectFdSupplierByCodeAndTenantId",
        "countSupplierByTenantAndHisId",
        "selectFdSupplierByTenantAndHisId",
        "selectFdFactoryByCodeAndTenantId",
        "countFactoryByTenantAndHisId",
        "selectFdFactoryByTenantAndHisId",
        "selectFdFinanceCategoryByCodeAndTenantId",
        "countFinanceCategoryByTenantAndHisId",
        "selectFdFinanceCategoryByTenantAndHisId",
        "selectFdMaterialByTenantAndHisId",
        "selectFdMaterialByTenantAndCode",
        "selectFdUnitByTenantAndUnitName",
        "selectFdWarehouseCategoryByCodeAndTenantId",
        "countWarehouseCategoryByTenantAndHisId",
    }
)


def should_skip_line(line: str) -> bool:
    if "<if test=\"tenantId" in line or "<if test='tenantId" in line:
        return True
    if "test=\"tenantId" in line and "<if" in line:
        return True
    return False


def process_file(path: Path) -> bool:
    if path.name in SKIP_FILES:
        return False
    text = path.read_text(encoding="utf-8")
    if '<bind name="tenantId"' not in text:
        return False

    lines = text.splitlines(keepends=True)
    out = []
    changed = False
    inside_explicit_tenant_select = False

    for line in lines:
        orig = line

        # Track explicit @Param tenant selects (no <bind> in fragment)
        if "<select" in line and 'id="' in line:
            m = re.search(r'id="([^"]+)"', line)
            if m and m.group(1) in EXPLICIT_TENANT_PARAM_SELECT_IDS:
                inside_explicit_tenant_select = True
            else:
                inside_explicit_tenant_select = False
        elif "</select>" in line:
            inside_explicit_tenant_select = False

        if '<bind name="tenantId"' in line:
            line = line.replace('<bind name="tenantId"', '<bind name="scopedTenantId"')
            changed = True

        if inside_explicit_tenant_select:
            if line != orig:
                changed = True
            out.append(line)
            continue

        if should_skip_line(line):
            out.append(line)
            continue

        # Replace #{tenantId} only where it clearly targets tenant/customer column binding
        if "#{tenantId}" in line:
            if re.search(r"(tenant_id|customer_id)\s*=\s*#\{tenantId\}", line):
                line = line.replace("#{tenantId}", "#{scopedTenantId}")
                changed = True
            elif re.search(r"\.tenant_id\s*=\s*#\{tenantId\}", line):
                line = line.replace("#{tenantId}", "#{scopedTenantId}")
                changed = True
            elif re.match(r"^\s*#\{tenantId\}\s*,\s*$", line.strip()) or re.match(
                r"^\s*#\{tenantId\}\s*$", line.strip()
            ):
                line = line.replace("#{tenantId}", "#{scopedTenantId}")
                changed = True
            elif "tenant_id = #{tenantId}" in line or "tenant_id= #{tenantId}" in line:
                line = line.replace("#{tenantId}", "#{scopedTenantId}")
                changed = True

        if line != orig:
            changed = True
        out.append(line)

    if changed:
        path.write_text("".join(out), encoding="utf-8")
        return True
    return False


def main():
    fixed = []
    for p in ROOT.glob(MAPPER_GLOB):
        if "target" in p.parts or "node_modules" in p.parts:
            continue
        if "mapper" not in p.parts:
            continue
        try:
            if process_file(p):
                fixed.append(str(p.relative_to(ROOT)))
        except Exception as e:
            print(f"ERROR {p}: {e}")
    print(f"Updated {len(fixed)} files")
    for f in sorted(fixed):
        print(f"  {f}")


if __name__ == "__main__":
    main()
