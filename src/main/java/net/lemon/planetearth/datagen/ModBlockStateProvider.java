package net.lemon.planetearth.datagen;

import net.lemon.planetearth.PlanetEarth;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PlanetEarth.MODID, existingFileHelper);
    }

    @Override
    /***
     * Add block registries to the below to generate Blockstates
     * ex: blockWithItem(ModBlocks.ITEM_NAME);
     */
    protected void registerStatesAndModels() {

    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
