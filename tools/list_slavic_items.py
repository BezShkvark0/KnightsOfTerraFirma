import zipfile
from pathlib import Path
path = Path(r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\epic-knights-slavic-armory\CjCtY8A1\773be444e0853c2f43e8aedcc32aca27b587501c\epic-knights-slavic-armory-CjCtY8A1.jar")
z = zipfile.ZipFile(path)
ids = sorted(n.rsplit("/", 1)[-1][:-5] for n in z.namelist() if "/models/item/" in n and n.endswith(".json"))
print("\n".join(ids))
