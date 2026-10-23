package net.lemon.animalia.client.player.capability;

import net.minecraft.resources.ResourceLocation;
import java.util.function.IntUnaryOperator;

public record StickingProjectileType(ResourceLocation id, IntUnaryOperator removeDelay) {
    public static final IntUnaryOperator ARROW_DELAY = count -> 20 * (30 - count);
}