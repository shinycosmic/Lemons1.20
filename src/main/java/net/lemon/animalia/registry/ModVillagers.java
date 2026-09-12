package net.lemon.animalia.registry;

import com.google.common.collect.ImmutableSet;
import net.lemon.animalia.Animalia;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, Animalia.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Animalia.MODID);


    public static final RegistryObject<PoiType> AQUARIST_POI = POI_TYPES.register("aquarist_poi",
        () -> new PoiType(ImmutableSet.copyOf(ModBlocks.FILTER_TRAP.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> AQUARIST =
            VILLAGER_PROFESSIONS.register("aquarist", () -> new VillagerProfession("aquarist",
                    holder -> holder.get() == AQUARIST_POI.get(), holder -> holder.get() == AQUARIST_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_FISHERMAN));


    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
