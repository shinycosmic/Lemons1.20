package net.lemon.animalia.client.player.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StickingProjectilesCapabilityProvider implements ICapabilityProvider {

    public static final Capability<StickingProjectilesCapability> STICKING_PROJECTILES =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final StickingProjectilesCapability capability = new StickingProjectilesCapability();
    private final LazyOptional<StickingProjectilesCapability> optional = LazyOptional.of(() -> capability);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == STICKING_PROJECTILES ? optional.cast() : LazyOptional.empty();
    }
}