package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FishHideGoal extends Goal {
    private final AnimaliaBreedableWater mob;
    private final ActivityTime activeTime;

    private static final float INACTIVE_CHANCE = 0.8F;
    private static final float ACTIVE_CHANCE = 0.05F;

    public FishHideGoal(AnimaliaBreedableWater mob) {
        this(mob, ActivityTime.NONE);
    }

    public FishHideGoal(AnimaliaBreedableWater mob, ActivityTime activeTime) {
        this.mob = mob;
        this.activeTime = activeTime;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!mob.canStartHiding() || mob.isBaby()) {
            return false;
        }
        return passesTimeGate();
    }

    private boolean passesTimeGate() {
        if (activeTime == ActivityTime.NONE) {
            return true;
        }

        boolean isDaytime = mob.level().isDay();
        boolean isInactivePeriod;

        switch (activeTime) {
            case NOCTURNAL:
                isInactivePeriod = isDaytime;
                break;
            case DIURNAL:
                isInactivePeriod = !isDaytime;
                break;
            default:
                return true;
        }

        float chance = isInactivePeriod ? INACTIVE_CHANCE : ACTIVE_CHANCE;
        return mob.getRandom().nextFloat() < chance;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.wantsToHide || mob.isHiding();
    }

    @Override
    public boolean isInterruptable() {
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

            mob.setYRot(mob.yRotO);
            mob.yBodyRot = mob.yBodyRotO;
            mob.yHeadRot = mob.yHeadRotO;
            mob.setXRot(mob.xRotO);
        }
    }

}