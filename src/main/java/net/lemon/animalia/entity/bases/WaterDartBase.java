package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FishBreedGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class WaterDartBase extends FishBase{
    private static final float[] YAW_OFFSETS =  {
            0F,
            10F, -10F,
            25F, -25F,
            45F, -45F,
            70F, -70F,
            110F, -110F,
            180F
    };

    public int panicCooldown = 0;
    public WaterDartBase(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    //future proofing for water striders and similar
    public boolean isSurfaceDarter(){ return false; }

    @Override
    public boolean canRandomSwim() { return false; }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof PanicGoal
                || g.getGoal() instanceof AvoidEntityGoal || g.getGoal() instanceof TemptGoal || g.getGoal() instanceof FishBreedGoal);
        this.goalSelector.addGoal(1, new DartPanicGoal(this));
        this.goalSelector.addGoal(2, new DartBreedGoal(this));
        this.goalSelector.addGoal(7, new DartWanderGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.panicCooldown > 0) {
                this.panicCooldown--;
            }
            if (this.isSurfaceDarter() && this.isInWater()) {
                if (this.isUnderWater()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02, 0));
                } else {
                    Vec3 vel = this.getDeltaMovement();
                    this.setDeltaMovement(vel.x, vel.y * 0.5, vel.z);
                }
            }
        }
    }

    public double getBurstPower() {
        return 0.25D;
    }

    public double getBabyBurstScale() {
        return 0.6D;
    }

    public int getRestCooldown() {
        return 30 + this.random.nextInt(30);
    }

    /** Override in subclass. how far vertical can this dart? 0 = horizontal only. */
    public float getDartPitch() {
        return 30.0F;
    }

    public int getPanicDartCount() {
        return 2 + this.random.nextInt(2);
    }

    public double getPanicRadius() {
        return 3.0D;
    }

    /** Override in subclass. Ticks between darts while approaching a mate. */
    public int getBreedDartInterval() {
        return 15;
    }

    public void dart(@Nullable Vec3 focusPos, boolean towardFocus) {
        float baseYaw = this.getYRot();
        float pitchRange = this.isSurfaceDarter() ? 0F : this.getDartPitch();
        float[] pitchOffsets = pitchRange > 0 ? new float[]{0F, pitchRange, -pitchRange} : new float[]{0F};

        float bestYaw = baseYaw;
        float bestPitch = 0F;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (float yawOffset : YAW_OFFSETS) {
            for (float pitch : pitchOffsets) {
                float yaw = baseYaw + yawOffset;
                double yawRad = Math.toRadians(yaw);
                double pitchRad = Math.toRadians(pitch);
                double xzScale = Math.cos(pitchRad);
                Vec3 dir = new Vec3(-Math.sin(yawRad) * xzScale, Math.sin(pitchRad), Math.cos(yawRad) * xzScale);

                double score = 0;
                if (focusPos != null) {
                    Vec3 focusDir = towardFocus
                            ? focusPos.subtract(this.position()).normalize()
                            : this.position().subtract(focusPos).normalize();
                    score += dir.dot(focusDir) * 3.0;
                }
                if (this.isDirectionBlocked(dir.scale(0.5))) {
                    score -= 8.0;
                }
                if (!this.isDirectionBlocked(dir.scale(1.5))) {
                    score += 2.0;
                }
                score -= this.waterPenalty(dir);
                if (pitch == 0F) {
                    score += 0.5;
                }
                if (focusPos == null && Math.abs(yawOffset) <= 45F) {
                    score += 0.05;
                }
                score += (this.getRandom().nextFloat() - 0.5) * 0.2;

                if (score > bestScore) {
                    bestScore = score;
                    bestYaw = yaw;
                    bestPitch = pitch;
                }
            }
        }

        double yawRad = Math.toRadians(bestYaw);
        double pitchRad = Math.toRadians(bestPitch);
        double strength = this.getBurstPower() * (this.isBaby() ? this.getBabyBurstScale() : 1.0);
        double xzScale = Math.cos(pitchRad);

        this.setDeltaMovement(
                -Math.sin(yawRad) * xzScale * strength,
                Math.sin(pitchRad) * strength,
                Math.cos(yawRad) * xzScale * strength
        );
        this.hasImpulse = true;

        this.setYRot(bestYaw);
        this.setYHeadRot(bestYaw);
        this.yRotO = bestYaw;
        this.yHeadRotO = bestYaw;
        this.setXRot(-bestPitch);
        this.xRotO = -bestPitch;
    }

    private boolean isDirectionBlocked(Vec3 offset) {
        AABB box = this.getBoundingBox().move(offset.x, offset.y, offset.z);
        return !this.level().noCollision(this, box);
    }

    private double waterPenalty(Vec3 dir) {
        for (int i = 1; i <= 3; i++) {
            Vec3 sample = this.position().add(dir.scale(i));
            BlockPos pos = BlockPos.containing(sample);
            if (this.level().getFluidState(pos).is(FluidTags.WATER)) {
                continue;
            }
            if (this.isSurfaceDarter() && this.level().getFluidState(pos.below()).is(FluidTags.WATER)) {
                continue;
            }
            return 16.0;
        }
        return 0.0;
    }

    @Nullable
    private Vec3 findThreatPos() {
        LivingEntity hurtBy = this.getLastHurtByMob();
        if (hurtBy != null && hurtBy.isAlive()) {
            return hurtBy.position();
        }
        Player player = this.level().getNearestPlayer(this, this.getPanicRadius() + 1.0);
        if (player != null && this.isThreat(player)) {
            return player.position();
        }
        return null;
    }

    static class DartPanicGoal extends Goal {
        private final WaterDartBase mob;
        private int dartCount;
        private int dartCooldown;
        protected boolean isRunning;

        public DartPanicGoal(WaterDartBase mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.panicCooldown > 0) return false;
            return this.shouldPanic();
        }

        protected boolean shouldPanic() {
            if (this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire()) {
                return true;
            }
            Player player = this.mob.level().getNearestPlayer(this.mob, this.mob.getPanicRadius());
            return player != null && this.mob.isThreat(player);
        }

        @Override
        public void start() {
            this.isRunning = true;
            this.dartCount = this.mob.getPanicDartCount();
            this.mob.panicCooldown = 80;
            this.triggerDart();
        }

        @Override
        public boolean canContinueToUse() {
            return this.isRunning && this.dartCount > 0 && this.mob.isInWater();
        }

        @Override
        public void stop() {
            this.isRunning = false;
            this.dartCount = 0;
        }

        @Override
        public void tick() {
            if (--this.dartCooldown <= 0 && this.dartCount > 0 && !this.mob.isMovementLockedByIdle()) {
                this.triggerDart();
            }
        }

        private void triggerDart() {
            this.dartCount--;
            this.dartCooldown = 8 + this.mob.getRandom().nextInt(5);
            this.mob.dart(this.mob.findThreatPos(), false);
        }
    }

    static class DartWanderGoal extends Goal {
        private final WaterDartBase mob;
        private int cooldown;

        public DartWanderGoal(WaterDartBase mob) {
            this.mob = mob;
            this.cooldown = mob.getRestCooldown();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.mob.isInWater() && !this.mob.isHiding();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            if (this.mob.isMovementLockedByIdle()) {
                return;
            }
            if (--this.cooldown <= 0) {
                this.mob.dart(null, false);
                this.cooldown = this.mob.getRestCooldown();
            }
        }
    }

    public static class DartBreedGoal extends FishBreedGoal {
        private int loveTime;
        private int dartCooldown;
        private final WaterDartBase mob;

        public DartBreedGoal(WaterDartBase mob) {
            super(mob, 1.0D);
            this.mob = mob;
        }

        @Override
        public boolean canContinueToUse() {
            return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
        }

        @Override
        public void stop() {
            super.stop();
            this.loveTime = 0;
            this.dartCooldown = 0;
        }

        @Override
        public void tick() {
            this.mob.getLookControl().setLookAt(this.partner, 10.0F, (float) this.mob.getMaxHeadXRot());
            ++this.loveTime;
            if (--this.dartCooldown <= 0 && !this.mob.isMovementLockedByIdle()
                    && this.mob.distanceToSqr(this.partner) > 4.0D) {
                this.mob.dart(this.partner.position(), true);
                this.dartCooldown = this.mob.getBreedDartInterval();
            }
            if (this.loveTime >= this.adjustedTickDelay(60) && this.mob.distanceToSqr(this.partner) < 9.0D) {
                this.breed();
            }
        }
    }
}
