package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Animalia.MODID, existingFileHelper);
    }

    @Override
    /***
     * Add block registries to the below to generate Blockstates
     * ex: blockWithItem(ModBlocks.ITEM_NAME);
     */
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.FILTER_TRAP.get(), new ModelFile.UncheckedModelFile(modLoc("block/filter_trap")));
        multifaceBlock(ModBlocks.ALGAE_MAT.get(), "algae_mat", modLoc("block/algae_mat"));
        plantWithSpecialModel(ModBlocks.KAEMPFERIA_PULCHRA);

    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void plantWithSpecialModel(RegistryObject<Block> blockObject) {
        Block block = blockObject.get();
        ModelFile model = cutoutWrapper(ForgeRegistries.BLOCKS.getKey(block).getPath());
        simpleBlock(block, ConfiguredModel.allYRotations(model, 0, false));
    }

    private ModelFile cutoutWrapper(String name) {
        return models().withExistingParent(name + "_cutout", modLoc("block/" + name))
                .renderType("cutout").texture("particle", "#1");
    }

    private void multifaceBlock(Block block, String name, ResourceLocation texture) {
        ModelFile model = models().withExistingParent(name, mcLoc("block/glow_lichen"))
                .texture("glow_lichen", texture)
                .texture("particle", texture)
                .renderType("cutout");

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        builder.part().modelFile(model).rotationX(270).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.UP), true).end();
        builder.part().modelFile(model).rotationX(90).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.DOWN), true).end();
        builder.part().modelFile(model).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.NORTH), true).end();
        builder.part().modelFile(model).rotationY(180).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.SOUTH), true).end();
        builder.part().modelFile(model).rotationY(270).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.WEST), true).end();
        builder.part().modelFile(model).rotationY(90).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(Direction.EAST), true).end();

        itemModels().withExistingParent(name, new ResourceLocation("item/generated")).texture("layer0", texture);
    }
}
