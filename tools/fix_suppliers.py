from pathlib import Path

root = Path("src/main/java")
for path in root.rglob("*.java"):
    text = path.read_text(encoding="utf-8")
    updated = text.replace(
        "RegistrySupplier<MedievalArmorItem>",
        "RegistrySupplier<? extends MedievalArmorItem>",
    )
    updated = updated.replace(".getEntries().putBefore(", ".insertBefore(")
    updated = updated.replace(".getEntries().putAfter(", ".insertAfter(")
    updated = updated.replace(
        "_SUPPLIER.get(), CreativeModeTab",
        "_SUPPLIER.get().getDefaultInstance(), CreativeModeTab",
    )
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        print(path)
