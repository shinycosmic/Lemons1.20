package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.IActivityTime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.function.Predicate;

public class FindNearestBlockGoal extends Goal {
    public enum TargetLocation {
        IN,
        ON,
        SIDE
    }

    protected final PathfinderMob mob;
    private final IActivityTime activityTime;
    private final Predicate<BlockPos> target;
    private final TargetLocation location;
    private final double speedMult;
    private final int searchRange;
    private final int chance;
    private BlockPos targetPos;
    private BlockPos standPos;
    private int nextSearchTime;
    private int cooldown;
    private int deadline;

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, TagKey<Block> tag, TargetLocation location, int chance) {
        this(mob, speedMult, searchRange, pos -> mob.level().getBlockState(pos).is(tag), location, chance, false);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Block block, TargetLocation location, int chance) {
        this(mob, speedMult, searchRange, pos -> mob.level().getBlockState(pos).is(block), location, chance, false);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, TagKey<Block> tag, TargetLocation location) {
        this(mob, speedMult, searchRange, pos -> mob.level().getBlockState(pos).is(tag), location, 0, true);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Block block, TargetLocation location) {
        this(mob, speedMult, searchRange, pos -> mob.level().getBlockState(pos).is(block), location, 0, true);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Predicate<BlockPos> target, TargetLocation location, int chance) {
        this(mob, speedMult, searchRange, target, location, chance, false);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Predicate<BlockPos> target, TargetLocation location) {
        this(mob, speedMult, searchRange, target, location, 0, true);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Predicate<BlockPos> target, TargetLocation location, int chance, boolean timegate) {
        this.mob = mob;
        this.speedMult = speedMult;
        this.searchRange = searchRange;
        this.target = target;
        this.location = location;
        this.chance = chance;
        this.activityTime = timegate ? (IActivityTime) mob : null;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > this.mob.tickCount) {
            return false;
        }
        if (this.mob.tickCount < this.nextSearchTime) {
            return false;
        }
        this.nextSearchTime = this.mob.tickCount + 20 + this.mob.getRandom().nextInt(20);
        if (!this.passCheck()) {
            return false;
        }
        this.targetPos = this.findNearestBlock();
        return this.targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPos == null || this.mob.hurtTime > 0) {
            return false;
        }
        if (this.mob.blockPosition().equals(this.standPos)
                || (this.mob.isInWater() && !this.mob.onGround() && this.standPos.distToCenterSqr(this.mob.position()) < 1.0D)) {
            this.onArrival();
            return false;
        }
        if (!this.isTarget(this.targetPos) || !this.isNoCollisionBlock(this.standPos)) {
            return false;
        }
        return this.mob.tickCount < this.deadline;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.deadline = this.mob.tickCount + this.searchRange * 25;
        this.moveToTarget();
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone()) {
            this.moveToTarget();
        }
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.standPos = null;
        this.mob.getNavigation().stop();
        this.cooldown = this.mob.tickCount + 400;
    }

    protected boolean passCheck() {
        if (this.activityTime != null) {
            return !this.activityTime.isActiveTime(this.mob);
        }
        return this.mob.getRandom().nextInt(this.chance) == 0;
    }

    private void moveToTarget() {
        this.mob.getNavigation().moveTo(this.mob.getNavigation().createPath(this.standPos, 0), this.speedMult);
    }

    private BlockPos findNearestBlock() {
        BlockPos match = BlockPos.findClosestMatch(this.mob.blockPosition(), this.searchRange, this.searchRange, this::isTarget)
                .map(BlockPos::immutable).orElse(null);
        if (match == null) {
            return null;
        }
        BlockPos stand = this.standPos(match);
        Path path = this.mob.getNavigation().createPath(stand, 0);
        if (path == null || !path.canReach()) {
            return null;
        }
        this.standPos = stand;
        return match;
    }

    protected boolean isTarget(BlockPos pos) {
        if (!this.target.test(pos)) {
            return false;
        }
        if (this.location == TargetLocation.ON && !this.isSupport(pos)) {
            return false;
        }
        return this.standPos(pos) != null;
    }

    private BlockPos standPos(BlockPos pos) {
        return switch (this.location) {
            case IN -> this.isNoCollisionBlock(pos) ? pos : null;
            case ON -> this.isNoCollisionBlock(pos.above()) ? pos.above() : null;
            case SIDE -> this.nearestSide(pos);
        };
    }

    private BlockPos nearestSide(BlockPos pos) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(face);
            if (!this.isNoCollisionBlock(side)) {
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

    private boolean isSupport(BlockPos pos) {
        BlockState state = this.mob.level().getBlockState(pos);
        return !state.getCollisionShape(this.mob.level(), pos).isEmpty() || !state.getFluidState().isEmpty();
    }

    private boolean isNoCollisionBlock(BlockPos pos) {
        return this.mob.level().getBlockState(pos).getCollisionShape(this.mob.level(), pos).isEmpty();
    }

    protected void onArrival() {
    }
}