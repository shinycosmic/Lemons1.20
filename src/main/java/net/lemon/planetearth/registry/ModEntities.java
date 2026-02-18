package net.lemon.planetearth.registry;

import net.lemon.planetearth.Animalia;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Animalia.MODID);

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
    }

    /// REGISTER ENTITIES BELOW
//    public static final Supplier<EntityType<SnakeEntity>> OCELLATED_PAMPAS_SNAKE = registerEntityType("ocellatedpampassnake", SnakeEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
