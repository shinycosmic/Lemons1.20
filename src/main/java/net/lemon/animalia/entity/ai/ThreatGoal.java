package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.interfaces.ICanThreat;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ThreatGoal extends Goal {

    public enum ThreatOutcome {
        NONE,
        FLEE,
        ATTACK
    }

    private final PathfinderMob mob;
    private final ICanThreat threatener;
    private final double threatRange;
    private final int maxThreatTicks;
    private final int exitTicks;
    private final ThreatOutcome outcome;
    private final Predicate<LivingEntity> threatPredicate;
    private final TargetingConditions targetingConditions;

    private LivingEntity threatTarget;
    private int displayTicks;
    private int leavingTicks;
    private int cooldown;

    public ThreatGoal(PathfinderMob mob, double threatRange, int maxThreatTicks, int exitTicks, ThreatOutcome outcome) {
        this(mob, threatRange, maxThreatTicks, exitTicks, outcome, entity -> entity instanceof Player player && !player.isCreative());
    }

    public ThreatGoal(PathfinderMob mob, double threatRange, int maxThreatTicks, int exitTicks, ThreatOutcome outcome, Predicate<LivingEntity> threatPredicate) {
        this.mob = mob;
        this.threatener = (ICanThreat) mob;
        this.threatRange = threatRange;
        this.maxThreatTicks = maxThreatTicks;
        this.exitTicks = exitTicks;
        this.outcome = outcome;
        this.threatPredicate = threatPredicate.and(EntitySelector.NO_SPECTATORS::test);
        this.targetingConditions = TargetingConditions.forNonCombat().range(threatRange).selector(this.threatPredicate::test);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.threatener.canStartThreatening() || this.mob.isBaby()) {
            return false;
        }
        this.threatTarget = this.findThreat();
        return this.threatTarget != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.leavingTicks > 0) {
            return true;
        }
        return this.threatener.getThreatPhase() == ICanThreat.THREAT_PHASE_DISPLAY;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.displayTicks = 0;
        this.leavingTicks = 0;
        this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_DISPLAY);
        if (this.threatener.getThreatSound() != null) {
            this.mob.playSound(this.threatener.getThreatSound());
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.leavingTicks > 0) {
            this.leavingTicks--;
            if (this.leavingTicks <= 0) {
                this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_NONE);
            }
            return;
        }

        this.mob.getNavigation().stop();
        if (this.threatTarget != null && this.threatTarget.isAlive()) {
            this.mob.getLookControl().setLookAt(this.threatTarget, 30.0F, 30.0F);
            this.threatener.onThreatTick(this.threatTarget);
        }

        boolean threatGone = this.threatTarget == null
                || !this.threatTarget.isAlive()
                || this.mob.distanceTo(this.threatTarget) > this.threatRange;

        if (threatGone) {
            this.beginLeaving();
            return;
        }

        this.displayTicks++;
        if (this.displayTicks >= this.maxThreatTicks) {
            this.fireOutcome();
        }
    }

    @Override
    public void stop() {
        this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_NONE);
        this.threatTarget = null;
        this.cooldown = 200;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private LivingEntity findThreat() {
        return this.mob.level().getNearestEntity(
                LivingEntity.class,
                this.targetingConditions,
                this.mob,
                this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                this.mob.getBoundingBox().inflate(this.threatRange));
    }

    private void beginLeaving() {
        if (this.exitTicks > 0) {
            this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_LEAVING);
            this.leavingTicks = this.exitTicks;
        } else {
            this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_NONE);
        }
    }

    private void fireOutcome() {
        LivingEntity threat = this.threatTarget;
        this.threatener.setThreatPhase(ICanThreat.THREAT_PHASE_NONE);
        switch (this.outcome) {
            case FLEE:
                this.threatener.onThreatFlee(threat);
                break;
            case ATTACK:
                this.mob.setTarget(threat);
                break;
            case NONE:
            default:
                break;
        }
    }
}