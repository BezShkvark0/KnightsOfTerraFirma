"""Rewrite Knights of TerraFirma datapack JSON for NeoForge 1.21.1 / TFC 4.2."""

import json
import os
from pathlib import Path

ROOT = Path("src/main/resources/data")


def item_stack(obj):
    if not isinstance(obj, dict) or "item" not in obj:
        return obj
    out = {"id": obj["item"]}
    out["count"] = obj.get("count", 1)
    return out


def modifiers(value):
    if not isinstance(value, list):
        return value
    out = []
    for entry in value:
        if isinstance(entry, str):
            out.append({"type": entry})
        else:
            out.append(entry)
    return out


def provider(obj):
    if not isinstance(obj, dict):
        return obj
    mods = modifiers(obj["modifiers"]) if "modifiers" in obj else None
    if "stack" in obj:
        stack = item_stack(obj["stack"])
        if mods:
            return {"stack": stack, "modifiers": mods}
        return stack
    if "item" in obj:
        stack = item_stack(obj)
        if mods:
            return {"stack": stack, "modifiers": mods}
        return stack
    if mods is not None:
        copied = dict(obj)
        copied["modifiers"] = mods
        return copied
    return obj


def sewing_stitches(values):
    rows = []
    flat = list(values)
    if len(flat) != 45:
        raise ValueError(f"expected 45 stitches, got {len(flat)}")
    for row in range(5):
        chars = []
        for col in range(9):
            chars.append(" " if flat[row * 9 + col] == 0 else "#")
        rows.append("".join(chars))
    return rows


def sewing_squares(values):
    rows = []
    flat = list(values)
    if len(flat) != 32:
        raise ValueError(f"expected 32 squares, got {len(flat)}")
    for row in range(4):
        chars = []
        for col in range(8):
            value = flat[row * 8 + col]
            if value == 0:
                chars.append("B")
            elif value == 1:
                chars.append("W")
            else:
                chars.append(" ")
        rows.append("".join(chars))
    return rows


def transform(data):
    if not isinstance(data, dict):
        return rewrite(data)

    recipe_type = data.get("type")
    if "conditions" in data:
        converted = []
        for condition in data.pop("conditions"):
            if isinstance(condition, dict) and condition.get("type") == "forge:false":
                converted.append({"type": "neoforge:false"})
            else:
                converted.append(condition)
        data["neoforge:conditions"] = converted
        if set(data) == {"neoforge:conditions"}:
            return data

    if recipe_type == "tfc:damage_inputs_shapeless_crafting":
        inner = data["recipe"]
        ingredient = inner["ingredients"][0]
        result = item_stack(inner["result"])
        return rewrite({
            "type": "tfc:advanced_shapeless_crafting",
            "ingredients": [ingredient],
            "primary_ingredient": ingredient,
            "remainder": {"modifiers": [{"type": "tfc:damage_crafting_remainder"}]},
            "result": result,
        })

    if recipe_type == "knightsofterrafirma:welding":
        data["type"] = "tfc:welding"
        recipe_type = "tfc:welding"

    if recipe_type == "tfc:anvil":
        if "input" in data and "ingredient" not in data:
            data["ingredient"] = data.pop("input")
        if "apply_forging_bonus" in data:
            data["apply_bonus"] = data.pop("apply_forging_bonus")
        if "result" in data:
            data["result"] = provider(data["result"])

    if recipe_type in ("tfc:welding", "tfc:advanced_shaped_crafting", "tfc:advanced_shapeless_crafting"):
        if "combine_forging_bonus" in data:
            if data.pop("combine_forging_bonus"):
                data["bonus"] = "copy_best"
        if "result" in data:
            data["result"] = provider(data["result"])

    if recipe_type == "tfc:heating":
        fluid = data.get("result_fluid")
        if isinstance(fluid, dict) and "fluid" in fluid:
            fluid["id"] = fluid.pop("fluid")
        if "result_item" in data:
            data["result_item"] = provider(data["result_item"])

    if recipe_type in (
        "minecraft:crafting_shaped",
        "minecraft:crafting_shapeless",
        "minecraft:smithing_transform",
        "tfc:sewing",
        "tfc:knapping",
        "tfc:barrel_sealed",
    ):
        if recipe_type != "tfc:barrel_sealed" and "result" in data and recipe_type != "tfc:advanced_shaped_crafting":
            if recipe_type in ("tfc:sewing", "tfc:knapping", "minecraft:crafting_shaped", "minecraft:crafting_shapeless", "minecraft:smithing_transform"):
                data["result"] = item_stack(data["result"])

    if recipe_type == "tfc:sewing":
        data["stitches"] = sewing_stitches(data["stitches"])
        data["squares"] = sewing_squares(data["squares"])

    if recipe_type == "tfc:barrel_sealed":
        item = data.get("input_item", {})
        if isinstance(item, dict) and "ingredient" in item:
            ingredient = item["ingredient"]
            item_id = ingredient["item"] if isinstance(ingredient, dict) else ingredient
            data["input_item"] = {"count": item.get("count", 1), "item": item_id}
        fluid = data.get("input_fluid", {})
        if isinstance(fluid, dict) and "ingredient" in fluid:
            data["input_fluid"] = {"amount": fluid["amount"], "fluid": fluid["ingredient"]}
        if "output_item" in data:
            data["output_item"] = item_stack(data["output_item"])

    if recipe_type == "tfc:knapping":
        outside = data.pop("outside_slot_required", None)
        pattern = data.get("pattern", [])
        full = len(pattern) == 5 and all(len(row) == 5 for row in pattern)
        if not full:
            data["default_on"] = bool(outside) if outside is not None else False

    return rewrite(data)


def rewrite(obj):
    if isinstance(obj, str):
        if obj.startswith("forge:"):
            return "c:" + obj[len("forge:"):]
        if obj.startswith("#forge:"):
            return "#c:" + obj[len("#forge:"):]
        return obj
    if isinstance(obj, list):
        return [rewrite(v) for v in obj]
    if isinstance(obj, dict):
        return {k: rewrite(v) for k, v in obj.items() if not str(k).startswith("__")}
    return obj


def remap(rel):
    rel = rel.replace("\\", "/")
    if rel.startswith("forge/"):
        rel = "c/" + rel[len("forge/"):]
    rel = rel.replace("/recipes/", "/recipe/")
    rel = rel.replace("/tags/items/", "/tags/item/")
    rel = rel.replace("/tags/blocks/", "/tags/block/")
    rel = rel.replace("/tags/fluids/", "/tags/fluid/")
    rel = rel.replace("/tfc/item_heats/", "/tfc/item_heat/")
    rel = rel.replace("/tfc/item_sizes/", "/tfc/item_size/")
    rel = rel.replace("/tfc/item_damage_resistances/", "/tfc/item_damage_resistance/")
    return rel


def main():
    files = [p for p in ROOT.rglob("*.json")]
    moved = 0
    changed = 0
    for path in files:
        original = path.read_bytes()
        data = json.loads(original.decode("utf-8-sig"))
        updated = transform(data)
        rel = path.relative_to(ROOT).as_posix()
        dest_rel = remap(rel)
        dest = ROOT / dest_rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        text = json.dumps(updated, indent=2) + "\n"
        dest.write_text(text, encoding="utf-8")
        if dest.resolve() != path.resolve():
            path.unlink()
            moved += 1
        elif text.encode("utf-8") != original:
            changed += 1
    # Drop directories left empty by the moves.
    for dirpath, dirnames, filenames in os.walk(ROOT, topdown=False):
        if not dirnames and not filenames:
            os.rmdir(dirpath)
    print(f"files={len(files)} moved={moved} rewritten_in_place={changed}")


if __name__ == "__main__":
    main()
