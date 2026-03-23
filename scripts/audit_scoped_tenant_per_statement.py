"""
Per-statement audit: mapper elements that reference #{scopedTenantId}
must contain <bind name="scopedTenantId"> within the same element.
"""
import pathlib
import xml.etree.ElementTree as ET

NS = {"m": "http://mybatis.org/dtd/mybatis-3-mapper.dtd"}
# MyBatis mapper files often have no default namespace in ElementTree without registering

def local_tag(tag: str) -> str:
    if "}" in tag:
        return tag.split("}", 1)[1]
    return tag


def audit_file(path: pathlib.Path) -> list[tuple[str, str]]:
    """Return list of (statement_id, issue) for problems."""
    text = path.read_text(encoding="utf-8", errors="replace")
    try:
        root = ET.fromstring(text)
    except ET.ParseError as e:
        return [("__parse__", f"XML parse error: {e}")]

    problems: list[tuple[str, str]] = []
    for el in root.iter():
        tag = local_tag(el.tag)
        # <sql> fragments often use #{scopedTenantId} with bind provided by including statement
        if tag not in ("select", "insert", "update", "delete"):
            continue
        sid = el.get("id") or ""
        body = ET.tostring(el, encoding="unicode")
        if "#{scopedTenantId}" in body:
            if '<bind name="scopedTenantId"' not in body:
                problems.append((sid or tag, "uses #{scopedTenantId} but no <bind name=\"scopedTenantId\"> in same element"))
    return problems


def main() -> None:
    root = pathlib.Path(__file__).resolve().parents[1] / "spd-biz/src/main/resources/mapper"
    all_problems: list[tuple[str, str, str]] = []
    for p in sorted(root.rglob("*.xml")):
        probs = audit_file(p)
        for sid, msg in probs:
            all_problems.append((str(p.relative_to(root.parents[2])), sid, msg))

    if not all_problems:
        print("OK: no per-statement scopedTenantId without bind.")
        return
    print(f"FOUND {len(all_problems)} issue(s):\n")
    for fp, sid, msg in all_problems:
        print(f"{fp}")
        print(f"  id={sid}: {msg}\n")


if __name__ == "__main__":
    main()
