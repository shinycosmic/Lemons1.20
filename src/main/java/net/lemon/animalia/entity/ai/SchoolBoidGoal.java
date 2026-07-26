package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Boid-based schooling goal for fish. While active, this goal owns the fish's movement:
 * each tick it computes a target direction from three local rules (separation, alignment,
 * cohesion) plus an optional depth bias, lerps velocity toward it, and rotates the fish
 * to face its motion. Smooth coordinated turns emerge from the velocity inertia — no
 * leader, no pathfinding.
 *
 * <p>The goal claims {@link Goal.Flag#MOVE} at low priority, so higher-priority behaviors
 * (panic, breed, tempt, graze) preempt schooling and it resumes when they finish. A fish
 * with no schoolmates in view fails {@code canUse()} and wanders normally — schools form
 * when wandering individuals happen upon each other.</p>
 *
 * <p>When a threat (see {@link FishBase#isThreat(LivingEntity)}) enters range, cohesion and
 * alignment are suppressed and a flee vector dominates, scattering the school radially.
 * Cohesion reconverges the survivors once the threat leaves.</p>
 *
 * <p>Social behaviors (grazing, hiding, idle displays) are handled separately by the
 * {@link SchoolSignal} system on FishBase. This goal only handles movement.</p>
 */
public class SchoolBoidGoal extends Goal {

    private static final double VIEW_RADIUS = 8.0;
    private static final double SEPARATION_RANGE = 1.5;
    private static final double THREAT_RADIUS = 5.0;

    private static final double W_COHESION = 1.5;
    private static final double W_ALIGNMENT = 2.0;
    private static final double W_SEPARATION = 3.5;
    private static final double W_DEPTH = 0.5;
    private static final double W_DEPTH_FORCED = 5.0;

    private static final double INERTIA = 0.1;
    private static final double FLEE_INERTIA = 0.5;
    private static final double FLEE_SPEED_MULTIPLIER = 1.5;
    private static final double Y_FLATTEN = 0.2;

    private static final int SCAN_INTERVAL = 10;
    private static final int MAX_NEIGHBORS = 4;
    private static final int DEPTH_COMFORT_RANGE = 8;

    private final FishBase fish;

    private List<Mob> neighbors = new ArrayList<>();
    private LivingEntity closestThreat;
    private int scanCooldown;
    private Vec3 depthBias = Vec3.ZERO;
    private boolean depthForced;

    public SchoolBoidGoal(FishBase fish) {
        this.fish = fish;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.fish.isInWater() || !this.fish.isSchoolingFish() || this.fish.isHiding()) {
            return false;
        }
        if (--this.scanCooldown <= 0) {
            this.scanCooldown = SCAN_INTERVAL;
            this.scanNeighbors();
            this.scanThreats();
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
    public void start() {
        this.fish.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.neighbors = new ArrayList<>();
        this.closestThreat = null;
        this.scanCooldown = 0;
    }

    @Override
    public void tick() {
        this.updateDepthBias();

        Vec3 targetDir;
        double lerpFactor;
        double speed = Math.max(0.1, this.fish.getAttributeValue(Attributes.MOVEMENT_SPEED));

        if (this.closestThreat != null) {
            targetDir = this.computeFlee().add(this.computeSeparation().scale(W_SEPARATION)).normalize();
            if (this.fish.horizontalCollision) {
                targetDir = targetDir.add(0, 0.8, 0).normalize();
            }
            lerpFactor = FLEE_INERTIA;
            speed = speed * FLEE_SPEED_MULTIPLIER;
        } else {
            Vec3 boid = this.computeCohesion().scale(W_COHESION)
                    .add(this.computeAlignment().scale(W_ALIGNMENT))
                    .add(this.computeSeparation().scale(W_SEPARATION))
                    .add(this.depthBias.scale(this.depthForced ? W_DEPTH_FORCED : W_DEPTH));
            targetDir = boid.normalize();
            if (!this.depthForced) {
                targetDir = new Vec3(targetDir.x, targetDir.y * Y_FLATTEN, targetDir.z).normalize();
            }
            lerpFactor = this.depthForced ? FLEE_INERTIA : INERTIA;
        }

        if (targetDir.lengthSqr() < 1.0E-4) {
            return;
        }

        Vec3 idealVelocity = targetDir.scale(speed);
        Vec3 velocity = this.fish.getDeltaMovement().lerp(idealVelocity, lerpFactor);
        this.fish.setDeltaMovement(velocity);
        this.faceMovement(velocity);
    }

    private void faceMovement(Vec3 velocity) {
        if (velocity.lengthSqr() < 1.0E-4) {
            return;
        }
        float yaw = (float) Math.toDegrees(Mth.atan2(-velocity.x, velocity.z));
        float pitch = (float) -Math.toDegrees(Mth.atan2(velocity.y, velocity.horizontalDistance()));
        this.fish.setYRot(yaw);
        this.fish.setYHeadRot(yaw);
        this.fish.setYBodyRot(yaw);
        this.fish.setXRot(pitch);
    }

    private Vec3 computeCohesion() {
        Vec3 center = Vec3.ZERO;
        for (Mob neighbor : this.neighbors) {
            center = center.add(neighbor.position());
        }
        center = center.scale(1.0 / this.neighbors.size());
        return center.subtract(this.fish.position()).normalize();
    }

    private Vec3 computeAlignment() {
        Vec3 avgVelocity = Vec3.ZERO;
        for (Mob neighbor : this.neighbors) {
            avgVelocity = avgVelocity.add(neighbor.getDeltaMovement());
        }
        return avgVelocity.scale(1.0 / this.neighbors.size()).normalize();
    }

    private Vec3 computeSeparation() {
        Vec3 push = Vec3.ZERO;
        Vec3 fishPos = this.fish.position();
        for (Mob neighbor : this.neighbors) {
            double dist = neighbor.distanceTo(this.fish);
            if (dist < SEPARATION_RANGE && dist > 0.01) {
                Vec3 away = fishPos.subtract(neighbor.position()).normalize();
                push = push.add(away.scale(1.0 / dist));
            }
        }
        return push;
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
        this.depthForced = false;

        SchoolDepthBias bias = this.fish.getSchoolDepthBias();
        if (bias == SchoolDepthBias.NONE) {
            return;
        }

        int dir = bias == SchoolDepthBias.SURFACE ? 1 : -1;
        BlockPos pos = this.fish.blockPosition();

        if (this.fish.level().getFluidState(pos.above(dir * DEPTH_COMFORT_RANGE)).is(FluidTags.WATER)) {
            this.depthBias = new Vec3(0, dir, 0);
            this.depthForced = true;
        } else if (this.fish.level().getFluidState(pos.above(dir * 2)).is(FluidTags.WATER)) {
            this.depthBias = new Vec3(0, dir * 0.2, 0);
        }
    }

    private void scanNeighbors() {
        List<Mob> found = this.fish.level().getEntitiesOfClass(
                Mob.class,
                this.fish.getBoundingBox().inflate(VIEW_RADIUS),
                this::isValidSchoolmate
        );

        Vec3 fishPos = this.fish.position();
        found.sort(Comparator.comparingDouble(m -> m.distanceToSqr(fishPos.x, fishPos.y, fishPos.z)));

        if (found.size() > MAX_NEIGHBORS) {
            this.neighbors = new ArrayList<>(found.subList(0, MAX_NEIGHBORS));
        } else {
            this.neighbors = found;
        }
    }

    private void scanThreats() {
        this.closestThreat = null;
        double closestDist = Double.MAX_VALUE;

        List<LivingEntity> threats = this.fish.level().getEntitiesOfClass(
                LivingEntity.class,
                this.fish.getBoundingBox().inflate(THREAT_RADIUS),
                e -> e != this.fish && e.isAlive() && this.fish.isThreat(e)
        );

        for (LivingEntity threat : threats) {
            double dist = this.fish.distanceToSqr(threat);
            if (dist < closestDist) {
                closestDist = dist;
                this.closestThreat = threat;
            }
        }
    }

    private boolean isValidSchoolmate(Mob other) {
        return other != this.fish
                && other.isAlive()
                && other.isInWater()
                && this.fish.canSchoolWith(other);
    }

    /**
     * Whether this fish currently has a threat nearby (scatter mode active).
     * Used by the signal system to suppress non-critical behaviors.
     */
    public boolean isThreatened() {
        return this.closestThreat != null;
    }

    /**
     * The cached list of nearby schoolmates, refreshed every SCAN_INTERVAL ticks.
     * Reused by the signal broadcast system on FishBase to avoid redundant scans.
     */
    public List<Mob> getNeighbors() {
        return this.neighbors;
    }

    /**
     * Whether the school (locally) is moving slowly enough for idle behaviors.
     */
    public boolean isSchoolDrifting() {
        if (this.fish.getDeltaMovement().lengthSqr() > 0.003) {
            return false;
        }
        if (this.neighbors.isEmpty()) {
            return false;
        }
        double avgSpeedSq = 0;
        for (Mob neighbor : this.neighbors) {
            avgSpeedSq += neighbor.getDeltaMovement().lengthSqr();
        }
        return avgSpeedSq / this.neighbors.size() < 0.003;
    }
}