import json
from pathlib import Path

root = Path("src/main/resources/data")
nonempty = 0
empty = 0
samples = []
for p in root.rglob("*.json"):
    try:
        data = json.loads(p.read_text(encoding="utf-8-sig"))
    except Exception:
        continue
    def walk(o):
        global nonempty, empty
        if isinstance(o, dict):
            if "nbt" in o:
                if o["nbt"] in ({}, None, ""):
                    empty += 1
                else:
                    nonempty += 1
                    if len(samples) < 8:
                        samples.append((str(p), o["nbt"]))
            for v in o.values():
                walk(v)
        elif isinstance(o, list):
            for v in o:
                walk(v)
    walk(data)
print("empty", empty, "nonempty", nonempty)
for s in samples:
    print(s)
