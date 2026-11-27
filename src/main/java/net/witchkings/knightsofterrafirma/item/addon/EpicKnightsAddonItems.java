package net.witchkings.knightsofterrafirma.item.addon;

import com.magistuarmory.addon.client.render.model.AddonModels;
import com.magistuarmory.addon.item.AddonWeaponTypes;
import com.magistuarmory.addon.item.armor.AddonArmorTypes;
import com.magistuarmory.api.item.ModItemsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.witchkings.knightsofterrafirma.KnightsOfTerraFirma;

public final class EpicKnightsAddonItems extends ModItemsProvider {
    public static final EpicKnightsAddonItems INSTANCE = new EpicKnightsAddonItems();
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KnightsOfTerraFirma.MODID);

    private EpicKnightsAddonItems() {
        super(KnightsOfTerraFirma.MODID);
    }

    public static void init(IEventBus bus) {
        OptionalGearRegistrar.registerWeapons(INSTANCE, AddonWeaponTypes.class, GearCatalog.ADDON_WEAPONS);
        OptionalGearRegistrar.registerArmor(INSTANCE, AddonArmorTypes.class, GearCatalog.ADDON_ARMOR);
        INSTANCE.init();
        TABS.register("kotf_addon", () -> CreativeModeTab.builder()
                .title(Component.translatable("creativetab.kotf_addon"))
                .icon(() -> new ItemStack(INSTANCE.weaponItems.getFirst().get()))
                .displayItems((parameters, output) -> {
                    INSTANCE.weaponItems.forEach(item -> output.accept(item.get()));
                    INSTANCE.armorItems.forEach(item -> output.accept(item.get()));
                })
                .build());
        TABS.register(bus);
    }

    public static void initClient() {
        AddonModels models = AddonModels.INSTANCE;
        if (models != null) {
            models.hashCode();
        }
    }
}
