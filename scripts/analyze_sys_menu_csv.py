# -*- coding: utf-8 -*-
"""Analyze exported sys_menu CSV for duplicates."""
import csv
import sys
from collections import defaultdict


def norm(s):
    return (s or "").strip()


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else "sys_menu_202603212150 - 副本.csv"
    rows = []
    with open(path, encoding="utf-8-sig", newline="") as f:
        for row in csv.DictReader(f):
            rows.append(row)

    active = [x for x in rows if norm(x.get("status", "")) == "0"]

    by_perm = defaultdict(list)
    for x in active:
        p = norm(x.get("perms", ""))
        if p:
            by_perm[p].append(x)

    dups_perm = {k: v for k, v in by_perm.items() if len(v) > 1}

    by_pp = defaultdict(list)
    for x in active:
        if norm(x.get("menu_type", "")) in ("C", "M"):
            pv = norm(x.get("path", ""))
            if pv:
                key = (norm(x.get("parent_id", "")), pv, norm(x.get("menu_type", "")))
                by_pp[key].append(x)

    dups_pp = {k: v for k, v in by_pp.items() if len(v) > 1}

    by_comp = defaultdict(list)
    for x in active:
        if norm(x.get("menu_type", "")) == "C":
            c = norm(x.get("component", ""))
            if c:
                by_comp[c].append(x)

    dups_comp = {k: v for k, v in by_comp.items() if len(v) > 1}

    by_pf = defaultdict(list)
    for x in active:
        if norm(x.get("menu_type", "")) == "F":
            p = norm(x.get("perms", ""))
            if p:
                key = (norm(x.get("parent_id", "")), p)
                by_pf[key].append(x)

    dups_pf = {k: v for k, v in by_pf.items() if len(v) > 1}

    # material-ish prefix filter
    def is_material_perm(p):
        return any(
            p.startswith(pref)
            for pref in (
                "warehouse:",
                "foundation:",
                "finance:",
                "hc:",
                "caigou:",
                "inWarehouse:",
                "department:",
                "dept:",
                "stock:",
                "material:",
                "gzOrder:",
            )
        )

    print("=== Total rows", len(rows), "active(status=0)", len(active))
    print("=== Duplicate perms (all):", len(dups_perm))
    mat_dups = {k: v for k, v in dups_perm.items() if is_material_perm(k)}
    print("=== Duplicate perms (material-ish prefixes):", len(mat_dups))

    for k in sorted(dups_perm.keys()):
        v = dups_perm[k]
        ids = sorted(int(x["menu_id"]) for x in v)
        keep = min(ids)
        drop = [i for i in ids if i != keep]
        print(f"\nPERM: {k}")
        print(f"  keep MIN(menu_id)={keep}  DROP: {drop}")
        for x in sorted(v, key=lambda z: int(z["menu_id"])):
            print(
                f"    {x['menu_id']:>5} | {x['menu_type']} | parent={x['parent_id']:<5} | {x['menu_name'][:40]}"
            )

    print("\n=== Duplicate parent+path C/M:", len(dups_pp))
    for k, v in sorted(dups_pp.items(), key=lambda x: str(x[0])):
        print(" ", k, [int(x["menu_id"]) for x in v])

    print("\n=== Duplicate component C:", len(dups_comp))
    for k, v in sorted(dups_comp.items()):
        ids = [int(x["menu_id"]) for x in sorted(v, key=lambda z: int(z["menu_id"]))]
        print(f"  {k[:75]}")
        print(f"    -> {ids}  keep={min(ids)} drop={[i for i in ids if i != min(ids)]}")

    print("\n=== Duplicate parent+perms F:", len(dups_pf))
    for k, v in sorted(dups_pf.items(), key=lambda x: (str(x[0][0]), x[0][1])):
        print(" ", k, [int(x["menu_id"]) for x in v])

    # Generate CALL statements for perms duplicates (keep min id)
    print("\n=== Suggested CALL sp_hc_merge_sys_menu(keep, drop) — review before run!")
    for k in sorted(dups_perm.keys()):
        v = dups_perm[k]
        ids = sorted(int(x["menu_id"]) for x in v)
        keep = min(ids)
        for mid in ids:
            if mid != keep:
                print(f"CALL sp_hc_merge_sys_menu({keep}, {mid}); -- {k}")


if __name__ == "__main__":
    main()
