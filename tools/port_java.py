import re
import pathlib

root = pathlib.Path("src/main/java")
skip = {
    "RPLoader.java",
    "ArmorModifier.java",
    "ArmorTypeMixin.java",
    "ModItemTierMixin.java",
}
import_map = [
    ("import net.minecraftforge.api.distmarker.", "import net.neoforged.api.distmarker."),
    ("import net.minecraftforge.common.MinecraftForge;", "import net.neoforged.neoforge.common.NeoForge;"),
    ("import net.minecraftforge.event.server.", "import net.neoforged.neoforge.event.server."),
    ("import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;", "import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;"),
    ("import net.minecraftforge.event.AddPackFindersEvent;", "import net.neoforged.neoforge.event.AddPackFindersEvent;"),
    ("import net.minecraftforge.eventbus.api.", "import net.neoforged.bus.api."),
    ("import net.minecraftforge.fml.common.Mod;", "import net.neoforged.fml.common.Mod;"),
    ("import net.minecraftforge.fml.event.lifecycle.", "import net.neoforged.fml.event.lifecycle."),
    ("import net.minecraftforge.fml.ModList;", "import net.neoforged.fml.ModList;"),
    ("import net.minecraftforge.registries.DeferredRegister;", "import net.neoforged.neoforge.registries.DeferredRegister;"),
    ("import net.minecraftforge.registries.RegistryObject;", "import net.neoforged.neoforge.registries.DeferredHolder;"),
    ("import dev.architectury.platform.forge.EventBuses;\n", ""),
    ("import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;\n", ""),
    ("import org.spongepowered.asm.mixin.Mixins;\n", ""),
]
levels = {
    "2": "BlockTags.INCORRECT_FOR_IRON_TOOL",
    "4": "BlockTags.INCORRECT_FOR_DIAMOND_TOOL",
    "5": "BlockTags.INCORRECT_FOR_NETHERITE_TOOL",
}


def convert_rl(expr: str) -> str:
    match = re.match(r"new ResourceLocation\((.*)\)\s*$", expr.strip())
    if not match:
        return expr
    inner = match.group(1)
    parts = [part.strip() for part in inner.split(",")]
    if len(parts) == 2:
        return f"ResourceLocation.fromNamespaceAndPath({parts[0]}, {parts[1]})"
    if len(parts) == 1:
        literal = parts[0]
        if literal.startswith('"') and ":" not in literal:
            return f"ResourceLocation.withDefaultNamespace({literal})"
        return f"ResourceLocation.parse({literal})"
    return expr


for path in root.rglob("*.java"):
    if path.name in skip:
        continue
    text = path.read_text(encoding="utf-8")
    original = text
    for old, new in import_map:
        text = text.replace(old, new)
    text = text.replace("MinecraftForge.EVENT_BUS", "NeoForge.EVENT_BUS")
    text = text.replace("RegistryObject<", "DeferredHolder<")
    text = re.sub(r"DeferredHolder<([^,<>]+)>", r"DeferredHolder<\1, \1>", text)
    text = text.replace("@Mod.EventBusSubscriber", "@EventBusSubscriber")
    text = text.replace("Mod.EventBusSubscriber.Bus", "EventBusSubscriber.Bus")
    text = re.sub(
        r"new ResourceLocation\((?:[^)(]|\([^)]*\))*\)",
        lambda match: convert_rl(match.group(0)),
        text,
    )
    text = text.replace(
        "new ArmorType(ResourceLocation.",
        "new ArmorType(com.magistuarmory.item.armor.ArmorTypes.ARMOR_MATERIALS, ResourceLocation.",
    )
    text = re.sub(
        r', Platform\.isForge\(\) \? "forge:ingots/([^"]+)" : "c:[^"]+"',
        r', false, "c:ingots/\1"',
        text,
    )
    text = re.sub(
        r'new ModItemTier\("([^"]+)", (\d+),',
        lambda match: f'new ModItemTier("{match.group(1)}", {levels[match.group(2)]},',
        text,
    )
    if "@EventBusSubscriber" in text and "import net.neoforged.fml.common.EventBusSubscriber;" not in text:
        text = text.replace(
            "import net.neoforged.fml.common.Mod;\n",
            "import net.neoforged.fml.common.EventBusSubscriber;\nimport net.neoforged.fml.common.Mod;\n",
        )
    if "BlockTags." in text and "import net.minecraft.tags.BlockTags;" not in text:
        text = re.sub(r"(package [^;]+;\n)", r"\1\nimport net.minecraft.tags.BlockTags;\n", text, count=1)
    if text != original:
        path.write_text(text, encoding="utf-8")
        print("updated", path)
