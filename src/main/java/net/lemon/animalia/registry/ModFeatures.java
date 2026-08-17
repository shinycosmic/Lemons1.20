package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.worldgen.feature.TermiteMoundConfiguration;
import net.lemon.animalia.worldgen.feature.TermiteMoundFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Animalia.MODID);

    public static final RegistryObject<Feature<TermiteMoundConfiguration>> TERMITE_MOUND =
            FEATURES.register("termite_mound", () -> new TermiteMoundFeature(TermiteMoundConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}