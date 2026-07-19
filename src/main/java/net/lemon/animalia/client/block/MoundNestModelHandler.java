package net.lemon.animalia.client.block;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.block.MoundNestBlock;
import net.lemon.animalia.client.block.MoundNestBakedModel;
import net.lemon.animalia.registry.ModBlocks;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Animalia.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoundNestModelHandler {

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(ModBlocks.MOUND_NEST.get());
        if (blockId == null) return;

        ModBlocks.MOUND_NEST.get().getStateDefinition().getPossibleStates().forEach(state -> {
            String variantString = "waterlogged=" + state.getValue(MoundNestBlock.WATERLOGGED);
            ModelResourceLocation modelLocation = new ModelResourceLocation(blockId, variantString);

            BakedModel existingModel = modelRegistry.get(modelLocation);
            if (existingModel != null) {
                modelRegistry.put(modelLocation, new MoundNestBakedModel(existingModel));
            }
        });
    }
}