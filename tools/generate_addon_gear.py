"""Build optional Epic Knights addon catalogs, models, and TFC recipes from the pinned jars."""

import json
import subprocess
import zipfile
from pathlib import Path

ROOT = Path("src/main/resources")
JAVAP = r"C:\Program Files\Java\jdk-21.0.11\bin\javap.exe"
JARS = {
    "magistuarmoryaddon": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-addon\ZLkRGFj6\4d164dc61b725a70a3d13ed75d7bb6ff0b3721df\epic-knights-addon-ZLkRGFj6.jar"),
    "slavicarmory": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-slavic-armory\CjCtY8A1\773be444e0853c2f43e8aedcc32aca27b587501c\epic-knights-slavic-armory-CjCtY8A1.jar"),
}
CLASSES = {
    "magistuarmoryaddon": (
        "com.magistuarmory.addon.item.armor.AddonArmorTypes",
        "com.magistuarmory.addon.item.AddonWeaponTypes",
    ),
    "slavicarmory": (
        "com.magistuarmory.slavicarmory.item.armor.AddonArmorTypes",
        "com.magistuarmory.slavicarmory.item.AddonWeaponTypes",
    ),
}
METALS = {
    "copper": (1, 1080, 648, 864, "tfc:metal/copper"),
    "tin": (1, 230, 138, 184, "tfc:metal/tin"),
    "bronze": (2, 950, 570, 760, "tfc:metal/bronze"),
    "bismuth_bronze": (2, 985, 591, 788, "tfc:metal/bismuth_bronze"),
    "black_bronze": (2, 1070, 642, 856, "tfc:metal/black_bronze"),
    "iron": (3, 1535, 921, 1228, "tfc:metal/wrought_iron"),
    "steel": (4, 1540, 924, 1232, "tfc:metal/steel"),
    "black_steel": (5, 1485, 891, 1188, "tfc:metal/black_steel"),
    "blue_steel": (5, 1540, 924, 1232, "tfc:metal/blue_steel"),
    "red_steel": (5, 1540, 924, 1232, "tfc:metal/red_steel"),
    "silver": (1, 961, 576, 768, "tfc:metal/silver"),
    "gold": (1, 1060, 636, 848, "tfc:metal/gold"),
}
# Iron gear in this mod melts to cast iron, matching the existing bastard-sword recipe.
MELT_FLUID = dict((metal, fluid) for metal, (*_, fluid) in ((m, v) for m, v in METALS.items()))
MELT_FLUID["iron"] = "tfc:metal/cast_iron"
MELT_TEMP = {metal: values[1] for metal, values in METALS.items()}
MELT_TEMP["iron"] = 1585

SLOT_SUFFIXES = (
    ("_boots", "boots"),
    ("_chestplate", "chestplate"),
    ("_breastplate", "chestplate"),
    ("_leggings", "leggings"),
    ("_helmet", "helmet"),
    ("_coif", "helmet"),
    ("_greaves", "leggings"),
)
SKIP_PARTS = (
    "decoration", "blocking", "raised", "cloth", "small_plate", "wire", "ring", "rivet", "spring",
    "cell", "fur", "rod", "sheet", "guard_", "dome", "mask", "unfinished", "material", "cord",
    "layer", "wings",
)


def javap_fields(jar, classname, field_type):
    output = subprocess.check_output([JAVAP, "-public", "-classpath", str(jar), classname], text=True)
    names = []
    needle = f" {field_type} "
    for line in output.splitlines():
        line = line.strip().rstrip(";")
        if needle in line and line.startswith("public static final"):
            names.append(line.split()[-1].lower())
    return names


def item_models(jar):
    with zipfile.ZipFile(jar) as archive:
        return sorted(
            name.rsplit("/", 1)[-1][:-5]
            for name in archive.namelist()
            if "/models/item/" in name and name.endswith(".json")
        )


def recipe_names(jar):
    with zipfile.ZipFile(jar) as archive:
        return [
            name.split("recipe/", 1)[1]
            for name in archive.namelist()
            if "/recipe/" in name and name.endswith(".json")
        ]


def strip_metal(piece_id):
    if piece_id.startswith("steel_"):
        return piece_id[len("steel_"):]
    if piece_id.startswith("iron_"):
        return piece_id[len("iron_"):]
    return piece_id


def slot_of(item_id, type_names):
    for suffix, slot in SLOT_SUFFIXES:
        if item_id.endswith(suffix):
            return slot
    if item_id in type_names or item_id.removeprefix("gilded_") in type_names:
        return "helmet"
    return None


def source_type(item_id, type_names):
    for name in sorted(type_names, key=len, reverse=True):
        if name and name in item_id:
            return name
    return "knight"


def armor_pieces(models, type_names):
    pieces = []
    seen = set()
    for item_id in models:
        if any(part in item_id for part in SKIP_PARTS):
            continue
        slot = slot_of(item_id, type_names)
        if slot is None:
            continue
        body = strip_metal(item_id)
        if body in seen:
            continue
        seen.add(body)
        pieces.append((item_id, source_type(item_id, type_names), slot, body))
    return pieces


def weapon_parent(name):
    if any(word in name for word in ("dagger", "seax", "sickle", "messer")):
        return "bettercombat:dagger"
    if any(word in name for word in ("halberd", "poleaxe", "billhook", "glaive", "fauchard", "voulge", "guisarme", "scythe", "partisan")):
        return "bettercombat:halberd"
    if any(word in name for word in ("spear", "lance", "rogatina")):
        return "bettercombat:spear"
    if any(word in name for word in ("axe", "bardiche")):
        return "bettercombat:axe"
    if any(word in name for word in ("mace", "hammer", "crow_beak", "goedendag", "evening_star")):
        return "bettercombat:mace"
    return "bettercombat:sword"


def write_json(path, payload):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")


def metal_of_original(item_id):
    for metal in METALS:
        if item_id.startswith(metal + "_"):
            return metal
    return "steel"


def heating(item_id, metal):
    return {
        "type": "tfc:heating",
        "ingredient": {"item": item_id},
        "result_fluid": {"amount": 300, "id": MELT_FLUID[metal]},
        "temperature": MELT_TEMP[metal],
        "use_durability": True,
    }


def anvil(item_id, metal, sheet):
    tag = f"c:{'sheets' if sheet else 'ingots'}/{metal}"
    return {
        "type": "tfc:anvil",
        "ingredient": {"tag": tag},
        "tier": METALS[metal][0],
        "rules": ["hit_last", "draw_second_last", "bend_third_last"],
        "apply_bonus": True,
        "result": {"id": item_id, "count": 1},
    }


def welding(item_id, metal, sheet):
    tag = f"c:{'sheets' if sheet else 'ingots'}/{metal}"
    return {
        "type": "tfc:welding",
        "first_input": {"tag": tag},
        "second_input": {"tag": tag},
        "tier": METALS[metal][0],
        "bonus": "copy_best",
        "result": {"id": item_id, "count": 1},
    }


def item_heat(item_id, metal):
    return {
        "ingredient": {"item": item_id},
        "heat_capacity": 11.428,
        "forging_temperature": METALS[metal][2],
        "welding_temperature": METALS[metal][3],
    }


def item_size(item_id, armor):
    return {
        "ingredient": {"item": item_id},
        "size": "large" if armor else "very_large",
        "weight": "heavy" if armor else "very_heavy",
    }


def damage_resistance(item_id):
    return {
        "ingredient": {"item": item_id},
        "piercing": 6.5,
        "slashing": 9.5,
        "crushing": 8.5,
    }


def emit_item_files(namespace, item_id, metal, armor, parent_model, attribute_parent):
    owner, path = item_id.split(":", 1)
    write_json(ROOT / f"data/knightsofterrafirma/recipe/addon/{namespace}/{owner}/heating/{path}.json", heating(item_id, metal))
    write_json(ROOT / f"data/knightsofterrafirma/recipe/addon/{namespace}/{owner}/anvil/{path}.json", anvil(item_id, metal, armor))
    write_json(ROOT / f"data/knightsofterrafirma/recipe/addon/{namespace}/{owner}/welding/{path}.json", welding(item_id, metal, armor))
    write_json(ROOT / f"data/knightsofterrafirma/tfc/item_heat/addon/{owner}/{path}.json", item_heat(item_id, metal))
    write_json(ROOT / f"data/knightsofterrafirma/tfc/item_size/addon/{owner}/{path}.json", item_size(item_id, armor))
    if armor:
        write_json(ROOT / f"data/knightsofterrafirma/tfc/item_damage_resistance/addon/{owner}/{path}.json", damage_resistance(item_id))
    else:
        attributes = ROOT / f"data/{owner}/weapon_attributes/{path}.json"
        if not attributes.exists():
            write_json(attributes, {"parent": attribute_parent})
    if owner == "knightsofterrafirma":
        model = ROOT / f"assets/knightsofterrafirma/models/item/{path}.json"
        if not model.exists():
            write_json(model, {"parent": parent_model})


def title(metal, body):
    metal_name = metal.replace("_", " ").title()
    piece_name = body.replace("_", " ").title()
    label = f"{metal_name} {piece_name}"
    if metal == "black_steel":
        return "§b" + label
    if metal in ("blue_steel", "red_steel"):
        return "§d" + label
    return label


def java_list(name, values, indent="    "):
    lines = [f"{indent}public static final java.util.List<String> {name} = java.util.List.of("]
    for value in values:
        lines.append(f'{indent}        "{value}",')
    if values:
        lines[-1] = lines[-1].rstrip(",")
    lines.append(f"{indent});")
    return "\n".join(lines)


def java_armor(name, pieces):
    lines = [f"    public static final java.util.List<ArmorPiece> {name} = java.util.List.of("]
    for piece_id, source, slot, _body in pieces:
        lines.append(f'            new ArmorPiece("{piece_id}", "{source}", "{slot}"),')
    if pieces:
        lines[-1] = lines[-1].rstrip(",")
    lines.append("    );")
    return "\n".join(lines)


def main():
    catalogs = {}
    lang = {}
    for modid, jar in JARS.items():
        armor_class, weapon_class = CLASSES[modid]
        type_names = javap_fields(jar, armor_class, "com.magistuarmory.item.armor.ArmorType")
        weapons = javap_fields(jar, weapon_class, "com.magistuarmory.item.WeaponType")
        models = item_models(jar)
        pieces = armor_pieces(models, set(type_names))
        catalogs[modid] = (weapons, pieces)
        print(modid, "weapons", len(weapons), "armor", len(pieces), "disable", len(recipe_names(jar)))

        for recipe in recipe_names(jar):
            write_json(ROOT / f"data/{modid}/recipe/{recipe}", {"neoforge:conditions": [{"type": "neoforge:false"}]})

        for name in weapons:
            parent_model = f"{modid}:item/steel_{name}"
            emit_item_files(modid, f"{modid}:steel_{name}", "steel", False, parent_model, weapon_parent(name))
            for metal in METALS:
                item = f"knightsofterrafirma:{metal}_{name}"
                emit_item_files(modid, item, metal, False, parent_model, weapon_parent(name))
                lang[f"item.knightsofterrafirma.{metal}_{name}"] = title(metal, name)

        for piece_id, _source, slot, body in pieces:
            original_metal = metal_of_original(piece_id)
            parent_model = f"{modid}:item/{piece_id}"
            emit_item_files(modid, f"{modid}:{piece_id}", original_metal, True, parent_model, None)
            for metal in METALS:
                item_name = f"{metal}_{body}"
                emit_item_files(modid, f"knightsofterrafirma:{item_name}", metal, True, parent_model, None)
                lang[f"item.knightsofterrafirma.{item_name}"] = title(metal, body)

    addon_weapons, addon_armor = catalogs["magistuarmoryaddon"]
    slavic_weapons, slavic_armor = catalogs["slavicarmory"]
    catalog = """package net.witchkings.knightsofterrafirma.item.addon;

public final class GearCatalog {
    public record ArmorPiece(String pieceId, String sourceType, String slot) {
    }

"""
    catalog += java_list("ADDON_WEAPONS", addon_weapons) + "\n\n"
    catalog += java_armor("ADDON_ARMOR", addon_armor) + "\n\n"
    catalog += java_list("SLAVIC_WEAPONS", slavic_weapons) + "\n\n"
    catalog += java_armor("SLAVIC_ARMOR", slavic_armor) + "\n"
    catalog += "}\n"
    Path("src/main/java/net/witchkings/knightsofterrafirma/item/addon/GearCatalog.java").write_text(catalog, encoding="utf-8")

    lang_path = ROOT / "assets/knightsofterrafirma/lang/en_us.json"
    existing = json.loads(lang_path.read_text(encoding="utf-8"))
    existing.update(lang)
    existing["creativetab.kotf_addon"] = "Knights of TerraFirma: Addon"
    existing["creativetab.kotf_slavic"] = "Knights of TerraFirma: Slavic Armory"
    lang_path.write_text(json.dumps(existing, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print("lang keys", len(lang))


if __name__ == "__main__":
    main()
