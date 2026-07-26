package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.SchoolBoidGoal;
import net.lemon.animalia.entity.ai.SchoolDepthBias;
import net.lemon.animalia.entity.ai.SchoolSignal;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public abstract class FishBase extends AnimaliaBreedableWater implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private SchoolBoidGoal schoolBoidGoal;
    @Nullable
    private SchoolSignal pendingSignal;
    private int signalData;
    private int signalDelay;
    private int signalCooldown;

    public FishBase(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setCanPickUpLoot(false);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean isActuallyMoving() {
        return this.walkAnimation.isMoving();
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean pFromBucket) {
        this.entityData.set(FROM_BUCKET, pFromBucket);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.6D, 1.4D, EntitySelector.NO_SPECTATORS::test));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.2D, 10));
        if (this.isSchoolingFish()) {
            this.schoolBoidGoal = new SchoolBoidGoal(this);
            this.goalSelector.addGoal(5, this.schoolBoidGoal);
        }
        super.registerGoals();
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putFloat("BucketVarSize", this.getVarSizeMultiplier());
        compoundTag.putInt("Age", this.getAge());
        compoundTag.putInt("BucketGender", this.getGender());
        compoundTag.putInt("BucketVarColor", this.getVarColor());
        compoundTag.putBoolean("BucketBaby", this.isBaby());
    }

    public void loadFromBucketTag(CompoundTag pTag) {
        if(pTag != null) {
            if(pTag.contains("BucketVarSize")) {
                this.setVarSizeMultiplier(pTag.getFloat("BucketVarSize"));
            }
            if(pTag.contains("Age")) {
                this.setAge(pTag.getInt("Age"));
            }
            if(pTag.contains("BucketGender")) {
                this.setGender(pTag.getInt("BucketGender"));
            }
            if(pTag.contains("BucketVarColor")){
                this.setVarColor(pTag.getInt("BucketVarColor"));
            }
        }
        Bucketable.loadDefaultDataFromBucketTag(this, pTag);
    }

    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    public boolean canRandomSwim() {
        return true;
    }

    public SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    public void broadcastSchoolSignal(SchoolSignal signal) {
        this.broadcastSchoolSignal(signal, -1);
    }

    public void broadcastSchoolSignal(SchoolSignal signal, int data) {
        if (this.schoolBoidGoal == null || this.schoolBoidGoal.isThreatened()) {
            return;
        }
        for (Mob neighbor : this.schoolBoidGoal.getNeighbors()) {
            if (neighbor instanceof FishBase fishNeighbor) {
                fishNeighbor.receiveSchoolSignal(signal, data);
            }
        }
    }

    public void receiveSchoolSignal(SchoolSignal signal, int data) {
        if (this.pendingSignal != null || this.signalCooldown > 0 || this.isHiding()) {
            return;
        }
        this.pendingSignal = signal;
        this.signalData = data;
        this.signalDelay = signal.getBaseDelay() + this.random.nextInt(signal.getDelayJitter());
    }

    protected void tickSchoolSignals() {
        if (this.signalCooldown > 0) {
            this.signalCooldown--;
        }
        if (this.pendingSignal == null) {
            return;
        }
        if (--this.signalDelay <= 0) {
            SchoolSignal signal = this.pendingSignal;
            int data = this.signalData;
            this.pendingSignal = null;
            boolean acted = this.random.nextFloat() < signal.getAdoptionChance()
                    && this.onSchoolSignalReceived(signal, data);
            if (acted && signal.wavePropagates()) {
                this.broadcastSchoolSignal(signal, data);
            }
            this.signalCooldown = 60 + this.random.nextInt(40);
        }
    }

    public boolean onSchoolSignalReceived(SchoolSignal signal, int data) {
        if (signal == SchoolSignal.IDLE_DISPLAY && this.getIdleDisplayCount() > 0
                && this.canPlayIdleDisplay()) {
            int display = data >= 0 && data < this.getIdleDisplayCount()
                    ? data : this.random.nextInt(this.getIdleDisplayCount());
            return this.startIdleDisplay(display);
        }
        return false;
    }

    @Override
    public void onSpontaneousIdleDisplay(int displayId) {
        if (this.isSchoolingFish()) {
            this.broadcastSchoolSignal(SchoolSignal.IDLE_DISPLAY, displayId);
            this.signalCooldown = 60 + this.random.nextInt(40);
        }
    }

    @Override
    public boolean canPlayIdleDisplay() {
        if (this.isHiding()) {
            return false;
        }
        if (this.schoolBoidGoal != null && !this.schoolBoidGoal.getNeighbors().isEmpty()) {
            return this.schoolBoidGoal.isSchoolDrifting();
        }
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putBoolean("FromBucket", this.fromBucket());
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setFromBucket(pCompound.getBoolean("FromBucket"));
        super.readAdditionalSaveData(pCompound);
    }

    public int getMaxSchoolSize() {
        return 10;
    }

    public boolean isSchoolingFish() {
        return false;
    }

    public boolean canSchoolWith(Mob other) {
        return other.getClass() == this.getClass();
    }

    public boolean isThreat(LivingEntity entity) {
        if (entity instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return entity instanceof Monster;
    }

    public SchoolDepthBias getSchoolDepthBias() {
        return SchoolDepthBias.NONE;
    }

    @Override
    public void aiStep() {
        if (this.shouldJumpOnFlop() && !this.isInWater() && this.onGround() && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
        }

        if (!this.level().isClientSide && this.isSchoolingFish()) {
            this.tickSchoolSignals();
        }

        if (this.isAlive()) {
            if (this.level().isClientSide) {

            }

            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            }
        }

        if (!this.level().isClientSide && this.wantsToHide && !this.isHiding() && this.isInWater() && !this.onGround() && this.canHide()) {
            if (this.getNavigation().isDone()) {
                BlockPos floor = this.blockPosition();
                while (this.level().getFluidState(floor.below()).is(FluidTags.WATER)
                        && floor.getY() > this.level().getMinBuildHeight()) {
                    floor = floor.below();
                }
                this.getNavigation().moveTo(this.getX(), floor.getY() + 0.5, this.getZ(), 1.0);
            }
        }

        super.aiStep();
    }

    /***
     * Same as AbstractFish, but we want to abstract a bit better than vanilla since we need breeding.
     * @param pTravelVector
     */
    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.getSwimSpeed()));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(pTravelVector);
        }

    }

    public void setForcedInWater(boolean inWater) {
        this.wasTouchingWater = inWater;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public Item getEggItem() {
        return ModItems.FISH_EGG.get();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if(reason != MobSpawnType.BUCKET) {
            this.setGender(this.random.nextInt(2));
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    /**
     * Taken from AbstractFish
     */
    public static class FishSwimGoal extends RandomSwimmingGoal {
        private final FishBase fish;

        public FishSwimGoal(FishBase pFish) {
            super(pFish, 1.0D, 10);
            this.fish = pFish;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return this.fish.canRandomSwim() && super.canUse();
        }
    }

}
