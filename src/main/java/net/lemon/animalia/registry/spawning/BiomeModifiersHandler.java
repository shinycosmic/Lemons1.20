package net.lemon.animalia.registry.spawning;

import com.mojang.serialization.Codec;
import net.lemon.animalia.Animalia;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BiomeModifiersHandler {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Animalia.MODID);

    public static final RegistryObject<Codec<MobSpawnBiomeModifier>> CONFIG_SPAWNS =
            SERIALIZERS.register("config_spawns", () -> MobSpawnBiomeModifier.CODEC);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}