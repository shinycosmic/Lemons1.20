package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

public class ClimbPanicGoal extends AnimaliaLandBase.LandPanicGoal {

    private final int range;

    @Nullable
    private BlockPos climbPos;

    public ClimbPanicGoal(AnimaliaLandBase mob, double speedMult, int fleeLength, double proximityRange, int range) {
        super(mob, speedMult, fleeLength, proximityRange);
        this.range = range;
    }

    @Override
    public void start() {
        this.mob.setWantsToClimb(true);
        this.climbPos = this.findClimbPos();
        super.start();
    }

    @Nullable
    private BlockPos findClimbPos() {
        if (!this.mob.climbEnabled()) {
            return null;
        }
        Level level = this.mob.level();
        return BlockPos.findClosestMatch(this.mob.blockPosition(), this.range, this.range,
                        pos -> pos.getY() > this.mob.getBlockY()
                                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                                && this.mob.hasClimbableFace(level, pos))
                .map(BlockPos::immutable).orElse(null);
    }

    @Override
    protected void moveAway() {
        if (this.mob.isAttached()) {
            return;
        }
        if (this.mob.blockPosition().equals(this.climbPos)) {
            this.climbPos = null;
        }
        Path path = this.climbPos == null ? null : this.mob.getNavigation().createPath(this.climbPos, 0);
        if (path == null || !path.canReach()) {
            this.climbPos = null;
            super.moveAway();
            return;
        }
        this.mob.getNavigation().moveTo(path, this.speedMult);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setWantsToClimb(false);
        this.climbPos = null;
    }
}