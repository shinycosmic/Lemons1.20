package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.IActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanSleep;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepGoal extends Goal {

    private final PathfinderMob mob;
    private final ICanSleep sleeper;
    private final IActivityTime activityTime;
    private int nextWakeCheck;
    private int sleepIdleTicks;
    private int enteringTicks;
    private int exitingTicks;
    private int cooldown;

    public SleepGoal(PathfinderMob mob) {
        this.mob = mob;
        this.sleeper = (ICanSleep) mob;
        this.activityTime = (IActivityTime) mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if(this.mob.isInWater()) { return false; }
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.sleeper.canStartSleeping() || this.activityTime.isActiveTime(this.mob)) {
            return false;
        }
        return this.mob.getRandom().nextInt(100) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.sleeper.getSleepPhase() != ICanSleep.SLEEP_PHASE_NONE;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.exitingTicks = 0;
        this.sleepIdleTicks = 0;
        this.enteringTicks = this.sleeper.getToSleepLength();
        if (this.enteringTicks > 0) {
            this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_ENTERING);
        } else {
            this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_SLEEPING);
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        switch (this.sleeper.getSleepPhase()) {
            case ICanSleep.SLEEP_PHASE_ENTERING:
                this.enteringTicks--;
                if (this.enteringTicks <= 0) {
                    this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_SLEEPING);
                }
                break;
            case ICanSleep.SLEEP_PHASE_SLEEPING:
                this.tickSleeping();
                break;
            case ICanSleep.SLEEP_PHASE_EXITING:
                this.exitingTicks--;
                if (this.exitingTicks <= 0) {
                    this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_NONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {
        this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_NONE);
        this.sleeper.setCurrentSleepIdle(-1);
        this.cooldown = this.sleeper.getSleepCooldown();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void tickSleeping() {
        this.tickSleepIdle();
        if (this.mob.tickCount < this.nextWakeCheck) {
            return;
        }
        this.nextWakeCheck = this.mob.tickCount + 20 + this.mob.getRandom().nextInt(20);
        if (this.activityTime.isActiveTime(this.mob) || this.mob.isInWater()) {
            this.beginExiting();
        }
    }

    private void tickSleepIdle() {
        if (this.sleeper.getCurrentSleepIdle() >= 0) {
            this.sleepIdleTicks--;
            if (this.sleepIdleTicks <= 0) {
                this.sleeper.setCurrentSleepIdle(-1);
            }
            return;
        }
        if (this.sleeper.getSleepIdleCount() > 0
                && this.mob.getRandom().nextInt(this.sleeper.sleepIdleChance()) == 0) {
            int sleepIdleId = this.mob.getRandom().nextInt(this.sleeper.getSleepIdleCount());
            this.sleeper.setCurrentSleepIdle(sleepIdleId);
            this.sleepIdleTicks = this.sleeper.getSleepIdleLength(sleepIdleId);
        }
    }

    private void beginExiting() {
        this.sleeper.setCurrentSleepIdle(-1);
        this.exitingTicks = this.sleeper.getUnSleepLength();
        if (this.exitingTicks > 0) {
            this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_EXITING);
        } else {
            this.sleeper.setSleepPhase(ICanSleep.SLEEP_PHASE_NONE);
        }
    }
}