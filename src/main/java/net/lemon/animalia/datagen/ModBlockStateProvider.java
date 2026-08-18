package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

import static net.minecraft.core.Direction.*;

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
        glowLichenBaseBlock(ModBlocks.ALGAE_MAT.get(), "algae_mat", modLoc("block/algae_mat"));
        plantWithSpecialModel(ModBlocks.KAEMPFERIA_PULCHRA);
        semiaquaticDoubleBlockPlant(ModBlocks.SAGITTARIA);
        anyAttachBlock(ModBlocks.BLUE_MUSSEL, "mussel1", "solid");
        anyAttachBlock(ModBlocks.SWAN_MUSSEL, "mussel1", "solid");
        anyAttachBlock(ModBlocks.ALGAE_CRUSTED_MUSSEL, "mussel1", "solid");
        anyAttachBlock(ModBlocks.BLACK_MUSSEL, "mussel2", "solid");
        anyAttachBlock(ModBlocks.YELLOW_MUSSEL, "mussel2", "solid");
        anyAttachBlock(ModBlocks.CREAM_MUSSEL, "mussel2", "solid");
        overlayBlock(ModBlocks.TERMITE_MOUND, mcLoc("block/sandstone_top"), modLoc("block/termite_mound"), "cutout_mipped");
        overlayBlock(ModBlocks.RED_TERMITE_MOUND, mcLoc("block/red_sandstone_top"), modLoc("block/termite_mound"), "cutout_mipped");

    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void plantWithSpecialModel(RegistryObject<Block> blockObject) {
        Block block = blockObject.get();
        ModelFile model = cutoutWrapper(ForgeRegistries.BLOCKS.getKey(block).getPath());
        simpleBlock(block, ConfiguredModel.allYRotations(model, 0, false));
    }

    private void semiaquaticDoubleBlockPlant(RegistryObject<Block> blockObject) {
        Block block = blockObject.get();
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ModelFile lower = cutoutWrapper(name);
        ModelFile upper = models().getBuilder(name + "_top")
                .texture("particle", modLoc("block/" + name + "_leaf"))
                .renderType("cutout");
        getVariantBuilder(block)
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                .setModels(ConfiguredModel.allYRotations(lower, 0, false))
                .partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                .setModels(new ConfiguredModel(upper));
    }

    private ModelFile cutoutWrapper(String name) {
        return models().withExistingParent(name + "_cutout", modLoc("block/" + name))
                .renderType("cutout").texture("particle", "#1");
    }

    private ModelFile crossWrapper(String name) {
        return models().cross(name, modLoc("block/" + name)).renderType("cutout");
    }

    private void anyAttachBlock(RegistryObject<Block> blockObject, String parentModel, String renderType) {
        Block block = blockObject.get();
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ModelFile model = models().withExistingParent(name, modLoc("block/"+parentModel))
                        .texture("0", modLoc("block/"+name)).texture("particle", modLoc("block/"+name))
                        .renderType(renderType);
        getVariantBuilder(block).forAllStatesExcept(state -> switch (state.getValue(BlockStateProperties.FACING)) {
            case UP -> ConfiguredModel.allYRotations(model, 0, false);
            case DOWN -> ConfiguredModel.allYRotations(model, 180, false);
            case NORTH -> ConfiguredModel.builder().modelFile(model).rotationX(90).build();
            case SOUTH -> ConfiguredModel.builder().modelFile(model).rotationX(90).rotationY(180).build();
            case EAST -> ConfiguredModel.builder().modelFile(model).rotationX(90).rotationY(90).build();
            case WEST -> ConfiguredModel.builder().modelFile(model).rotationX(90).rotationY(270).build();
        }, BlockStateProperties.WATERLOGGED);
    }

    private void overlayBlock(RegistryObject<Block> blockObject, ResourceLocation base, ResourceLocation overlay, String renderType) {
        Block block = blockObject.get();
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ModelFile model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("base", base)
                .texture("overlay", overlay)
                .texture("particle", base)
                .renderType(renderType)
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, face) -> face.texture("#base").cullface(direction)).end()
                .element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, face) -> face.texture("#overlay").cullface(direction)).end();
        simpleBlock(block, ConfiguredModel.allYRotations(model, 0, false));
        simpleBlockItem(block, model);
    }

    private void glowLichenBaseBlock(Block block, String name, ResourceLocation texture) {
        ModelFile model = models().withExistingParent(name, mcLoc("block/glow_lichen"))
                .texture("glow_lichen", texture)
                .texture("particle", texture)
                .renderType("cutout");

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        builder.part().modelFile(model).rotationX(270).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(UP), true).end();
        builder.part().modelFile(model).rotationX(90).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(DOWN), true).end();
        builder.part().modelFile(model).addModel()
                .condition(MultifaceBlock.getFaceProperty(NORTH), true).end();
        builder.part().modelFile(model).rotationY(180).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(SOUTH), true).end();
        builder.part().modelFile(model).rotationY(270).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(WEST), true).end();
        builder.part().modelFile(model).rotationY(90).uvLock(true).addModel()
                .condition(MultifaceBlock.getFaceProperty(EAST), true).end();

        itemModels().withExistingParent(name, new ResourceLocation("item/generated")).texture("layer0", texture);
    }
}
