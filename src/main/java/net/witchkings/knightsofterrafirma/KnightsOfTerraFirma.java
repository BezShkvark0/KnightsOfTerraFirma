package net.witchkings.knightsofterrafirma;

import com.magistuarmory.config.ArmorConfig;
import com.magistuarmory.config.GeneralConfig;
import com.magistuarmory.config.ModConfig;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.witchkings.knightsofterrafirma.client.AntiqueModels;
import net.witchkings.knightsofterrafirma.client.ModModel;
import net.witchkings.knightsofterrafirma.config.ConfigAntique;
import net.witchkings.knightsofterrafirma.config.ConfigArmor;
import net.witchkings.knightsofterrafirma.config.ConfigMain;
import net.witchkings.knightsofterrafirma.item.*;
import net.witchkings.knightsofterrafirma.item.addon.EpicKnightsAddonItems;
import net.witchkings.knightsofterrafirma.item.addon.OptionalGearRegistrar;
import net.witchkings.knightsofterrafirma.item.addon.SlavicArmoryItems;
import net.witchkings.knightsofterrafirma.misc.*;


import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KnightsOfTerraFirma.MODID)
public class KnightsOfTerraFirma {
    public static boolean BC_or_EF_installed;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "knightsofterrafirma";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ConfigMain CONFIG;
    public static ConfigArmor CONFIG_ARMOR;
    public static ConfigAntique CONFIG_ANTIQUE;

    public KnightsOfTerraFirma(IEventBus modEventBus) {
        AutoConfig.register(ConfigMain.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        CONFIG = AutoConfig.getConfigHolder(ConfigMain.class).getConfig();
        CONFIG_ARMOR = CONFIG.armor;
        CONFIG_ANTIQUE = CONFIG.antique_armor;
        LOGGER.info("Config loaded");
        EKArmor.init(CONFIG_ARMOR);
        ALArmor.init(CONFIG_ANTIQUE);
        CreativeTab.register(modEventBus);
        ModdedItems.INSTANCE.init();
        ModdedItems.weaponRegistry();
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
        ModelledItems.INSTANCE.init();
        ModelledItems.shieldRegistry();
        if (Platform.getEnv() == Dist.CLIENT)
            ModModel.INSTANCE.init(ModelledItems.INSTANCE);
        RPLoader.init(modEventBus);

        ExtendedCreativeTabs.register(modEventBus);

        if (Platform.isModLoaded("antiquelegacy")) {
            AntiqueItems.INSTANCE.init();
            AntiqueItems.weaponRegistry();
            if (Platform.getEnv() == Dist.CLIENT)
                AntiqueModels.INSTANCE.init(AntiqueItems.INSTANCE);

        }
        if (Platform.isModLoaded("antiquelegacy")) {
            AntiqueTab.register(modEventBus);
        }
        if (Platform.isModLoaded("dfc")) {
            AdditionalItems.INSTANCE.init();
            if (Platform.getEnv() == Dist.CLIENT)
                ModModel.INSTANCE.init(AdditionalItems.INSTANCE);

        }
        if (Platform.isModLoaded("dfc")) {
            Additions.register(modEventBus);
        }
        if (Platform.isModLoaded("magistuarmoryaddon")) {
            EpicKnightsAddonItems.init(modEventBus);
            if (Platform.getEnv() == Dist.CLIENT) {
                EpicKnightsAddonItems.initClient();
            }
        }
        if (Platform.isModLoaded("slavicarmory")) {
            SlavicArmoryItems.init(modEventBus);
            if (Platform.getEnv() == Dist.CLIENT) {
                SlavicArmoryItems.initClient();
            }
        }
        if (Platform.isModLoaded("magistuarmoryaddon") || Platform.isModLoaded("slavicarmory")) {
            OptionalGearRegistrar.registerMaterials();
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code


        LOGGER.info("Weapons ready, my lord!");


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Call the banners! We enter the fray!");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("Call the banners! We enter the fray!");
            prepareWthit();
        }

        // WTHIT loads its tooltip service from the thread context class loader. The creative
        // search index builds tooltips on a background thread, whose loader cannot see that service.
        private static void prepareWthit() {
            if (!ModList.get().isLoaded("wthit")) {
                return;
            }
            String serviceName = "mcp.mobius.waila.api.__internal__.IClientApiService";
            Thread thread = Thread.currentThread();
            ClassLoader previous = thread.getContextClassLoader();
            try {
                Class<?> service = Class.forName(serviceName, false, KnightsOfTerraFirma.class.getClassLoader());
                thread.setContextClassLoader(service.getClassLoader());
                Class.forName(serviceName, true, service.getClassLoader());
            } catch (ReflectiveOperationException | ExceptionInInitializerError exception) {
                LOGGER.warn("Could not prepare WTHIT tooltips", exception);
            } finally {
                thread.setContextClassLoader(previous);
            }
        }
    }

    public static void checkBetterCombatOrEpicFightInstalled() {
        BC_or_EF_installed = Platform.isModLoaded("bettercombat") || Platform.isModLoaded("epicfight");
    }
    static {

    }
}
