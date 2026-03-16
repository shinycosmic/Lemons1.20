package net.lemon.animalia.entity.bases;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.*;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class BottomWalkerSwimmerBase extends FishBase {

    private static final EntityDataAccessor<Boolean> IS_WALKING = SynchedEntityData.defineId(BottomWalkerSwimmerBase.class, EntityDataSerializers.BOOLEAN);

    private int stateTime;
    public float currentRoll = 0.0F;
    private boolean wantsToWalk = false;

    protected BottomWalkerSwimmerBase(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WALKABLE, 0.0F);
        switchToWalking();
        this.stateTime = getWalkTime();
    }

    @Override
    public boolean useSmoothControl() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_WALKING, true);
    }

    public boolean isWalking() {
        return this.entityData.get(IS_WALKING);
    }

    public void setWalking(boolean walking) {
        this.entityData.set(IS_WALKING, walking);
    }

    /** Override in subclass. Ticks spent walking before switching to swim. */
    public int getWalkTime() {
        return 200 + random.nextInt(200); // 10-20 seconds default
    }

    /** Override in subclass. Ticks spent swimming before switching to walk. */
    public int getSwimTime() {
        return 100 + random.nextInt(100); // 5-10 seconds default
    }

    /** Override to adjust sink speed when walking underwater. */
    public double getSinkSpeed() {
        return -0.05;
    }

    @Override
    public boolean canRandomSwim() {
        return !this.isWalking() && !this.wantsToWalk;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new BottomWalkerStrollGoal(this, 1.0D));
    }

    public void switchToWalking() {
        this.moveControl = new MoveControl(this);
        this.lookControl = new LookControl(this);
        this.jumpControl = new WalkingJumpControl(this);
        this.navigation = new AmphibiousPathNavigation(this, level());
        this.setMaxUpStep(1.0F);
        this.setWalking(true);
        this.wantsToWalk = false;
    }

    public void switchToSwimming() {
        this.moveControl = new SmoothSwimmingMoveControl(this, 1, 1, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.navigation = new WaterBoundPathNavigation(this, level());
        this.setWalking(false);
        this.wantsToWalk = false;
    }

    @Override
    public void aiStep() {
        if (this.isInWater() && !this.level().isClientSide) {
            // State timer
            if (!this.wantsToWalk) {
                if (--this.stateTime <= 0) {
                    if (this.isWalking()) {
                        switchToSwimming();
                        this.stateTime = getSwimTime();
                    } else {
                        this.wantsToWalk = true;
//                        this.navigation.stop();
                    }
                }
            }

            // Transition: descend to floor, then switch to walking
            if (this.wantsToWalk) {
                if (this.onGround()) {
                    switchToWalking();
                    this.stateTime = getWalkTime();
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, getSinkSpeed(), 0));
                }
            }

            // Sink to bottom when walking
            if (this.isWalking() && !this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, getSinkSpeed(), 0));
            }

            // Prevent stepping out of water
            BlockPos pos = this.blockPosition();
            if (this.level().getBlockState(pos.above()).getFluidState().is(Fluids.EMPTY)) {
                this.setMaxUpStep(0);
            } else {
                this.setMaxUpStep(1.0F);
            }
        }

        super.aiStep();

        // Roll calculation (Saca-style banking on turns)
        float targetRoll = Math.max(-0.45F, Math.min(0.45F, (this.getYRot() - this.yRotO) * 0.1F));
        targetRoll = -targetRoll;
        this.currentRoll = this.currentRoll + (targetRoll - this.currentRoll) * 0.05F;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    /** Strongly prefer underwater positions, reject land */
    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return pLevel.getFluidState(pPos.above()).is(FluidTags.WATER) ? 10.0F : -1.0F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsWalking", this.isWalking());
        pCompound.putInt("StateTime", this.stateTime);
        pCompound.putBoolean("WantsToWalk", this.wantsToWalk);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.wantsToWalk = pCompound.getBoolean("WantsToWalk");
        this.stateTime = pCompound.getInt("StateTime");
        if (pCompound.contains("IsWalking")) {
            if (pCompound.getBoolean("IsWalking") && !this.wantsToWalk) {
                switchToWalking();
            } else {
                switchToSwimming();
            }
        }
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.isWalking()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }


    class BottomWalkerStrollGoal extends RandomStrollGoal {

        public BottomWalkerStrollGoal(PathfinderMob pMob, double pSpeedModifier) {
            super(pMob, pSpeedModifier);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            return DefaultRandomPos.getPos(this.mob, 10, 1);
        }

        @Override
        public boolean canUse() {
            return BottomWalkerSwimmerBase.this.isWalking()
                    && BottomWalkerSwimmerBase.this.isInWater()
                    && BottomWalkerSwimmerBase.this.onGround()
                    && super.canUse();
        }
    }

    static class WalkingJumpControl extends JumpControl {
        private final BottomWalkerSwimmerBase mob;
        public WalkingJumpControl(BottomWalkerSwimmerBase fish) {
            super(fish);
            this.mob = fish;
        }
        @Override
        public void jump() {
            if (!mob.isInWater()) super.jump();
        }
    }
}
