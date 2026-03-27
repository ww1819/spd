"""List Mapper XML files that use #{scopedTenantId} but lack <bind name="scopedTenantId">."""
import pathlib

root = pathlib.Path(__file__).resolve().parents[1] / "spd-biz/src/main/resources/mapper"
with_scoped: set[str] = set()
with_bind: set[str] = set()
for p in root.rglob("*.xml"):
    t = p.read_text(encoding="utf-8", errors="replace")
    if "#{scopedTenantId}" in t:
        with_scoped.add(str(p))
    if '<bind name="scopedTenantId"' in t:
        with_bind.add(str(p))
orphan = sorted(with_scoped - with_bind)
print("Files with #{scopedTenantId} but NO <bind name=\"scopedTenantId\">:", len(orphan))
for x in orphan:
    print(x)
