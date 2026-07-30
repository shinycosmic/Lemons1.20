package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.navigation.WaterBottomPathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.*;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class BottomWalkerSwimmerBase extends FishBase {

    private static final EntityDataAccessor<Boolean> IS_WALKING = SynchedEntityData.defineId(BottomWalkerSwimmerBase.class, EntityDataSerializers.BOOLEAN);

    private int stateTime;
    public float currentRoll = 0.0F;
    public boolean wantsToWalk = false;

    protected BottomWalkerSwimmerBase(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WALKABLE, 0.0F);
        if(pLevel != null) {
            this.selectNavigator();
        }
        this.stateTime = this.startsWalking() ? this.getWalkTime() : this.getSwimTime();
    }

    @Override
    public boolean useSmoothControl() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_WALKING, false);
    }

    public boolean hasSwimToWalkTransition() {
        return true;
    }

    //use !isWalking() to check for is swimming
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
        return -0.008;
    }

    @Override
    public boolean canRandomSwim() {
        return !this.isWalking() && !this.wantsToWalk;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new BottomWalkerStrollGoal(this, 1.0D));
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isWalking();
    }

    public boolean startsWalking() {
        return false;
    }

    @Override
    public void aiStep() {
        if(!this.level().isClientSide) {
            this.selectNavigator();
        }
        if (this.isInWater() && !this.level().isClientSide) {
            //handle decrease ticks when walking
            if(this.stateTime > 0 && this.isWalking()) {
                this.stateTime = this.stateTime - this.random.nextInt(3);
                if(this.stateTime < 0) {
                    this.stateTime = 0;
                }
            }

            //handle decrease ticks when swimming
            if(this.stateTime > 0 && !this.isWalking()) {
                this.stateTime = this.stateTime - this.random.nextInt(3);
                if(this.stateTime < 0) {
                    this.stateTime = 0;
                }
            }

            //switch from walking to swimming, cannot switch if also hiding
            if(!(this.stateTime > 0) && this.isWalking() && !this.isHiding()) {
                this.setWalking(false);
                this.stateTime = this.getSwimTime();
            }

            if(!(this.stateTime > 0) && !this.isWalking()) {
                if(this.hasSwimToWalkTransition()) {
                    this.wantsToWalk = true;
                } else {
                    this.setWalking(true);
                    this.stateTime = this.getWalkTime();
                }
            }

            // Transition: descend to floor, then switch to walking
            if (this.wantsToWalk) {
                if (this.onGround()) {
                    this.setWalking(true);
                    this.wantsToWalk = false;
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

        // Roll calculation
        float targetRoll = Math.max(-0.45F, Math.min(0.45F, (this.getYRot() - this.yRotO) * 0.1F));
        targetRoll = -targetRoll;
        this.currentRoll = this.currentRoll + (targetRoll - this.currentRoll) * 0.05F;
    }

    /** Strongly prefer underwater positions, reject land */
    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return pLevel.getFluidState(pPos.above()).is(FluidTags.WATER) ? 10.0F : -1.0F;
    }

    public void selectNavigator() {
        if(!this.isWalking()) {
            if((!(this.moveControl instanceof SmoothSwimmingMoveControl))
        || (!(this.navigation instanceof WaterBoundPathNavigation))) {
                this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2f, 0.1f, true);
                this.navigation = new WaterBoundPathNavigation(this, this.level());
                this.navigation.stop();
            }
        }
        else if((!(this.moveControl instanceof BottomWalkMoveController))
            || (!(this.navigation instanceof WaterBottomPathNavigation))) {
            this.moveControl = new BottomWalkMoveController();
            this.navigation = new WaterBottomPathNavigation(this, this.level());
            this.navigation.stop();
        }
    }

    @Override
    public boolean canStartHiding() {
        return super.canStartHiding() && this.isInWater() && this.isWalking() && this.onGround();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("StateTime", this.stateTime);
        pCompound.putBoolean("isWalking", this.isWalking());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.stateTime = pCompound.getInt("StateTime");
        this.setWalking(pCompound.getBoolean("isWalking"));
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.isWalking()) {
            this.moveRelative(0.01f, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.getSwimSpeed()));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }

    public float getMaxTurn() {
        return 10.0F;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (this.startsWalking()) {
            this.setWalking(true);
            this.stateTime = this.getWalkTime();
        } else {
            this.setWalking(false);
            this.stateTime = this.getSwimTime();
        }
        return spawnData;
    }

    public class BottomWalkerStrollGoal extends RandomStrollGoal {

        public BottomWalkerStrollGoal(PathfinderMob pMob, double pSpeedModifier) {
            super(pMob, pSpeedModifier);
        }

        @Override
        protected Vec3 getPosition() {
            Vec3 pos = DefaultRandomPos.getPos(this.mob, 10, 3);
            System.out.println("[Stroll] getPos=" + pos);
            if (pos == null) return null;

            BlockPos floor = BlockPos.containing(pos);

            while (this.mob.level().getFluidState(floor).is(Fluids.WATER) && floor.getY() > this.mob.level().getMinBuildHeight()) {
                floor = floor.below();
            }

            return Vec3.atCenterOf(floor.above());
        }
        @Override
        public boolean canUse() {
            boolean sup = super.canUse();
            System.out.println("[Stroll] side=" + (this.mob.level().isClientSide ? "CLIENT" : "SERVER")
                    + " walking=" + BottomWalkerSwimmerBase.this.isWalking()
                    + " ground=" + BottomWalkerSwimmerBase.this.onGround()
                    + " nav=" + this.mob.getNavigation().getClass().getSimpleName()
                    + " stableHere=" + this.mob.getNavigation().isStableDestination(this.mob.blockPosition())
                    + " noAction=" + this.mob.getNoActionTime()
                    + " super=" + sup);
            return BottomWalkerSwimmerBase.this.isWalking()
                    && BottomWalkerSwimmerBase.this.isInWater()
                    && BottomWalkerSwimmerBase.this.onGround()
                    && sup;
        }
    }

    public class BottomWalkMoveController extends MoveControl {
        private final BottomWalkerSwimmerBase mob = BottomWalkerSwimmerBase.this;

        public BottomWalkMoveController() {
            super(BottomWalkerSwimmerBase.this);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.STRAFE) {
                //float f = (float) this.EntityBase.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                float f = (float)getSwimSpeed();
                float f1 = (float) this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);

                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }

                f4 = f1 / f4;
                f2 *= f4;
                f3 *= f4;
                float f5 = Mth.sin(this.mob.getYRot() * 0.017453292F);
                float f6 = Mth.cos(this.mob.getYRot() * 0.017453292F);
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;

                PathNavigation navigator = this.mob.getNavigation();
                if (navigator != null) {
                    NodeEvaluator nodeEvaluator = navigator.getNodeEvaluator();
                    if (nodeEvaluator != null && nodeEvaluator.getBlockPathType(this.mob.level(),
                            Mth.floor(this.mob.getX() + (double) f7),
                            Mth.floor(this.mob.getY()),
                            Mth.floor(this.mob.getZ() + (double) f8)) != BlockPathTypes.WALKABLE) {
                        this.strafeForwards = 1.0F;
                        this.strafeRight = 0.0F;
                        f1 = f;
                    }
                }

                this.mob.setSpeed(f1);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = Operation.WAIT;
            } else if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;

                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float turn = (mob.getMaxTurn());
                float f9 = (float) (Mth.atan2(d1, d0) * (180D / Math.PI)) - 90;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, turn));
                this.mob.setSpeed((float) (0.4f * this.speedModifier * (float)getSwimSpeed()));

                //Testing mode:
                //this.mob.setAIMoveSpeed(0f);

                BlockPos blockpos = this.mob.blockPosition();
                BlockState blockstate = this.mob.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
                if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = MoveControl.Operation.JUMPING;
                }
            } else if (this.operation == Operation.JUMPING) {
                this.mob.setSpeed((float) (this.speedModifier * (float)getSwimSpeed()));
//                this.mob.motionY += 0.04D;
                if (this.mob.onGround()) {
                    this.operation = Operation.WAIT;
                }
            } else {
                this.mob.setZza(0.0F);
            }

            if (mob.onGround()) {
                Vec3 vel = mob.getDeltaMovement();
                mob.setDeltaMovement(vel.x, 0, vel.z); // lock vertical motion
            }
        }
    }

}
