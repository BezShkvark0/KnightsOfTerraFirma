package net.witchkings.knightsofterrafirma.item.addon;

import com.magistuarmory.api.item.ModItemsProvider;
import com.magistuarmory.item.ModItemTier;
import com.magistuarmory.item.WeaponType;
import com.magistuarmory.item.armor.ArmorType;
import com.magistuarmory.item.armor.ArmorTypes;
import dev.architectury.registry.registries.DeferredRegister;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.witchkings.knightsofterrafirma.KnightsOfTerraFirma;
import net.witchkings.knightsofterrafirma.item.ModdedItems;

/**
 * Registers TerraFirmaCraft metal variants of an optional Epic Knights addon.
 * Stats are copied from that addon's own armor types, which already carry the addon's config.
 */
public final class OptionalGearRegistrar {
    public static final List<ModItemTier> METALS = List.of(
            ModdedItems.BISMUTH_BRONZE,
            ModdedItems.BLACK_BRONZE,
            ModdedItems.BLACK_STEEL,
            ModdedItems.BLUE_STEEL,
            ModdedItems.RED_STEEL,
            ModItemTier.COPPER,
            ModItemTier.TIN,
            ModItemTier.BRONZE,
            ModItemTier.IRON,
            ModItemTier.STEEL,
            ModItemTier.SILVER,
            ModItemTier.GOLD);

    private static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(KnightsOfTerraFirma.MODID, Registries.ARMOR_MATERIAL);
    private static final Map<String, ArmorType> CREATED = new HashMap<>();
    private static final Field MODEL_LOCATION = modelLocationField();

    private OptionalGearRegistrar() {
    }

    public static void registerWeapons(ModItemsProvider provider, Class<?> weaponTypes, List<String> names) {
        Map<String, WeaponType> byName = weaponTypes(weaponTypes);
        for (ModItemTier metal : METALS) {
            for (String name : names) {
                WeaponType type = byName.get(name);
                if (type == null || type.isDisabled()) {
                    continue;
                }
                String id = metal.getMaterialName() + "_" + name;
                Item.Properties properties = new Item.Properties();
                if ("lance".equals(name)) {
                    provider.addLanceItem(id, properties, metal, type);
                } else {
                    provider.addMedievalWeaponItem(id, properties, metal, type);
                }
            }
        }
    }

    public static void registerArmor(ModItemsProvider provider, Class<?> armorTypes, List<GearCatalog.ArmorPiece> pieces) {
        Map<String, ArmorType> sources = armorTypes(armorTypes);
        for (ModItemTier metal : METALS) {
            for (GearCatalog.ArmorPiece piece : pieces) {
                ArmorType source = sources.get(piece.sourceType());
                if (source == null) {
                    source = ArmorTypes.KNIGHT;
                }
                if (source.isDisabled()) {
                    continue;
                }
                String key = metal.getMaterialName() + "/" + source.getLocation();
                ArmorType sourceType = source;
                ArmorType type = CREATED.computeIfAbsent(key, ignored -> copy(sourceType, metal));
                provider.addMedievalArmorItem(itemId(metal, piece.pieceId()), type, slot(piece.slot()), new Item.Properties());
            }
        }
    }

    private static ArmorType copy(ArmorType source, ModItemTier metal) {
        // Defense, toughness, and the equip sound live on the armor material. Epic Knights only
        // registers that material later, so the public getters throw while mods are still constructing.
        ArmorMaterial stats = pendingMaterial(source);
        Map<ArmorItem.Type, Integer> defenseBySlot = stats.defense();
        Integer[] durability = new Integer[] {
                source.getDurabilityForType(ArmorItem.Type.BOOTS),
                source.getDurabilityForType(ArmorItem.Type.LEGGINGS),
                source.getDurabilityForType(ArmorItem.Type.CHESTPLATE),
                source.getDurabilityForType(ArmorItem.Type.HELMET)
        };
        Integer[] defense = new Integer[] {
                defenseBySlot.get(ArmorItem.Type.BOOTS),
                defenseBySlot.get(ArmorItem.Type.LEGGINGS),
                defenseBySlot.get(ArmorItem.Type.CHESTPLATE),
                defenseBySlot.get(ArmorItem.Type.HELMET)
        };
        String metalName = metal.getMaterialName();
        ResourceLocation sourceId = source.getLocation();
        return new ArmorType(
                MATERIALS,
                ResourceLocation.fromNamespaceAndPath(
                        KnightsOfTerraFirma.MODID,
                        metalName + "_" + sourceId.getNamespace() + "_" + sourceId.getPath().replace('/', '_')),
                modelLocation(source),
                stats.toughness(),
                stats.knockbackResistance(),
                durability,
                defense,
                stats.enchantmentValue(),
                stats.equipSound(),
                !source.isDisabled(),
                false,
                "c:ingots/" + metalName);
    }

    public static void registerMaterials() {
        MATERIALS.register();
    }

    private static ArmorMaterial pendingMaterial(ArmorType source) {
        Object holder = source.getMaterial();
        try {
            Field supplier = holder.getClass().getDeclaredField("supplier");
            supplier.setAccessible(true);
            @SuppressWarnings("unchecked")
            Supplier<ArmorMaterial> factory = (Supplier<ArmorMaterial>) supplier.get(holder);
            return factory.get();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not read Epic Knights armor material " + source.getLocation(), exception);
        }
    }

    private static String itemId(ModItemTier metal, String pieceId) {
        String body = pieceId;
        if (body.startsWith("steel_")) {
            body = body.substring("steel_".length());
        } else if (body.startsWith("iron_")) {
            body = body.substring("iron_".length());
        }
        return metal.getMaterialName() + "_" + body;
    }

    private static ArmorItem.Type slot(String name) {
        return switch (name) {
            case "boots" -> ArmorItem.Type.BOOTS;
            case "leggings" -> ArmorItem.Type.LEGGINGS;
            case "chestplate" -> ArmorItem.Type.CHESTPLATE;
            default -> ArmorItem.Type.HELMET;
        };
    }

    private static ResourceLocation modelLocation(ArmorType source) {
        try {
            return (ResourceLocation) MODEL_LOCATION.get(source);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not read Epic Knights armor model location", exception);
        }
    }

    private static Field modelLocationField() {
        try {
            Field field = ArmorType.class.getDeclaredField("modellocation");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Epic Knights ArmorType has no modellocation field", exception);
        }
    }

    private static Map<String, WeaponType> weaponTypes(Class<?> type) {
        Map<String, WeaponType> types = new HashMap<>();
        for (Field field : type.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == WeaponType.class) {
                try {
                    types.put(field.getName().toLowerCase(Locale.ROOT), (WeaponType) field.get(null));
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        }
        return types;
    }

    private static Map<String, ArmorType> armorTypes(Class<?> type) {
        Map<String, ArmorType> types = new HashMap<>();
        for (Field field : type.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == ArmorType.class) {
                try {
                    types.put(field.getName().toLowerCase(Locale.ROOT), (ArmorType) field.get(null));
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        }
        return types;
    }
}
