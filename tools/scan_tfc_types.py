import re
import zipfile
from collections import Counter

tfc = r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\terrafirmacraft\2gXJo8Ol\297d45c6234310b0103191de5f574c637875254a\terrafirmacraft-2gXJo8Ol.jar"
z = zipfile.ZipFile(tfc)
c = Counter()
samples = {}
pat = re.compile(r'"type"\s*:\s*"([^"]+)"')
for n in z.namelist():
    if not n.startswith("data/") or not n.endswith(".json") or "/recipe/" not in n:
        continue
    s = z.read(n).decode("utf-8", "replace")
    m = pat.search(s)
    if not m:
        continue
    c[m.group(1)] += 1
    samples.setdefault(m.group(1), (n, s[:500]))
for k, v in c.most_common():
    print(f"{v:5} {k}")
print("--- interesting ---")
for k, (n, s) in samples.items():
    if any(x in k for x in ("damage", "shapeless", "advanced", "extra")):
        print(k, n)
        print(s[:400])
        print()
