package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AnimaliaSound {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Animalia.MODID);

    public static final RegistryObject<SoundEvent> MUNTIACUS_MUNTJAK_BARK = registerSound("entity.muntiacus_muntjak.bark");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Animalia.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
