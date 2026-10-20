import json
from pathlib import Path

root = Path("src/main/resources/data")
problems = []
old_dirs = [
    root / "forge",
    root / "knightsofterrafirma" / "recipes",
    root / "knightsofterrafirma" / "tfc" / "item_heats",
    root / "knightsofterrafirma" / "tfc" / "item_sizes",
    root / "knightsofterrafirma" / "tfc" / "item_damage_resistances",
]
for d in old_dirs:
    if d.exists():
        problems.append(f"old dir remains: {d}")

counts = {}
for p in root.rglob("*.json"):
    data = json.loads(p.read_text(encoding="utf-8"))
    text = p.read_text(encoding="utf-8")
    for needle in ("forge:false", "knightsofterrafirma:welding", "apply_forging_bonus", '"nbt"', "item_heats", "damage_inputs"):
        if needle in text:
            problems.append(f"{needle} in {p}")
    if isinstance(data, dict):
        t = data.get("type")
        if t:
            counts[t] = counts.get(t, 0) + 1
        if t == "tfc:anvil" and "input" in data:
            problems.append(f"anvil input {p}")
        fluid = data.get("result_fluid")
        if isinstance(fluid, dict) and "fluid" in fluid:
            problems.append(f"fluid key {p}")
        if "forge:" in text:
            problems.append(f"forge ns {p}")

print("types:")
for k, v in sorted(counts.items(), key=lambda kv: -kv[1]):
    print(f"  {v:5} {k}")
print("problems", len(problems))
for line in problems[:40]:
    print(line)
print("item_heat", sum(1 for _ in (root / "knightsofterrafirma" / "tfc" / "item_heat").rglob("*.json")))
print("item_size", sum(1 for _ in (root / "knightsofterrafirma" / "tfc" / "item_size").rglob("*.json")))
print("item_damage", sum(1 for _ in (root / "knightsofterrafirma" / "tfc" / "item_damage_resistance").rglob("*.json")))
print("c tags", sum(1 for _ in (root / "c").rglob("*.json")))
print("weapon", sum(1 for _ in (root / "knightsofterrafirma" / "weapon_attributes").rglob("*.json")))
