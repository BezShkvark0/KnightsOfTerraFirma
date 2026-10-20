package net.witchkings.knightsofterrafirma.item;

import com.magistuarmory.item.armor.ArmorType;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.item.ArmorItem;

public class ArmorModifier {
    public record Stats(int[] durability, int[] defense, float toughness, float knockbackResistance) {
        public int durability(ArmorItem.Type type) {
            return durability[index(type)];
        }

        public int defense(ArmorItem.Type type) {
            return defense[index(type)];
        }

        private static int index(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> 0;
                case LEGGINGS -> 1;
                case CHESTPLATE, BODY -> 2;
                case HELMET -> 3;
            };
        }
    }

    public static final Map<ArmorType, Stats> OVERRIDES = new IdentityHashMap<>();

    public static void modifyArmorType(ArmorType armorType,
                                        int bootsDurability, int leggingsDurability, int chestplateDurability, int helmetDurability,
                                        int bootsDefense, int leggingsDefense, int chestplateDefense, int helmetDefense,
                                        float toughness, float knockbackResistance) {
        OVERRIDES.put(armorType, new Stats(
                new int[]{bootsDurability, leggingsDurability, chestplateDurability, helmetDurability},
                new int[]{bootsDefense, leggingsDefense, chestplateDefense, helmetDefense},
                toughness,
                knockbackResistance));
    }
}
