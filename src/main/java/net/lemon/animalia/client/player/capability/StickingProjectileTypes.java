package net.lemon.animalia.client.player.capability;

import net.lemon.animalia.Animalia;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

/** Every stuck-projectile type. Add one line here per new projectile. */
public class StickingProjectileTypes {
    private static final Map<ResourceLocation, StickingProjectileType> TYPES = new HashMap<>();

    public static final StickingProjectileType QUILL = register("quill");

    public static StickingProjectileType register(String name) {
        return register(name, StickingProjectileType.ARROW_DELAY);
    }

    public static StickingProjectileType register(String name, IntUnaryOperator removeDelay) {
        StickingProjectileType type = new StickingProjectileType(new ResourceLocation(Animalia.MODID, name), removeDelay);
        TYPES.put(type.id(), type);
        return type;
    }

    @Nullable
    public static StickingProjectileType get(ResourceLocation id) {
        return TYPES.get(id);
    }
}