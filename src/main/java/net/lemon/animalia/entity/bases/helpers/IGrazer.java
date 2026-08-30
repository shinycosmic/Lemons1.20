package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IGrazer {

    boolean isGrazableBlock(BlockState state);

    boolean isGrazing();

    void startGrazing();

    default int getGrazeLength() {
        return 20;
    }

    default int getGrazeSearchRange() { return 6; }

    default int getGrazeSearchHeight() { return this.getGrazeSearchRange(); }

    default int getGrazeCount() {
        return 1;
    }

    default double getGrazeReachSqr() {
        return 1.0D;
    }

    //override this to set graze conditions
    default boolean canGraze() {
        return true;
    }

    default boolean wantsToGraze() {
        return false;
    }

    default void clearWantsToGraze() {
    }

    default void onGrazeStart() {
    }

    default void onRandomGraze() {
    }

    default void onGrazeStop() {
    }

    @Nullable
    default BlockPos findGrazeBlock() {
        PathfinderMob mob = (PathfinderMob) this;
        return BlockPos.findClosestMatch(mob.blockPosition(), this.getGrazeSearchRange(), this.getGrazeSearchHeight(),
                pos -> this.isGrazableBlock(mob.level().getBlockState(pos))).orElse(null);
    }
}