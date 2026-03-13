package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Animalia.MODID);

    /// REGISTER ENTITIES BELOW
    public static final RegistryObject<EntityType<ToothfishEntity>> CHILEANSEABASS = registerEntityType("dissostichus_eleginoides", ToothfishEntity::new, MobCategory.CREATURE, 0.9f, 0.9f);
    public static final RegistryObject<EntityType<ToothfishEntity>> ELEGINOPS_MACLOVINUS = registerEntityType("eleginops_maclovinus", ToothfishEntity::new, MobCategory.CREATURE, 0.9f, 0.9f);


    //Supplier Helper Methods
    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
