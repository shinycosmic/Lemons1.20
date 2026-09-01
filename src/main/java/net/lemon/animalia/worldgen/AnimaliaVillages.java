package net.lemon.animalia.worldgen;

import com.mojang.datafixers.util.Pair;
import net.lemon.animalia.Animalia;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Animalia.MODID)
public class AnimaliaVillages {
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> pools = event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processors = event.getServer().registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> empty = processors.getHolderOrThrow(
                ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("minecraft", "empty")));
//        for (String type : List.of("plains", "desert", "savanna", "snowy", "taiga")) {
        for (String type : List.of("desert")) {
            addToPool(pools, villagePool(type, "houses"), villagePiece(type + "_aquarist_1"), empty, 65);
        }
    }

    private static void addToPool(Registry<StructureTemplatePool> pools, ResourceLocation poolId, String pieceId,
                                  Holder<StructureProcessorList> processors, int weight) {
        StructureTemplatePool pool = pools.get(poolId);
        if (pool == null) {
            return;
        }
        SinglePoolElement piece = StructurePoolElement.legacy(pieceId, processors).apply(StructureTemplatePool.Projection.RIGID);
        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }
        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(pool.rawTemplates);
        rawTemplates.add(new Pair<>(piece, weight));
        pool.rawTemplates = rawTemplates;
    }

    private static ResourceLocation villagePool(String type, String pool) {
        return new ResourceLocation("minecraft", "village/" + type + "/" + pool);
    }

    private static String villagePiece(String name) {
        return Animalia.MODID + ":village/" + name;
    }
}
