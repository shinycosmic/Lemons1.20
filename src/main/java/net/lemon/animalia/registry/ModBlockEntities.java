package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.block.entities.FilterTrapBlockEntity;
import net.lemon.animalia.block.entities.MoundNestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Animalia.MODID);

    public static final RegistryObject<BlockEntityType<FilterTrapBlockEntity>> FILTER_TRAP_BE =
            BLOCK_ENTITIES.register("filter_trap_be", () ->
                    BlockEntityType.Builder.of(FilterTrapBlockEntity::new, ModBlocks.FILTER_TRAP.get()).build(null));

    public static final RegistryObject<BlockEntityType<MoundNestBlockEntity>> MOUND_NEST_BE =
            BLOCK_ENTITIES.register("filter_trap_be", () ->
                    BlockEntityType.Builder.of(MoundNestBlockEntity::new, ModBlocks.MOUND_NEST.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
