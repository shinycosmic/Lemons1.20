package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Flees to a climbable refuge and climbs it. With no refuge in range it falls through to the plain
 * flee of its parent, so the mob is never stranded by a failed search.
 */
public class ClimbPanicGoal extends AnimaliaLandBase.LandPanicGoal {

    private final Predicate<BlockState> refuge;
    private final int range;
    private final int climbHeight;

    @Nullable
    private BlockPos targetPos;
    @Nullable
    private Path climbPath;

    public ClimbPanicGoal(AnimaliaLandBase mob, double speedMult, int fleeLength, double proximityRange, int range, int climbHeight, TagKey<Block> tag) {
        this(mob, speedMult, fleeLength, proximityRange, range, climbHeight, state -> state.is(tag));
    }

    public ClimbPanicGoal(AnimaliaLandBase mob, double speedMult, int fleeLength, double proximityRange, int range, int climbHeight, Block block) {
        this(mob, speedMult, fleeLength, proximityRange, range, climbHeight, state -> state.is(block));
    }

    private ClimbPanicGoal(AnimaliaLandBase mob, double speedMult, int fleeLength, double proximityRange, int range, int climbHeight, Predicate<BlockState> refuge) {
        super(mob, speedMult, fleeLength, proximityRange);
        this.range = range;
        this.climbHeight = climbHeight;
        this.refuge = refuge;
    }

    @Override
    public boolean canUse() {
        this.mob.setWantsToClimb(false);
        this.targetPos = null;
        this.climbPath = null;
        if (!super.canUse()) {
            return false;
        }
        this.targetPos = this.findRefuge();
        return true;
    }

    /**
     * Intent is set before the reachability pre-check: the evaluator reads it in {@code prepare}, so
     * checking first would path with the wall withheld and refuse every climb.
     */
    @Nullable
    private BlockPos findRefuge() {
        Level level = this.mob.level();
        BlockPos found = BlockPos.findClosestMatch(this.mob.blockPosition(), this.range, this.range,
                pos -> this.refuge.test(level.getBlockState(pos))).orElse(null);
        if (found == null) {
            return null;
        }

        BlockPos.MutableBlockPos cursor = found.mutable();
        for (int i = 0; i < this.climbHeight; i++) {
            cursor.move(Direction.UP);
            if (!this.refuge.test(level.getBlockState(cursor))) {
                cursor.move(Direction.DOWN);
                break;
            }
        }

        BlockPos perch = this.perchBeside(level, cursor.immutable());
        if (perch == null) {
            return null;
        }

        this.mob.setWantsToClimb(true);
        this.climbPath = this.mob.getNavigation().createPath(perch, 0);
        if (this.climbPath != null && this.climbPath.canReach()) {
            return perch;
        }
        this.climbPath = null;
        this.mob.setWantsToClimb(false);
        return null;
    }

    @Nullable
    private BlockPos perchBeside(Level level, BlockPos trunk) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockPos side = trunk.relative(face);
            if (!level.getBlockState(side).getCollisionShape(level, side).isEmpty()
                    || !this.mob.hasClimbableFace(level, side)) {
                continue;
            }
            double dist = this.mob.distanceToSqr(side.getX() + 0.5D, side.getY() + 0.5D, side.getZ() + 0.5D);
            if (dist < bestDist) {
                best = side;
                bestDist = dist;
            }
        }
        return best;
    }

    @Override
    protected void moveAway() {
        if (this.targetPos == null) {
            super.moveAway();
            return;
        }
        Path path = this.climbPath != null ? this.climbPath : this.mob.getNavigation().createPath(this.targetPos, 0);
        this.climbPath = null;
        this.mob.getNavigation().moveTo(path, this.speedMult);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setWantsToClimb(false);
        this.targetPos = null;
        this.climbPath = null;
    }
}