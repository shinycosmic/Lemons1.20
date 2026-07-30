package net.lemon.animalia.registry.spawning;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModMobCategories;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSpawnPlacements {

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        for (RegistryObject<EntityType<?>> entry : ModEntities.ENTITY_TYPES.getEntries()) {
            EntityType<?> type = entry.get();
            SpawnPlacements.Type placement = placementFor(type.getCategory());
            if (placement != null) {
                register(event, type, placement);
            }
        }
    }

    @Nullable
    private static SpawnPlacements.Type placementFor(MobCategory category) {
        if (category == ModMobCategories.ANIMALIA_FISH || category == ModMobCategories.ANIMALIA_INVERTEBRATE) {
            return SpawnPlacements.Type.IN_WATER;
        }
        if (category == ModMobCategories.ANIMALIA_LAND) {
            return SpawnPlacements.Type.ON_GROUND;
        }
        if (category == ModMobCategories.ANIMALIA_FLIER) {
            return SpawnPlacements.Type.NO_RESTRICTIONS;
        }
        return null;
    }

    private static <T extends Entity> void register(SpawnPlacementRegisterEvent event, EntityType<T> type, SpawnPlacements.Type placement) {
        event.register(type, placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, reason, pos, random) -> true,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}