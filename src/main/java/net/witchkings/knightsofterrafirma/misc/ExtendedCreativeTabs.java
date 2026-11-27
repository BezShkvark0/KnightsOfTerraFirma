package net.witchkings.knightsofterrafirma.misc;



import com.magistuarmory.item.ModItems;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.witchkings.knightsofterrafirma.KnightsOfTerraFirma;
import net.witchkings.knightsofterrafirma.item.ModdedItems;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;



@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ExtendedCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KnightsOfTerraFirma.MODID);

    @SubscribeEvent
    public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
        if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Epic Knights already displays its own gilding template. Adding it again throws.
            ItemStack previous = ModItems.GILDING_TEMPLATE.get().getDefaultInstance();
            Item[] extras = new Item[] {
                    ModdedItems.SILVER_TEMPLATE.get(),
                    ModdedItems.STERLING_SILVER_TEMPLATE.get(),
                    ModdedItems.BRASS_TEMPLATE.get(),
                    ModdedItems.ROSE_GOLD_TEMPLATE.get(),
                    ModdedItems.BRONZE_TEMPLATE.get(),
                    ModdedItems.BISMUTH_BRONZE_TEMPLATE.get(),
                    ModdedItems.BLACK_BRONZE_TEMPLATE.get(),
                    ModdedItems.TIN_TEMPLATE.get(),
                    ModdedItems.BLACK_STEEL_CHAINMAIL.get()
            };
            for (Item extra : extras) {
                ItemStack stack = extra.getDefaultInstance();
                tabData.insertAfter(previous, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                previous = stack;
            }
        }
    }
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}