package net.witchkings.knightsofterrafirma.misc;


import com.magistuarmory.EpicKnights;
import com.magistuarmory.antiquelegacy.item.AddonItems;
import com.magistuarmory.item.DyeableArmorDecorationItem;
import com.magistuarmory.item.MedievalShieldItem;
import com.magistuarmory.item.MedievalWeaponItem;
import com.magistuarmory.item.ModItems;
import com.magistuarmory.item.armor.DyeableWearableArmorDecorationItem;
import com.magistuarmory.item.armor.MedievalArmorItem;
import com.magistuarmory.item.armor.WearableArmorDecorationItem;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.witchkings.knightsofterrafirma.KnightsOfTerraFirma;

import net.witchkings.knightsofterrafirma.item.AntiqueItems;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.network.chat.Component;
import net.witchkings.knightsofterrafirma.item.ModdedItems;
import net.witchkings.knightsofterrafirma.item.ModelledItems;

import java.util.Collections;

import static com.magistuarmory.misc.ModCreativeTabs.ARMOR_DECORATIONS_RESOURCE_KEY;
import static net.witchkings.knightsofterrafirma.item.AntiqueItems.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AntiqueTab {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KnightsOfTerraFirma.MODID);
    public static final ResourceKey<CreativeModeTab> ARMOR_DECORATIONS_RESOURCE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(EpicKnights.ID, "armor_decorations"));
    public static final ResourceKey<CreativeModeTab> PARTICULAR_WEAPONS_RESOURCE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(EpicKnights.ID, "particular_weapons"));

    @SubscribeEvent
    public static void buildTabContents(BuildCreativeModeTabContentsEvent tabData) {
        if (Platform.isModLoaded("antiquelegacy")) {
            if (tabData.getTabKey() == CreativeTab.KOTF_ARMOR.getKey()) {
                for (RegistrySupplier<? extends MedievalArmorItem> item: AntiqueItems.listArmorBismuthBronze) {
                    tabData.insertBefore(ModelledItems.SILVER_STEEL_ARMET_WITH_PLUME_SUPPLIER.get(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
                for (RegistrySupplier<? extends MedievalArmorItem> item: AntiqueItems.listArmorBlackBronze) {
                    tabData.insertBefore(ModelledItems.SILVER_STEEL_ARMET_WITH_PLUME_SUPPLIER.get(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
    @SubscribeEvent
    public static void bildTabContents(BuildCreativeModeTabContentsEvent tabData) {
        if (Platform.isModLoaded("antiquelegacy")) {
            if (tabData.getTabKey() == CreativeTab.KOTF_PARTS.getKey()) {
                for (RegistrySupplier<Item> item: AntiqueItems.listBronzeParts) {
                    tabData.accept(item.get());}
                for (RegistrySupplier<Item> item: AntiqueItems.listBismuthBronzeParts) {
                    tabData.accept(item.get());
                }
                for (RegistrySupplier<Item> item: AntiqueItems.listBlackBronzeParts) {
                    tabData.accept(item.get());}

                for (RegistrySupplier<Item> item: AntiqueItems.listIronParts) {
                    tabData.accept(item.get());}

            }
            else if (tabData.getTabKey() == CreativeTab.KOTF_ARMOR_PARTS.getKey()) {
                for (RegistrySupplier<Item> item : listArmorParts) {
                    tabData.insertBefore(ModdedItems.BASCINET_VISOR.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
    @SubscribeEvent
    public static void buildTabContentsDeco(BuildCreativeModeTabContentsEvent tabData) {
        if (Platform.isModLoaded("antiquelegacy")) {
            if (tabData.getTabKey() == ARMOR_DECORATIONS_RESOURCE_KEY) {
                tabData.accept(BISMUTH_BRONZE_HORNS_DECORATION.get());
                tabData.accept(BISMUTH_BRONZE_SMALL_WINGS_DECORATION.get());
                for (RegistrySupplier<DyeableArmorDecorationItem> item: AntiqueItems.listDecoBismuthBronze) {
                    tabData.accept(item.get());
                }
                tabData.accept(BISMUTH_BRONZE_LEFT_GLADIATOR_SHOULDER_PAD_DECORATION.get());
                tabData.accept(BISMUTH_BRONZE_RIGHT_GLADIATOR_SHOULDER_PAD_DECORATION.get());
                tabData.accept(BISMUTH_BRONZE_LEFT_HAND_MANIKA_DECORATION.get());
                tabData.accept(BISMUTH_BRONZE_RIGHT_HAND_MANIKA_DECORATION.get());
                tabData.accept(BLACK_BRONZE_HORNS_DECORATION.get());
                tabData.accept(BLACK_BRONZE_SMALL_WINGS_DECORATION.get());
                for (RegistrySupplier<DyeableArmorDecorationItem> item: AntiqueItems.listDecoBlackBronze) {
                    tabData.accept(item.get());
                }
                tabData.accept(BLACK_BRONZE_LEFT_GLADIATOR_SHOULDER_PAD_DECORATION.get());
                tabData.accept(BLACK_BRONZE_RIGHT_GLADIATOR_SHOULDER_PAD_DECORATION.get());
                tabData.accept(BLACK_BRONZE_LEFT_HAND_MANIKA_DECORATION.get());
                tabData.accept(BLACK_BRONZE_RIGHT_HAND_MANIKA_DECORATION.get());
                for (RegistrySupplier<WearableArmorDecorationItem> item: AntiqueItems.listDeco) {
                    tabData.insertAfter(AddonItems.GOLDEN_LAUREL_WREATH_DECORATION.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
    @SubscribeEvent
    public static void builtTabContents(BuildCreativeModeTabContentsEvent tabData) {
        if (Platform.isModLoaded("antiquelegacy")) {
            if (tabData.getTabKey() == CreativeTab.KOTF_SHIELDS.getKey()) {
                for (RegistrySupplier<MedievalShieldItem> item: AntiqueItems.listShields) {
                    tabData.insertBefore(ModelledItems.ELLIPTICAL_SHIELD.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
    @SubscribeEvent
    public static void buildedTabContents(BuildCreativeModeTabContentsEvent tabData) {
        if (Platform.isModLoaded("antiquelegacy")) {
            if (tabData.getTabKey() == CreativeTab.KOTF_WEAPONS.getKey()) {
                for (RegistrySupplier<MedievalWeaponItem> item: listBismuthBronzeWeapons) {
                    tabData.insertBefore(ModdedItems.BISMUTH_BRONZE_BASTARDSWORD.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
                for (RegistrySupplier<MedievalWeaponItem> item: listBlackBronzeWeapons) {
                    tabData.insertBefore(ModdedItems.BLACK_BRONZE_BASTARDSWORD.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            } else if (tabData.getTabKey() == PARTICULAR_WEAPONS_RESOURCE_KEY) {
                for (RegistrySupplier<MedievalWeaponItem> item: listBronzeWeapons) {
                    tabData.insertBefore(AddonItems.IRON_ANTIQUE_DAGGER.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
                tabData.insertBefore(AddonItems.IRON_ANTIQUE_DAGGER.get().getDefaultInstance(), IRON_BLACK_BRONZE_ANTIQUE_DAGGER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                tabData.insertBefore(IRON_BLACK_BRONZE_ANTIQUE_DAGGER.get().getDefaultInstance(), IRON_BLACK_BRONZE_ANTIQUE_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                tabData.insertBefore(IRON_BLACK_BRONZE_ANTIQUE_SWORD.get().getDefaultInstance(), IRON_BLACK_BRONZE_CELTIC_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                tabData.insertBefore(IRON_BLACK_BRONZE_CELTIC_SWORD.get().getDefaultInstance(), IRON_BISMUTH_BRONZE_ANTIQUE_DAGGER.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                tabData.insertBefore(IRON_BISMUTH_BRONZE_ANTIQUE_DAGGER.get().getDefaultInstance(), IRON_BISMUTH_BRONZE_ANTIQUE_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                tabData.insertBefore(IRON_BISMUTH_BRONZE_ANTIQUE_SWORD.get().getDefaultInstance(), IRON_BISMUTH_BRONZE_CELTIC_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
    @SubscribeEvent
    public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
        if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            tabData.insertAfter(AddonItems.IRON_LAMELLAR_ROWS.get().getDefaultInstance(), SMALL_BLACK_BRONZE_PLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(SMALL_BLACK_BRONZE_PLATE.get().getDefaultInstance(), BLACK_BRONZE_PLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(BLACK_BRONZE_PLATE.get().getDefaultInstance(), BLACK_BRONZE_LAMELLAR_ROWS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(BLACK_BRONZE_LAMELLAR_ROWS.get().getDefaultInstance(), BLACK_BRONZE_NUGGET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(BLACK_BRONZE_NUGGET.get().getDefaultInstance(), SMALL_BISMUTH_BRONZE_PLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(SMALL_BISMUTH_BRONZE_PLATE.get().getDefaultInstance(), BISMUTH_BRONZE_PLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(BISMUTH_BRONZE_PLATE.get().getDefaultInstance(), BISMUTH_BRONZE_LAMELLAR_ROWS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            tabData.insertAfter(BISMUTH_BRONZE_LAMELLAR_ROWS.get().getDefaultInstance(), BISMUTH_BRONZE_NUGGET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
//for some reason had to put it there:
    @SubscribeEvent
    public static void buildTabContentsDecoCrowns(BuildCreativeModeTabContentsEvent tabData) {
        if (tabData.getTabKey() == ARMOR_DECORATIONS_RESOURCE_KEY) {
            for (RegistrySupplier<DyeableWearableArmorDecorationItem> item: ModelledItems.listCrownDeco) {
                tabData.insertAfter(ModItems.CROWN_DECORATION.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
            for (RegistrySupplier<WearableArmorDecorationItem> item: ModelledItems.listMinicrownDeco) {
                tabData.insertAfter(ModItems.MINICROWN_DECORATION.get().getDefaultInstance(), item.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }

}


