import zipfile

tfc = r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\terrafirmacraft\2gXJo8Ol\297d45c6234310b0103191de5f574c637875254a\terrafirmacraft-2gXJo8Ol.jar"
z = zipfile.ZipFile(tfc)
shown = 0
for n in z.namelist():
    if not n.endswith(".json"):
        continue
    s = z.read(n).decode("utf-8", "replace")
    if "neoforge:false" in s:
        print(n)
        print(s[:600])
        print("---")
        shown += 1
        if shown >= 2:
            break
