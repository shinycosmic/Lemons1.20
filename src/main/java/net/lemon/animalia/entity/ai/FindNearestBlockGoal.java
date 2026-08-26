package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.IActivityTime;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.function.Predicate;

public class FindNearestBlockGoal extends Goal {
    protected final PathfinderMob mob;
    private final IActivityTime activityTime;
    private final Predicate<BlockState> target;
    private final double speedMult;
    private final int searchRange;
    private final int chance;
    private BlockPos targetPos;
    private int nextSearchTime;
    private int cooldown;
    private int deadline;

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, TagKey<Block> tag, int chance) {
        this(mob, speedMult, searchRange, state -> state.is(tag), chance, false);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Block block, int chance) {
        this(mob, speedMult, searchRange, state -> state.is(block), chance, false);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, TagKey<Block> tag) {
        this(mob, speedMult, searchRange, state -> state.is(tag), 0, true);
    }

    public FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Block block) {
        this(mob, speedMult, searchRange, state -> state.is(block), 0, true);
    }

    protected FindNearestBlockGoal(PathfinderMob mob, double speedMult, int searchRange, Predicate<BlockState> target, int chance, boolean timegate) {
        this.mob = mob;
        this.speedMult = speedMult;
        this.searchRange = searchRange;
        this.target = target;
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
        if (this.mob.blockPosition().equals(this.targetPos)) {
            this.onArrival();
            return false;
        }
        if (!this.isTarget(this.targetPos)) {
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
        this.mob.getNavigation().moveTo(this.mob.getNavigation().createPath(this.targetPos, 0), this.speedMult);
    }

    private BlockPos findNearestBlock() {
        BlockPos pos = BlockPos.findClosestMatch(this.mob.blockPosition(), this.searchRange, this.searchRange, this::isTarget).orElse(null);
        if (pos == null) {
            return null;
        }
        Path path = this.mob.getNavigation().createPath(pos, 0);
        return path != null && path.canReach() ? pos.immutable() : null;
    }

    protected boolean isTarget(BlockPos pos) {
        BlockState state = this.mob.level().getBlockState(pos);
        return this.target.test(state) && state.getCollisionShape(this.mob.level(), pos).isEmpty();
    }

    protected void onArrival() {
    }
}