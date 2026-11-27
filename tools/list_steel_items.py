import zipfile
from pathlib import Path

path = Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-addon\ZLkRGFj6\4d164dc61b725a70a3d13ed75d7bb6ff0b3721df\epic-knights-addon-ZLkRGFj6.jar")
z = zipfile.ZipFile(path)
ids = sorted(n.rsplit("/", 1)[-1][:-5] for n in z.namelist() if "/models/item/" in n and n.endswith(".json") and n.rsplit("/", 1)[-1].startswith("steel_"))
print("\n".join(ids))
print("count", len(ids))
