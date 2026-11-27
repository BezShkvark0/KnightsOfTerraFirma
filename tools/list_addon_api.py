import zipfile
from pathlib import Path

jars = {
    "addon": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-addon\ZLkRGFj6\4d164dc61b725a70a3d13ed75d7bb6ff0b3721df\epic-knights-addon-ZLkRGFj6.jar"),
    "slavic": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-slavic-armory\CjCtY8A1\773be444e0853c2f43e8aedcc32aca27b587501c\epic-knights-slavic-armory-CjCtY8A1.jar"),
}
for name, path in jars.items():
    z = zipfile.ZipFile(path)
    print("====", name, "mods.toml ====")
    for entry in z.namelist():
        if entry.endswith("neoforge.mods.toml") or entry.endswith("mods.toml"):
            print(entry)
            print(z.read(entry).decode("utf-8", "replace")[:1500])
    classes = [n for n in z.namelist() if n.endswith(".class") and "$" not in n]
    interesting = [n for n in classes if any(k in n.lower() for k in ("item", "armor", "weapon", "model", "tab", "config"))]
    print("classes", len(classes), "interesting", len(interesting))
    for n in interesting:
        print(n)
    print()
