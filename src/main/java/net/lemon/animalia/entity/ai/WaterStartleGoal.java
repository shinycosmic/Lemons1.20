package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Startle goal for any aquatic mob extending AnimaliaBreedableWater.
 * Triggers when a player (survival/adventure) or hostile mob enters within
 * triggerDistance, causing the mob to flee at high speed.
 * Usage:
 *   this.goalSelector.addGoal(2, new WaterStartleGoal(this, 5.0F, 1.8D));
 *   this.goalSelector.addGoal(2, new WaterStartleGoal(this, 5.0F, 1.8D, 50));
 */
public class WaterStartleGoal extends Goal {
    private final AnimaliaBreedableWater mob;
    private final float triggerDistance;
    private final double speedMultiplier;
    private final int maxFleeTicks;

    private LivingEntity threat;
    private int fleeTicks;

    public WaterStartleGoal(AnimaliaBreedableWater mob, float triggerDistance, double speedMultiplier) {
        this(mob, triggerDistance, speedMultiplier, 40);
    }

    public WaterStartleGoal(AnimaliaBreedableWater mob, float triggerDistance, double speedMultiplier, int maxFleeTicks) {
        this.mob = mob;
        this.triggerDistance = triggerDistance;
        this.speedMultiplier = speedMultiplier;
        this.maxFleeTicks = maxFleeTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.isInWater()) return false;

        // Check for nearby players
        Player nearestPlayer = this.mob.level().getNearestPlayer(this.mob, this.triggerDistance);
        if (nearestPlayer != null && !nearestPlayer.isSpectator() && !nearestPlayer.isCreative()) {
            this.threat = nearestPlayer;
            return true;
        }

        // Check for nearby hostile mobs
        List<Monster> hostiles = this.mob.level().getEntitiesOfClass(Monster.class, this.mob.getBoundingBox().inflate(this.triggerDistance));
        if (!hostiles.isEmpty()) {
            this.threat = hostiles.get(0);
            double closestDist = mob.distanceToSqr(threat);
            for (int i = 1; i < hostiles.size(); i++) {
                double dist = mob.distanceToSqr(hostiles.get(i));
                if (dist < closestDist) {
                    closestDist = dist;
                    this.threat = hostiles.get(i);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        this.fleeTicks = 0;
        moveAway();
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

        // Recalculate flee direction periodically so the mob adjusts if the threat chases
        if (this.fleeTicks % 10 == 0) {
            moveAway();
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

    private void moveAway() {
        Vec3 mobPos = this.mob.position();
        Vec3 threatPos = this.threat.position();

        // Direction vector pointing away from the threat
        Vec3 fleeDir = mobPos.subtract(threatPos).normalize();

        // Add slight randomness for organic-looking movement
        double randX = (this.mob.getRandom().nextDouble() - 0.5) * 0.4;
        double randY = (this.mob.getRandom().nextDouble() - 0.5) * 0.3;
        double randZ = (this.mob.getRandom().nextDouble() - 0.5) * 0.4;
        fleeDir = fleeDir.add(randX, randY, randZ).normalize();

        // Target a point 8-12 blocks away in the flee direction
        double distance = 8.0 + this.mob.getRandom().nextDouble() * 4.0;
        Vec3 target = mobPos.add(fleeDir.scale(distance));

        // Drive MoveControl at speedMultiplier × base attribute speed
        double fleeSpeed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedMultiplier;
        this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, fleeSpeed);
    }
}