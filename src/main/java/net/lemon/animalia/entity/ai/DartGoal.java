package net.lemon.animalia.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.BooleanSupplier;

public abstract class DartGoal extends Goal {
    private final Mob mob;
    private double speed;
    private int cooldown = 0;
    private int restTime = 0;
    private double minDistance = 1.0;
    private double maxDistance = 10.0;
    private float chance = 0.3f;
    private Vec3 targetPos = null;
    private final BooleanSupplier envCheck;
    private DartTargetProvider targetProvider;

    public DartGoal(Mob mob, double speed, BooleanSupplier environmentCheck, DartTargetProvider targetProvider, float fireChance, float minDist, float maxDist, int rest) {
        this.mob = mob;
        this.speed = speed;
        this.envCheck = environmentCheck;
        this.targetProvider = targetProvider;
        this.chance = fireChance;
        this.minDistance = minDist;
        this.maxDistance = maxDist;
        this.restTime = rest;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public DartGoal(Mob mob, double speed, BooleanSupplier environmentCheck, DartTargetProvider targetProvider){
        this(mob, speed, environmentCheck, targetProvider, 0.3f, 1, 10, 0);
    }

    protected boolean isEnvironmentEligible() {
        return envCheck.getAsBoolean();
    }

    protected Vec3 findTarget() {
        return targetProvider.getTarget();
    }
    @Override
    public boolean canUse() {
        if (!isEnvironmentEligible()) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        //Has a chance to not use if all other conditions are true
        if (mob.getRandom().nextFloat() > this.chance) return false;

        targetPos = findTarget();
        if (targetPos == null) return false;

        double distance = mob.position().distanceTo(targetPos);
        return distance >= minDistance && distance <= maxDistance;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue until we reach the target or some external condition stops it
        if (targetPos == null) return false;
        double distance = mob.position().distanceTo(targetPos);
        return distance > 0.5; // threshold for "reached target"
    }

    @Override
    public void start() {
        // Move toward the target
        moveToTarget();
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        // Move the mob incrementally toward the target
        moveToTarget();

        // If we've reached the target, begin rest timer
        if (mob.position().distanceTo(targetPos) <= 0.5) {
            cooldown = getRestTime();
            stop();
        }
    }

    private void moveToTarget() {
        Vec3 current = mob.position();
        Vec3 direction = targetPos.subtract(current).normalize();
        Vec3 movement = direction.scale(speed);
        mob.setDeltaMovement(movement);
        mob.setYRot((float) Math.toDegrees(Math.atan2(movement.z, movement.x)) - 90f);
        mob.yBodyRot = mob.getYRot();
    }

    private int getRestTime() {
        return restTime > 0 ? restTime : 20 + mob.getRandom().nextInt(40);
    }

    /**
     * Functional interface for providing dart targets
     */
    @FunctionalInterface
    public interface DartTargetProvider {
        Vec3 getTarget();
    }
}
