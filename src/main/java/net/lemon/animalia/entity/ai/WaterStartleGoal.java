package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class WaterStartleGoal extends Goal {
    private final AnimaliaBreedableWater mob;
    private final float triggerDistance;
    private final double speedMult;
    private final int maxFleeTicks;
    private final Predicate<LivingEntity> threatPredicate;
    private final TargetingConditions targetingConditions;

    private LivingEntity threat;
    private int fleeTicks;

    public WaterStartleGoal(AnimaliaBreedableWater mob, float triggerDistance, double speedMult) {
        this(mob, triggerDistance, speedMult, 40);
    }

    public WaterStartleGoal(AnimaliaBreedableWater mob, float triggerDistance, double speedMult, int maxFleeTicks) {
        this(mob, triggerDistance, speedMult, maxFleeTicks, entity -> entity instanceof Player player && !player.isCreative());
    }

    public WaterStartleGoal(AnimaliaBreedableWater mob, float triggerDistance, double speedMult, int maxFleeTicks, Predicate<LivingEntity> threatPredicate) {
        this.mob = mob;
        this.triggerDistance = triggerDistance;
        this.speedMult = speedMult;
        this.maxFleeTicks = maxFleeTicks;
        this.threatPredicate = threatPredicate.and(EntitySelector.NO_SPECTATORS::test);
        this.targetingConditions = TargetingConditions.forNonCombat().range(triggerDistance)
                .selector(entity -> this.threatPredicate.test(entity) && this.mob.hasLineOfSight(entity));
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isInWater()) {
            return false;
        }
        this.threat = this.findThreat();
        return this.threat != null;
    }

    @Override
    public void start() {
        this.fleeTicks = 0;
        this.moveAway();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.fleeTicks >= this.maxFleeTicks) return false;
        if (this.threat == null || !this.threat.isAlive()) return false;
        return this.mob.distanceTo(this.threat) < this.triggerDistance * 2.0;
    }

    @Override
    public void tick() {
        this.fleeTicks++;

        if (this.fleeTicks % 10 == 0) {
            this.moveAway();
        }
    }

    @Override
    public void stop() {
        this.threat = null;
        this.fleeTicks = 0;
    }

    public boolean isActive() {
        return this.threat != null && this.fleeTicks > 0 && this.fleeTicks < this.maxFleeTicks;
    }

    private LivingEntity findThreat() {
        return this.mob.level().getNearestEntity(
                LivingEntity.class,
                this.targetingConditions,
                this.mob,
                this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                this.mob.getBoundingBox().inflate(this.triggerDistance));
    }

    private void moveAway() {
        Vec3 mobPos = this.mob.position();
        Vec3 threatPos = this.threat.position();

        Vec3 fleeDir = mobPos.subtract(threatPos).normalize();

        double randX = (this.mob.getRandom().nextDouble() - 0.5) * 0.4;
        double randY = (this.mob.getRandom().nextDouble() - 0.5) * 0.3;
        double randZ = (this.mob.getRandom().nextDouble() - 0.5) * 0.4;
        fleeDir = fleeDir.add(randX, randY, randZ).normalize();

        double distance = 8.0 + this.mob.getRandom().nextDouble() * 4.0;
        Vec3 target = mobPos.add(fleeDir.scale(distance));

        this.mob.getNavigation().moveTo(target.x, target.y, target.z, this.speedMult);
    }
}