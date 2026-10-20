import zipfile
from collections import Counter
from pathlib import Path

jars = {
    "addon": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-addon\ZLkRGFj6\4d164dc61b725a70a3d13ed75d7bb6ff0b3721df\epic-knights-addon-ZLkRGFj6.jar"),
    "slavic": Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-slavic-armory\CjCtY8A1\773be444e0853c2f43e8aedcc32aca27b587501c\epic-knights-slavic-armory-CjCtY8A1.jar"),
}
for name, path in jars.items():
    z = zipfile.ZipFile(path)
    print("====", name)
    recipes = [n for n in z.namelist() if "/recipe/" in n and n.endswith(".json")]
    print("recipes", len(recipes))
    types = Counter()
    for n in recipes:
        text = z.read(n).decode("utf-8", "replace")
        i = text.find('"type"')
        types[text[i:i+60].split('"')[3] if i >= 0 else "?"] += 1
    print(types)
    print("sample recipes:")
    for n in recipes[:8]:
        print(n)
        print(z.read(n).decode()[:300])
        print("---")
    models = [n for n in z.namelist() if n.startswith("assets/") and n.endswith(".json") and "/models/item/" in n]
    print("item models", len(models))
    print("sample models", models[:5])
