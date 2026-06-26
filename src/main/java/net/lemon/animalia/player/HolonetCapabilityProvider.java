package net.lemon.animalia.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HolonetCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<HolonetCapability> HOLONET_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final HolonetCapability capability = new HolonetCapability();
    private final LazyOptional<HolonetCapability> optional = LazyOptional.of(() -> capability);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == HOLONET_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        capability.deserializeNBT(tag);
    }

    public void invalidate() {
        optional.invalidate();
    }
}