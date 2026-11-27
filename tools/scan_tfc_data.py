import zipfile

tfc = r"C:\Users\vvede\AppData\Local\Temp\cursor-sandbox-cache\a8f3bcf7b669440f80a21f2b3a1508d3\gradle\caches\modules-2\files-2.1\maven.modrinth\terrafirmacraft\2gXJo8Ol\297d45c6234310b0103191de5f574c637875254a\terrafirmacraft-2gXJo8Ol.jar"
z = zipfile.ZipFile(tfc)
for prefix in ("data/tfc/tfc/item_size/", "data/tfc/tfc/item_heat/", "data/tfc/tfc/item_damage_resistance/"):
    hits = [n for n in z.namelist() if n.startswith(prefix) and n.endswith(".json")]
    print(prefix, len(hits))
    if hits:
        print(hits[0])
        print(z.read(hits[0]).decode()[:400])
        print("---")
