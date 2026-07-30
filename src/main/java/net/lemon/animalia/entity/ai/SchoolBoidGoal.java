package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.ai.utils.SchoolDepthBias;
import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Boid schooling for fish. Each tick it adds a clamped steering nudge on top of normal
 * swimming, so schooling fish keep their usual speed, wandering, and pathfinding.
 *
 * <p>Boid forces only act between fish sharing the same school leader.
 * Membership (join, promote, defect, lost-contact) is maintained during the periodic scan.</p>
 */
public class SchoolBoidGoal extends Goal {

    private static final double VIEW_RADIUS = 8.0;
    private static final double THREAT_RADIUS = 5.0;
    private static final double THREAT_RADIUS_SQR = THREAT_RADIUS * THREAT_RADIUS;

    private static final double COHESION_INFLUENCE = 0.05;
    private static final double ALIGNMENT_INFLUENCE = 0.4;
    private static final double SEPARATION_INFLUENCE = 0.25;
    private static final double DEPTH_INFLUENCE = 0.1;

    private static final double MAX_DELTA_FACTOR = 0.0075;

    private static final int SCAN_INTERVAL = 10;
    private static final int SCAN_INTERVAL_JITTER = 5;
    private static final int DEPTH_COMFORT_RANGE = 8;

    private static final int LOST_CONTACT_SCANS = 20;
    private static final int REJOIN_COOLDOWN_MIN = 600;
    private static final int REJOIN_COOLDOWN_JITTER = 600;

    private static final double PERSONAL_SPACE_MIN = 0.7;
    private static final double PERSONAL_SPACE_SPREAD = 0.6;
    private static final int PERSONAL_SPACE_REROLL_CHANCE = 20;

    private final FishBase fish;
    private final int maxNeighbors;

    private List<Mob> neighbors = new ArrayList<>();
    private LivingEntity closestThreat;
    private int scanCooldown;
    private int lonelyScans;
    private Vec3 depthBias = Vec3.ZERO;
    private double personalSpace;
    private double maxDelta;

    public SchoolBoidGoal(FishBase fish, int maxNeighbors) {
        this.fish = fish;
        this.maxNeighbors = maxNeighbors;
        this.personalSpace = this.rollPersonalSpace();
        this.scanCooldown = 1 + fish.getRandom().nextInt(SCAN_INTERVAL);
    }

    @Override
    public boolean canUse() {
        if (!this.fish.isInWater() || !this.fish.isSchoolingFish() || this.fish.isHiding()) {
            return false;
        }
        if (!this.isInSchoolingState(this.fish)) {
            return false;
        }
        if (--this.scanCooldown <= 0) {
            this.scanCooldown = SCAN_INTERVAL + this.fish.getRandom().nextInt(SCAN_INTERVAL_JITTER);
            this.scan();
        } else {
            this.neighbors.removeIf(m -> !m.isAlive());
            if (this.closestThreat != null && !this.closestThreat.isAlive()) {
                this.closestThreat = null;
            }
        }
        return !this.neighbors.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        this.neighbors = new ArrayList<>();
        this.closestThreat = null;
        this.scanCooldown = 0;
    }

    @Override
    public void tick() {
        double maxDelta = this.maxDelta;
        Vec3 nudge;

        if (this.closestThreat != null) {
            this.fish.getNavigation().stop();
            nudge = this.computeFlee().add(this.computeSeparation().scale(SEPARATION_INFLUENCE));
            if (this.fish.horizontalCollision) {
                nudge = nudge.add(0, 0.8, 0);
            }
            maxDelta = maxDelta * this.fish.getSchoolFleeSpeedMultiplier();
        } else {
            nudge = this.computeCohesion().scale(COHESION_INFLUENCE)
                    .add(this.computeAlignment().scale(ALIGNMENT_INFLUENCE))
                    .add(this.computeSeparation().scale(SEPARATION_INFLUENCE))
                    .add(this.depthBias.scale(DEPTH_INFLUENCE));
        }

        if (nudge.length() > maxDelta) {
            nudge = nudge.normalize().scale(maxDelta);
        }
        if (nudge.lengthSqr() < 1.0E-7) {
            return;
        }

        this.fish.addDeltaMovement(nudge);
        this.faceMovement();
    }

    private void faceMovement() {
        Vec3 velocity = this.fish.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-4) {
            return;
        }
        Vec3 target = this.fish.position().add(velocity);
        this.fish.lookAt(EntityAnchorArgument.Anchor.EYES,
                new Vec3(target.x, target.y + this.fish.getEyeHeight(), target.z));
    }

    private Vec3 computeCohesion() {
        Vec3 center = Vec3.ZERO;
        for (Mob neighbor : this.neighbors) {
            center = center.add(neighbor.position());
        }
        center = center.scale(1.0 / this.neighbors.size());
        return center.subtract(this.fish.position());
    }

    private Vec3 computeAlignment() {
        Vec3 avgVelocity = Vec3.ZERO;
        for (Mob neighbor : this.neighbors) {
            avgVelocity = avgVelocity.add(neighbor.getDeltaMovement());
        }
        avgVelocity = avgVelocity.scale(1.0 / this.neighbors.size());
        return avgVelocity.subtract(this.fish.getDeltaMovement());
    }

    private Vec3 computeSeparation() {
        Vec3 push = Vec3.ZERO;
        Vec3 fishPos = this.fish.position();
        double separationRange = this.fish.getSchoolSeparationRange() * this.personalSpace;
        for (Mob neighbor : this.neighbors) {
            double dist = neighbor.distanceTo(this.fish);
            if (dist < separationRange && dist > 0.01) {
                Vec3 away = fishPos.subtract(neighbor.position()).normalize();
                double strength = (separationRange - dist) / (separationRange * dist);
                push = push.add(away.scale(strength));
            }
        }
        return push;
    }

    private double rollPersonalSpace() {
        return PERSONAL_SPACE_MIN + this.fish.getRandom().nextDouble() * PERSONAL_SPACE_SPREAD;
    }

    private Vec3 computeFlee() {
        Vec3 away = this.fish.position().subtract(this.closestThreat.position());
        double dist = away.length();
        if (dist < 0.01) {
            double angle = this.fish.getRandom().nextDouble() * Math.PI * 2;
            return new Vec3(Math.cos(angle), 0, Math.sin(angle));
        }
        return away.normalize().scale(2.0 / (dist + 0.1));
    }

    private void updateDepthBias() {
        this.depthBias = Vec3.ZERO;

        SchoolDepthBias bias = this.fish.getSchoolDepthBias();
        if (bias == SchoolDepthBias.NONE) {
            return;
        }

        int dir = bias == SchoolDepthBias.SURFACE ? 1 : -1;
        BlockPos pos = this.fish.blockPosition();

        if (this.fish.level().getFluidState(pos.above(dir * DEPTH_COMFORT_RANGE)).is(FluidTags.WATER)) {
            this.depthBias = new Vec3(0, dir, 0);
        } else if (this.fish.level().getFluidState(pos.above(dir * 2)).is(FluidTags.WATER)) {
            this.depthBias = new Vec3(0, dir * 0.2, 0);
        }
    }

    private void scan() {
        if (this.fish.getRandom().nextInt(PERSONAL_SPACE_REROLL_CHANCE) == 0) {
            this.personalSpace = this.rollPersonalSpace();
        }

        List<LivingEntity> nearby = this.fish.level().getEntitiesOfClass(
                LivingEntity.class,
                this.fish.getBoundingBox().inflate(VIEW_RADIUS),
                e -> e != this.fish && e.isAlive()
        );

        this.closestThreat = null;
        double closestThreatDist = Double.MAX_VALUE;
        List<Mob> visible = new ArrayList<>();

        for (LivingEntity entity : nearby) {
            double dist = this.fish.distanceToSqr(entity);
            if (dist < THREAT_RADIUS_SQR && dist < closestThreatDist && this.fish.isThreat(entity)) {
                closestThreatDist = dist;
                this.closestThreat = entity;
            }
            if (entity instanceof Mob mob && this.isSchoolableFish(mob)) {
                visible.add(mob);
            }
        }

        this.updateMembership(visible);

        FishBase leader = this.fish.getSchoolLeader();
        List<Mob> schoolmates = new ArrayList<>();
        if (leader != null) {
            for (Mob mob : visible) {
                if (mob instanceof FishBase other && other.getSchoolLeader() == leader) {
                    schoolmates.add(mob);
                }
            }
        }

        this.trackLostContact(schoolmates);

        Vec3 fishPos = this.fish.position();
        schoolmates.sort(Comparator.comparingDouble(m -> m.distanceToSqr(fishPos.x, fishPos.y, fishPos.z)));

        if (schoolmates.size() > this.maxNeighbors) {
            this.neighbors = new ArrayList<>(schoolmates.subList(0, this.maxNeighbors));
        } else {
            this.neighbors = schoolmates;
        }

        this.updateDepthBias();
        this.maxDelta = Math.max(0.1, this.fish.getAttributeValue(Attributes.MOVEMENT_SPEED))
                * this.fish.getSwimSpeed() * MAX_DELTA_FACTOR;
    }

    private void updateMembership(List<Mob> visible) {
        this.fish.validateSchoolLeader();

        if (this.fish.hasSchool() && !this.fish.isSchoolLeader()
                && this.fish.getRandom().nextFloat() < this.fish.getSchoolDefectionChance()) {
            this.fish.leaveSchool();
            this.fish.setSchoolJoinCooldown(REJOIN_COOLDOWN_MIN + this.fish.getRandom().nextInt(REJOIN_COOLDOWN_JITTER));
            return;
        }

        boolean effectivelyLone = !this.fish.hasSchool()
                || (this.fish.isSchoolLeader() && this.fish.getSchoolSize() <= 1);
        if (!effectivelyLone || !this.fish.canJoinSchool()) {
            return;
        }

        FishBase bestLeader = null;
        double bestDist = Double.MAX_VALUE;
        for (Mob mob : visible) {
            if (mob instanceof FishBase other) {
                FishBase otherLeader = other.getSchoolLeader();
                if (otherLeader != null && otherLeader != this.fish && otherLeader.canAcceptSchoolMember()) {
                    double dist = this.fish.distanceToSqr(mob);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestLeader = otherLeader;
                    }
                }
            }
        }

        if (bestLeader != null) {
            this.fish.joinSchool(bestLeader);
        } else if (!this.fish.hasSchool() && !visible.isEmpty()) {
            this.fish.startSchool();
        }
    }

    private void trackLostContact(List<Mob> schoolmates) {
        if (this.fish.hasSchool() && !this.fish.isSchoolLeader() && schoolmates.isEmpty()) {
            if (++this.lonelyScans >= LOST_CONTACT_SCANS) {
                this.fish.leaveSchool();
                this.lonelyScans = 0;
            }
        } else {
            this.lonelyScans = 0;
        }
    }

    private boolean isSchoolableFish(Mob other) {
        return other != this.fish
                && other.isAlive()
                && other.isInWater()
                && this.fish.canSchoolWith(other)
                && this.isInSchoolingState(other);
    }

    private boolean isInSchoolingState(Mob mob) {
        if (mob instanceof FishBase fishMob) {
            if (fishMob.isBaby() && !fishMob.doesBabySchool()) {
                return false;
            }
            if (fishMob.isInLove() && fishMob.shouldDetachOnBreed()) {
                return false;
            }
        }
        return true;
    }

    public boolean isThreatened() {
        return this.closestThreat != null;
    }

    public List<Mob> getNeighbors() {
        return this.neighbors;
    }
}