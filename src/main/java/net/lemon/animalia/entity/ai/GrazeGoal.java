package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.IFoodEater;
import net.lemon.animalia.entity.bases.helpers.IGrazer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Target a grazable block, move to it, and play the eat animation in graze animation cycles.
 */
public class GrazeGoal<T extends PathfinderMob & IGrazer & IFoodEater> extends Goal {
    private final T mob;
    private final double speedMultiplier;

    private static final int INTERVAL_TICKS = 20;
    private static final int INTERVAL_OFFSET = 15;
    private static final int GRAZE_COOLDOWN = 400;
    private static final double CLOSE_SQR = 6.25D;

    private BlockPos targetPos;
    private int grazeCooldown;
    private int nextSearchTime;
    private int nextGrazeTime;
    private int grazesRemaining;
    private int deadline;
    private boolean randomChance;
    private boolean fed;

    public GrazeGoal(T mob, double speedMultiplier) {
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.grazeCooldown > this.mob.tickCount) {
            return false;
        }
        if (this.mob.isEating() || !this.mob.canGraze()) {
            return false;
        }
        this.randomChance = !this.mob.wantsToGraze();
        if (this.randomChance) {
            if (this.mob.getRandom().nextInt(this.adjustedTickDelay(1000)) != 0) {
                return false;
            }
        }
        if (this.mob.tickCount < this.nextSearchTime) {
            return false;
        }
        this.nextSearchTime = this.mob.tickCount + 20;
        this.targetPos = this.findReachableGrazeBlock();
        return this.targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPos == null || !this.mob.canGraze()) {
            return false;
        }
        if (this.grazesRemaining <= 0 && !this.mob.isGrazing()) {
            return false;
        }
        if (!this.mob.isGrazableBlock(this.mob.level().getBlockState(this.targetPos))) {
            return false;
        }
        return this.mob.tickCount < this.deadline;
    }

    @Override
    public void start() {
        this.mob.clearWantsToGraze();
        this.grazesRemaining = this.mob.getGrazeCount();
        this.nextGrazeTime = 0;
        this.fed = false;
        this.deadline = this.mob.tickCount + 200;
        Vec3 center = Vec3.atCenterOf(this.targetPos);
        this.mob.getNavigation().moveTo(center.x, center.y, center.z, this.speedMultiplier);
        if (this.randomChance) {
            this.mob.onRandomGraze();
        }
        this.mob.onGrazeStart();
    }

    @Override
    public void tick() {
        Vec3 center = Vec3.atCenterOf(this.targetPos);
        this.mob.getLookControl().setLookAt(center.x, center.y, center.z);

        Vec3 mouth = this.mob.position().add(0.0D, this.mob.getBbHeight() * 0.5D, 0.0D);
        double distSqr = center.distanceToSqr(mouth);
        if (distSqr < this.mob.getGrazeReachSqr()) {
            this.mob.getNavigation().stop();
            this.tryGraze();
        } else if (distSqr < CLOSE_SQR) {
            this.mob.getNavigation().stop();
            this.mob.addDeltaMovement(center.subtract(mouth).normalize().scale(0.02D));
        } else if (this.mob.getNavigation().isDone()) {
            this.mob.getNavigation().moveTo(center.x, center.y, center.z, this.speedMultiplier);
        }
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.mob.getNavigation().stop();
        this.grazeCooldown = this.mob.tickCount + GRAZE_COOLDOWN;
        this.mob.onGrazeStop();
    }

    private void tryGraze() {
        if (this.mob.isGrazing() || this.mob.tickCount < this.nextGrazeTime) {
            return;
        }
        this.mob.startGrazing();
        this.mob.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F + (this.mob.getRandom().nextFloat() - this.mob.getRandom().nextFloat()) * 0.4F);
        if (!this.fed) {
            this.mob.heal(2.0f);
            this.mob.ageUpFromFood();
            this.fed = true;
        }
        this.grazesRemaining--;
        this.nextGrazeTime = this.mob.tickCount + this.mob.getGrazeLength() + INTERVAL_TICKS + this.mob.getRandom().nextInt(INTERVAL_OFFSET);
        this.deadline = this.nextGrazeTime + 100;
    }

    private BlockPos findReachableGrazeBlock() {
        BlockPos pos = this.mob.findGrazeBlock();
        if (pos == null) {
            return null;
        }
        Path path = this.mob.getNavigation().createPath(pos, 1);
        return path != null && path.canReach() ? pos : null;
    }
}