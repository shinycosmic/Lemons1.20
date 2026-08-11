package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.block.AlgaeMatBlock;
import net.lemon.animalia.block.FilterTrapBlock;
import net.lemon.animalia.block.MoundNestBlock;
import net.lemon.animalia.block.SemiaquaticPlantBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Animalia.MODID);

    public static final RegistryObject<Block> FILTER_TRAP = registerBlock("filter_trap", () -> new FilterTrapBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> MOUND_NEST = registerBlock("mound_nest", () -> new MoundNestBlock(BlockBehaviour.Properties.copy(Blocks.SAND).noOcclusion().noLootTable()));
    public static final RegistryObject<Block> ALGAE_MAT = registerBlock("algae_mat", () -> new AlgaeMatBlock(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).noOcclusion().noCollission().sound(SoundType.VINE).lightLevel(state -> 0)));

    public static final RegistryObject<Block> KAEMPFERIA_PULCHRA = registerFlowerBlock("kaempferia_pulchra", MobEffects.FIRE_RESISTANCE);
    public static final RegistryObject<Block> SAGITTARIA = registerBlock("sagittaria", () -> new SemiaquaticPlantBlock(BlockBehaviour.Properties.copy(Blocks.TALL_GRASS).noOcclusion().noCollission()));






    //HELPER METHODS BELOW
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Block> registerFlowerBlock(String name, MobEffect effect) {
        RegistryObject<Block> toReturn = BLOCKS.register(name, () -> new FlowerBlock(() -> effect, 5, BlockBehaviour.Properties.copy(Blocks.ALLIUM).noOcclusion().noCollission()));
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
