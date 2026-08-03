package net.lemon.animalia.entity.bases.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IGrazer {

    boolean isGrazableBlock(BlockState state);

    /** Whether the graze animation is currently playing. */
    boolean isGrazing();

    /** Trigger one playthrough of the graze animation. */
    void startGrazing();

    /** Ticks one graze animation playthrough lasts — override to match the clip. */
    default int getGrazeLength() {
        return 20;
    }

    default int getGrazeSearchRange() {
        return 6;
    }

    default int getGrazeCount() {
        return 3;
    }

    /** Squared distance from the block center at which the graze animation can begin. */
    default double getGrazeReachSqr() {
        return 4.0D;
    }

    /** Override to gate grazing, e.g. isInWater(), !isHiding(). */
    default boolean canGraze() {
        return true;
    }

    /** Whether an external trigger (e.g. a school signal) is urging this entity to graze. */
    default boolean wantsToGraze() {
        return false;
    }

    default void clearGrazeUrge() {
    }

    /** Called when a graze session begins, spontaneous or signaled. */
    default void onGrazeStart() {
    }

    /** Called only when a graze session begins spontaneously. */
    default void onSpontaneousGraze() {
    }

    /** Called when a graze session ends for any reason. */
    default void onGrazeStop() {
    }

    @Nullable
    default BlockPos findGrazeBlock() {
        PathfinderMob mob = (PathfinderMob) this;
        int range = this.getGrazeSearchRange();
        return BlockPos.findClosestMatch(mob.blockPosition(), range, range,
                pos -> this.isGrazableBlock(mob.level().getBlockState(pos))).orElse(null);
    }
}