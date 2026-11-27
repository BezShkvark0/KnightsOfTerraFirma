package net.witchkings.knightsofterrafirma.misc;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.witchkings.knightsofterrafirma.KnightsOfTerraFirma;

public class RPLoader {
    public static void init(IEventBus bus) {
        bus.addListener(RPLoader::onPackFinder);
    }

    public static void onPackFinder(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        var modFile = ModList.get()
                .getModContainerById(KnightsOfTerraFirma.MODID)
                .orElseThrow()
                .getModInfo()
                .getOwningFile()
                .getFile();
        var root = modFile.findResource("");
        var info = new PackLocationInfo(
                "kotf_data",
                Component.literal("KOTF Resources"),
                PackSource.BUILT_IN,
                Optional.empty());
        var pack = Pack.readMetaAndCreate(
                info,
                BuiltInPackSource.fromName(location -> new PathPackResources(location, root)),
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.TOP, false));
        if (pack != null) {
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }
}
