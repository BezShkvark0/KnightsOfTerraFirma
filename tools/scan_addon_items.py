import zipfile
from collections import Counter
from pathlib import Path

jars = {
    "addon": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-addon\ZLkRGFj6\4d164dc61b725a70a3d13ed75d7bb6ff0b3721df\epic-knights-addon-ZLkRGFj6.jar"),
    "slavic": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-slavic-armory\CjCtY8A1\773be444e0853c2f43e8aedcc32aca27b587501c\epic-knights-slavic-armory-CjCtY8A1.jar"),
}
metals = ["wood", "stone", "copper", "tin", "bronze", "silver", "gold", "iron", "steel", "diamond", "netherite", "black_steel", "blue_steel", "red_steel", "bismuth_bronze", "black_bronze"]
for name, path in jars.items():
    z = zipfile.ZipFile(path)
    ids = []
    for n in z.namelist():
        if "/models/item/" in n and n.endswith(".json"):
            ids.append(n.rsplit("/", 1)[-1][:-5])
    print("====", name, len(ids))
    prefixes = Counter()
    for item in ids:
        for metal in metals:
            if item.startswith(metal + "_"):
                prefixes[metal] += 1
                break
        else:
            prefixes["(none)"] += 1
    print(prefixes)
    print("unprefixed sample:")
    shown = 0
    for item in sorted(ids):
        if not any(item.startswith(m + "_") for m in metals):
            print(" ", item)
            shown += 1
            if shown >= 25:
                break
