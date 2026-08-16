package net.lemon.animalia.datagen.loot;

import net.lemon.animalia.registry.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    /***
     * ALL BLOCKS MUST BE ADDED TO THIS LIST unless you have a .noLootTable() in the registry
     * Below will create a block loot table that allows the blocks to drop itself
     *  this.dropSelf(ModBlocks.ITEM.get());
     *
     *  Ore blocks use something different since they already have a loot table.
     *  CreateCopperLikeOreDrops is a placeholder, but basically look at vanilla and find one you want to reuse or edit.
     *  this.add(ModBlocks.ORE.get(), block -> createCopperLikeOreDrops(ModBlocks.ORE.get(), ModItems.WHAT_DOES_THIS_ORE_DROP.get()));
     */
    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.FILTER_TRAP.get());
        this.add(ModBlocks.ALGAE_MAT.get(), block -> createMultifaceBlockDrops(block, HAS_SHEARS));

        this.dropSelf(ModBlocks.KAEMPFERIA_PULCHRA.get());
        this.add(ModBlocks.SAGITTARIA.get(), block -> this.createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));

        this.dropSelf(ModBlocks.BLUE_MUSSEL.get());
        this.dropSelf(ModBlocks.BLACK_MUSSEL.get());
        this.dropSelf(ModBlocks.SWAN_MUSSEL.get());
        this.dropSelf(ModBlocks.ALGAE_CRUSTED_MUSSEL.get());
        this.dropSelf(ModBlocks.YELLOW_MUSSEL.get());
        this.dropSelf(ModBlocks.CREAM_MUSSEL.get());

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
