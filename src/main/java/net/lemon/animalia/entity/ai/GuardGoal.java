package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.ICanGuard;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GuardGoal extends Goal {

    private final PathfinderMob mob;
    private final ICanGuard guarder;
    private final double guardRange;
    private final Predicate<LivingEntity> threatPredicate;
    private final TargetingConditions targetingConditions;
    private static final int SCAN_INTERVAL = 5;
    private int nextThreatScan;
    private LivingEntity threatTarget;
    private int enteringTicks;
    private int exitingTicks;
    private int cooldown;

    public GuardGoal(PathfinderMob mob, double guardRange) {
        this(mob, guardRange, entity -> entity instanceof Player player && !player.isCreative());
    }

    public GuardGoal(PathfinderMob mob, double guardRange, Predicate<LivingEntity> threatPredicate) {
        this.mob = mob;
        this.guarder = (ICanGuard) mob;
        this.guardRange = guardRange;
        this.threatPredicate = threatPredicate.and(EntitySelector.NO_SPECTATORS::test);
        this.targetingConditions = TargetingConditions.forNonCombat().range(guardRange).selector(this.threatPredicate::test);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.guarder.canStartGuarding() || this.mob.isBaby()) {
            return false;
        }
        return this.activationSatisfied();
    }

    @Override
    public boolean canContinueToUse() {
        return this.guarder.getGuardPhase() != ICanGuard.GUARD_PHASE_NONE;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.exitingTicks = 0;
        this.enteringTicks = this.guarder.getToGuardLength();
        if (this.enteringTicks > 0) {
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_ENTERING);
        } else {
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_GUARDING);
        }
        if (this.guarder.getGuardSound() != null) {
            this.mob.playSound(this.guarder.getGuardSound());
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        switch (this.guarder.getGuardPhase()) {
            case ICanGuard.GUARD_PHASE_ENTERING:
                this.enteringTicks--;
                if (this.enteringTicks <= 0) {
                    this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_GUARDING);
                }
                break;
            case ICanGuard.GUARD_PHASE_GUARDING:
                this.tickGuarding();
                break;
            case ICanGuard.GUARD_PHASE_EXITING:
                this.tickExiting();
                break;
            default:
                break;
        }
    }

    @Override
    public void stop() {
        this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_NONE);
        this.guarder.clearWantsToGuard();
        this.threatTarget = null;
        this.cooldown = 100;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void tickGuarding() {
        this.threatTarget = this.currentThreat();
        if (this.threatTarget != null) {
            this.guarder.onGuardTick(this.threatTarget);
        }
        if (this.shouldExit()) {
            this.beginExiting();
        }
    }

    private void tickExiting() {
        if (this.activationSatisfied()) {
            this.exitingTicks = 0;
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_GUARDING);
            return;
        }
        this.exitingTicks--;
        if (this.exitingTicks <= 0) {
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_NONE);
        }
    }

    private boolean activationSatisfied() {
        switch (this.guarder.getGuardActivation()) {
            case PROXIMITY:
                return this.currentThreat() != null;
            case ATTACK:
                return this.guarder.wantsToGuard();
            case BOTH:
            default:
                return this.guarder.wantsToGuard() || this.currentThreat() != null;
        }
    }

    private boolean shouldExit() {
        boolean calm = !this.guarder.wantsToGuard();
        switch (this.guarder.getGuardActivation()) {
            case PROXIMITY:
                return this.threatTarget == null;
            case ATTACK:
                return calm || this.attackerGone();
            case BOTH:
            default:
                return calm && this.threatTarget == null;
        }
    }

    private boolean attackerGone() {
        LivingEntity attacker = this.mob.getLastHurtByMob();
        if (attacker == null) {
            return false;
        }
        return !attacker.isAlive() || this.mob.distanceTo(attacker) > this.guardRange;
    }

    private void beginExiting() {
        this.guarder.clearWantsToGuard();
        this.exitingTicks = this.guarder.getUnGuardLength();
        if (this.exitingTicks > 0) {
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_EXITING);
        } else {
            this.guarder.setGuardPhase(ICanGuard.GUARD_PHASE_NONE);
        }
    }

    private LivingEntity findThreat() {
        return this.mob.level().getNearestEntity(
                LivingEntity.class,
                this.targetingConditions,
                this.mob,
                this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                this.mob.getBoundingBox().inflate(this.guardRange));
    }

    private LivingEntity currentThreat() {
        if (this.threatTarget != null && !this.stillThreatening(this.threatTarget)) {
            this.threatTarget = null;
        }
        if (this.mob.tickCount < this.nextThreatScan) {
            return this.threatTarget;
        }
        this.nextThreatScan = this.mob.tickCount + SCAN_INTERVAL + this.mob.getRandom().nextInt(10);
        this.threatTarget = this.findThreat();
        return this.threatTarget;
    }

    private boolean stillThreatening(LivingEntity threat) {
        return threat.isAlive() && this.threatPredicate.test(threat)
                && this.mob.distanceTo(threat) <= this.guardRange;
    }
}