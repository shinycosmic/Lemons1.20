package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * HidingGoal — a Goal that initiates and locks movement during the hiding behavior.
 *
 * The actual hiding state machine (hideTicks, hideCooldown, transition to ground)
 * remains in AnimaliaBreedableWater.aiStep() so that save/load mid-hide works correctly.
 * This Goal handles:
 *   1. Deciding WHEN to start hiding (canUse → canStartHiding())
 *   2. Locking MOVE + LOOK flags so other goals don't interrupt
 *   3. Stopping navigation each tick while the mob is actively hiding
 */
public class FishHideGoal extends Goal {
    private final AnimaliaBreedableWater mob;

    public FishHideGoal(AnimaliaBreedableWater mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.canStartHiding() && !mob.isBaby();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.wantsToHide || mob.isHiding();
    }

    @Override
    public boolean isInterruptable() {
        // Don't let panic or other goals yank the mob out mid-burrow
        return !mob.isHiding();
    }

    @Override
    public void start() {
        mob.wantsToHide = true;
    }

    @Override
    public void tick() {
        if (mob.isHiding()) {
            mob.getNavigation().stop();
            mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0);
            mob.setDeltaMovement(mob.getDeltaMovement().multiply(0, 1, 0));
        }
    }

    @Override
    public void stop() {
        // State cleanup is handled by aiStep() when hideTicks expires.
        // Nothing extra needed here.
    }
}