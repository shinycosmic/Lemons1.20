package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.SchoolBoidGoal;
import net.lemon.animalia.entity.ai.utils.SchoolDepthBias;
import net.lemon.animalia.entity.ai.utils.SchoolSignal;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class FishBase extends AnimaliaBreedableWater implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.BOOLEAN);
    private static final int GRAZE_WINDOW = 200;

    private boolean invisToBoid;
    @Nullable
    private SchoolBoidGoal schoolBoidGoal;
    @Nullable
    private FishBase schoolLeader;
    private int schoolSize;
    private long schoolJoinCooldownEnd;
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

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    public boolean hasVerticalDrift() {
        return false;
    }

    @Override
    public boolean sinksWhenIdle() {
        return false;
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
        this.goalSelector.addGoal(7, new FishSwimGoal(this));
        if (this.isSchoolingFish()) {
            this.schoolBoidGoal = new SchoolBoidGoal(this, this.maxNeighbors());
            this.goalSelector.addGoal(5, this.schoolBoidGoal);
        }
        super.registerGoals();
    }

    public int maxNeighbors() {
        return 4;
    }

    //now we should finally have signal if there is no bucket instead of just defaulting to salmon buckets
    @Override
    public ItemStack getBucketItemStack() {
        String name = EntityType.getKey(this.getType()).getPath();
        return new ItemStack(Objects.requireNonNull(ModEntities.BUCKET_MAP.get(name), () -> "No bucket registered for " + name).get());
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
        this.signalDelay = signal.getBaseDelay() + this.random.nextInt(signal.getDelay());
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
        if (signal == SchoolSignal.GRAZE
                && this.canGraze() && this.findGrazeBlock() != null) {
            this.grazeWindow(GRAZE_WINDOW);
            return true;
        }

        if (signal == SchoolSignal.IDLE_DISPLAY && this.getIdleCount() > 0
                && this.canPlayIdle()) {
            int display = data >= 0 && data < this.getIdleCount()
                    ? data : this.pickIdleOfType(this, IdleType.MOVEMENT_POSITIVE);
            if (display >= 0 && this.getIdleType(display) == IdleType.MOVEMENT_POSITIVE) {
                return this.startIdle(display);
            }
            return false;
        }
        return false;
    }

    @Override
    public void onRandomIdle(int displayId) {
        if (this.isSchoolingFish()) {
            this.broadcastSchoolSignal(SchoolSignal.IDLE_DISPLAY, displayId);
            this.signalCooldown = 60 + this.random.nextInt(40);
        }
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

    public double getSchoolSeparationRange() {
        return 1.5;
    }

    public int getMaxSchoolSize() {
        return 20;
    }

    public boolean doesBabySchool() {
        return false;
    }

    public boolean shouldDetachOnBreed() {
        return true;
    }

    public boolean isSchoolingFish() {
        return false;
    }

    public boolean canSchoolWith(Mob other) {
        return other.getType() == this.getType();
    }

    @Override
    public void onMovementLockingIdleStart() {
        if (this.isSchoolingFish()) {
            this.setInvisToBoid(true);
            this.leaveSchool();
        }
    }

    @Override
    public void onMovementLockingIdleEnd() {
        this.setInvisToBoid(false);
    }

    @Override
    public void onGrazeStart() {
        if (this.isSchoolingFish()) {
            this.setInvisToBoid(true);
            this.leaveSchool();
        }
    }

    @Override
    public void onRandomGraze() {
        if (this.isSchoolingFish()) {
            this.broadcastSchoolSignal(SchoolSignal.GRAZE);
            this.signalCooldown = 60 + this.random.nextInt(40);
        }
    }

    @Override
    public void onGrazeStop() {
        this.setInvisToBoid(false);
    }

    public boolean isInvisToBoid() {
        return this.invisToBoid;
    }

    public void setInvisToBoid(boolean invis) {
        this.invisToBoid = invis;
    }

    public double getSchoolFleeSpeedMultiplier() {
        return 2.0;
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

    @Nullable
    public FishBase getSchoolLeader() {
        return this.schoolLeader;
    }

    public boolean isSchoolLeader() {
        return this.schoolLeader == this;
    }

    public boolean hasSchool() {
        return this.schoolLeader != null;
    }

    public int getSchoolSize() {
        return this.schoolLeader == null ? 0 : this.schoolLeader.schoolSize;
    }

    public boolean canAcceptSchoolMember() {
        return this.isSchoolLeader() && this.isAlive() && this.schoolSize < this.getMaxSchoolSize();
    }

    public void startSchool() {
        this.leaveSchool();
        this.schoolLeader = this;
        this.schoolSize = 1;
    }

    public void joinSchool(FishBase leader) {
        this.leaveSchool();
        this.schoolLeader = leader;
        leader.schoolSize++;
    }

    public void leaveSchool() {
        if (this.schoolLeader != null && this.schoolLeader != this && this.schoolLeader.isSchoolLeader()) {
            this.schoolLeader.schoolSize--;
        }
        this.schoolLeader = null;
        this.schoolSize = 0;
    }

    public void validateSchoolLeader() {
        if (this.schoolLeader != null && this.schoolLeader != this
                && (!this.schoolLeader.isAlive() || !this.schoolLeader.isSchoolLeader())) {
            this.schoolLeader = null;
        }
    }

    public boolean canJoinSchool() {
        return this.level().getGameTime() >= this.schoolJoinCooldownEnd;
    }

    public void setSchoolJoinCooldown(int ticks) {
        this.schoolJoinCooldownEnd = this.level().getGameTime() + ticks;
    }

    public float getSchoolDefectionChance() {
        return 0.0002f;
    }

    @Override
    public void remove(RemovalReason reason) {
        this.leaveSchool();
        super.remove(reason);
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
            this.moveRelative(0.01F, this.isMovementLockedByIdle() ? Vec3.ZERO : pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.getSwimSpeed()));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null && this.sinksWhenIdle()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(pTravelVector);
        }

    }

    public void setForcedInWater(boolean inWater) {
        this.wasTouchingWater = inWater;
    }

    public boolean bucketable() {
        return true;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.bucketable()) {
            return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public Item getEggItem() {
        return ModItems.FISH_EGG.get();
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

        public boolean canUse() {
            return this.fish.canRandomSwim() && super.canUse();
        }
    }

}
