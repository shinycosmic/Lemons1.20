package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.EatDroppedItemsGoal;
import net.lemon.animalia.entity.bases.helpers.*;
import net.lemon.animalia.item.FishEggItem;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.lemon.animalia.entity.bases.AnimaliaBreedableWater.*;

public abstract class AnimaliaLandBase extends Animal implements IActivityTime, IFoodEater, IIdles, IGrazer {
    private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VAR_COLOR = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> VAR_SIZE_MULTIPLIER = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_EATING = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HIDE_PHASE = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_GRAZING = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BODY_IDLE = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TWITCH_IDLE = SynchedEntityData.defineId(AnimaliaLandBase.class, EntityDataSerializers.INT);

    private int grazeTicks = 0;
    private int grazeUrgeUntil;
    private int eatTicks = 0;
    private int hideTicks = 0;
    private int hideCooldown = 0;
    private int hideTransitionInTicks = 0;
    private int hideTransitionOutTicks = 0;
    public boolean wantsToHide = false;
    public int cooldown = 0;
    public int growthTicks = -12000;
    private int idleDisplayTicks;
    private int twitchIdleTicks;

    protected AnimaliaLandBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return pSize.height * 0.65F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GENDER, 0);
        this.entityData.define(VAR_COLOR, 0);
        this.entityData.define(VAR_SIZE_MULTIPLIER, 1.0f);
        this.entityData.define(IS_EATING, false);
        this.entityData.define(IS_GRAZING, false);
        this.entityData.define(IS_HIDING, false);
        this.entityData.define(HIDE_PHASE, 0);
        this.entityData.define(BODY_IDLE, -1);
        this.entityData.define(TWITCH_IDLE, -1);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (VAR_SIZE_MULTIPLIER.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    public String getScientificName() {
        String key = this.getType().getDescriptionId() + ".scientific";
        return Language.getInstance().has(key) ? Component.translatable(key).getString() : "";
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.15D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, this.foodIngredients(), false));
        //this.goalSelector.addGoal(4, new EatDroppedItemsGoal<>(this, 1.2D, 10.0F));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        super.registerGoals();
    }

    public Ingredient foodIngredients() {
        return Ingredient.of(getFoodTag());
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(getBreedingItem());
    }

    public abstract Item getBreedingItem();

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(getFoodTag());
    }

    public abstract TagKey<Item> getFoodTag();

    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.RABBIT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    public boolean isEating() {
        return this.entityData.get(IS_EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(IS_EATING, eating);
    }

    public void startEating() {
        this.setEating(true);
        this.eatTicks = this.getEatLength();
    }

    @Override
    public boolean isGrazableBlock(BlockState state) {
        return false;
    }

    public boolean isGrazing() {
        return this.entityData.get(IS_GRAZING);
    }

    public void setGrazing(boolean grazing) {
        this.entityData.set(IS_GRAZING, grazing);
    }

    public void startGrazing() {
        this.setGrazing(true);
        this.grazeTicks = this.getGrazeLength();
    }

    public boolean canGraze() {
        return this.onGround() && !this.isInWater() && !this.isHiding() && !this.isEating() && !this.isInLove()
                && !this.isMovementLockedByIdle();
    }

    public boolean wantsToGraze() {
        return this.tickCount < this.grazeUrgeUntil;
    }

    public void urgeGraze(int ticks) {
        this.grazeUrgeUntil = this.tickCount + ticks;
    }

    public void clearGrazeUrge() {
        this.grazeUrgeUntil = 0;
    }

    public float getVarSizeMultiplier() {
        return this.entityData.get(VAR_SIZE_MULTIPLIER);
    }

    /// Call this from classes to set a multiplier
    public void setVarSizeMultiplier(float sizeMult) {
        this.entityData.set(VAR_SIZE_MULTIPLIER, sizeMult);
        this.refreshDimensions();
    }

    public int getVarColor() {
        return this.entityData.get(VAR_COLOR);
    }

    public void setVarColor(int i) {
        this.entityData.set(VAR_COLOR, i);
    }

    public int getGender() {
        return Mth.clamp(this.entityData.get(GENDER), 0, 2);
    }

    public void setGender(int i) {
        this.entityData.set(GENDER, i);
    }

    public int getEatLength() { return 20; }

    public boolean onHideableBlock(AnimaliaLandBase mob) {
        BlockPos pos = mob.blockPosition().below();
        BlockState blockState = mob.level().getBlockState(pos);
        return blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.SAND) || blockState.is(BlockTags.LUSH_GROUND_REPLACEABLE);
    }

    public boolean canHide() {
        return false;
    }

    public int getHideLength() {
        return 100;
    }

    public int getHideCooldown() {
        return 600 + random.nextInt(2000);
    }

    public int getHideTicks() {
        return this.hideTicks;
    }

    public int getHidePhase() {
        return this.entityData.get(HIDE_PHASE);
    }

    public void setHidePhase(int phase) {
        this.entityData.set(HIDE_PHASE, phase);
    }

    /** Override in subclass. Ticks for the burrowing-in animation. */
    public int getHideTransitionInLength() {
        return 20;
    }

    /** Override in subclass. Ticks for the surfacing animation. */
    public int getHideTransitionOutLength() {
        return 20;
    }

    public boolean isHiding() {
        return this.entityData.get(IS_HIDING);
    }

    public void setHiding(boolean hiding) {
        this.entityData.set(IS_HIDING, hiding);
    }

    /***
     * Override this method to add special conditions such as hiding in plants.
     */
    public boolean canStartHiding() {
        return this.canHide() && !this.isHiding() && this.hideCooldown <= 0;
    }

    public boolean isFast() {
        double speed = this.getDeltaMovement().horizontalDistance();
        double baseSpeed = this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        return speed > baseSpeed * 1.5;
    }

    public float getBabyScale() {
        return 0.3f;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float scale = this.getVarSizeMultiplier();
        if (this.isBaby()) {
            scale *= getBabyScale();
        }
        EntityDimensions scaled = super.getDimensions(pose).scale(scale);
        // Clamp minimum dimensions to prevent derendering and interaction issues
        float minSize = 0.2f;
        return EntityDimensions.scalable(
                Math.max(scaled.width, minSize),
                Math.max(scaled.height, minSize)
        );
    }

    /***
     * Override this to get a variant size. Default is random of 0.7 to 1.5 of base. Set base value in the renderer.
     * If there is NO random sizing for a creature, override this method to return 1, and set base value in the renderer.
     * @return
     */
    public float genVarSizeMultiplier() {
        return 1;
    }

    /***
     * Override this to get a random CM length, min = minimum cm, max = maximum cm, mode = most common average
     * @param
     */
    public float genVarSize(float min, float max, float mode) {
        float u = this.random.nextFloat();
        float c = (mode - min) / (max - min);

        if (u < c) {
            return min + (float) Math.sqrt(u * (max - min) * (mode - min));
        } else {
            return max - (float) Math.sqrt((1 - u) * (max - min) * (max - mode));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("VarSize", this.getVarSizeMultiplier());
        pCompound.putInt("Gender", this.getGender());
        pCompound.putInt("VarColor", this.getVarColor());
        if(this.canHide()) {
            pCompound.putInt("HidePhase", this.getHidePhase());
            pCompound.putInt("HideTicks", this.hideTicks);
            pCompound.putInt("HideCooldown", this.hideCooldown);
            pCompound.putInt("HideTransitionInTicks", this.hideTransitionInTicks);
            pCompound.putInt("HideTransitionOutTicks", this.hideTransitionOutTicks);
            pCompound.putBoolean("WantsToHide", this.wantsToHide);
            pCompound.putBoolean("IsHiding", this.isHiding());
        }
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setGender(pCompound.getInt("Gender"));
        this.setVarColor(pCompound.getInt("VarColor"));

        if(this.canHide()) {
            this.setHiding(pCompound.getBoolean("IsHiding"));
            this.setHidePhase(PHASE_NONE);
            this.setHiding(false);
            this.hideTicks = 0;
            this.hideTransitionInTicks = 0;
            this.hideTransitionOutTicks = 0;
            this.wantsToHide = false;
            // Give a short cooldown so it doesn't immediately re-hide on spawn
            this.hideCooldown = pCompound.getInt("HideCooldown") > 0
                    ? pCompound.getInt("HideCooldown")
                    : (this.canHide() ? 100 + random.nextInt(200) : 0);
        }

        if (!pCompound.contains("VarSize")) {
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        } else {
            this.setVarSizeMultiplier(pCompound.getFloat("VarSize")); }
        super.readAdditionalSaveData(pCompound);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.level().isClientSide && this.getHidePhase() != PHASE_NONE) {
                this.setHidePhase(PHASE_NONE);
                this.setHiding(false);
                this.hideTicks = 0;
                this.hideTransitionInTicks = 0;
                this.hideTransitionOutTicks = 0;
                this.wantsToHide = false;
                this.hideCooldown = this.getHideCooldown();
            }
            if (!this.level().isClientSide && this.isMovementLockedByIdle()) {
                this.setIdleDisplayTicks(0);
                this.setCurrentBodyIdle(-1);
                this.onMovementLockingIdleEnd();
            }
            return super.hurt(source, amount);
        }
    }

    @Override
    public int getIdleDisplayTicks() {
        return this.idleDisplayTicks;
    }

    @Override
    public void setIdleDisplayTicks(int ticks) {
        this.idleDisplayTicks = ticks;
    }

    @Override
    public int getTwitchIdleTicks() {
        return this.twitchIdleTicks;
    }

    @Override
    public void setTwitchIdleTicks(int ticks) {
        this.twitchIdleTicks = ticks;
    }

    @Override
    public int getCurrentBodyIdle() {
        return this.entityData.get(BODY_IDLE);
    }

    @Override
    public void setCurrentBodyIdle(int displayId) {
        this.entityData.set(BODY_IDLE, displayId);
    }

    @Override
    public int getCurrentTwitchIdle() {
        return this.entityData.get(TWITCH_IDLE);
    }

    @Override
    public void setCurrentTwitchIdle(int displayId) {
        this.entityData.set(TWITCH_IDLE, displayId);
    }

    @Override
    public boolean canPlayIdleDisplay() {
        return IIdles.super.canPlayIdleDisplay() && !this.isHiding() && !this.isGrazing();
    }

    @Override
    public boolean isAtRestForIdle(PathfinderMob mob) {
        return IIdles.super.isAtRestForIdle(mob) && this.onGround();
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isMovementLockedByIdle()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(pTravelVector);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.cooldown > 0) {
            --this.cooldown;
        }

        if (!this.level().isClientSide) {
            this.tickIdleDisplay(this);
        }

        if (!this.level().isClientSide && this.eatTicks > 0) {
            --this.eatTicks;
            if (this.eatTicks <= 0) {
                this.setEating(false);
            }
        }

        if (!this.level().isClientSide && this.grazeTicks > 0) {
            --this.grazeTicks;
            if (this.grazeTicks <= 0) {
                this.setGrazing(false);
            }
        }

        if (!this.level().isClientSide && this.canHide()) {
            if (this.hideCooldown > 0) {
                --this.hideCooldown;
            }

            int phase = this.getHidePhase();

            switch (phase) {
                case PHASE_NONE:
                    if (this.wantsToHide) {
                        if (this.onGround() && this.onHideableBlock(this)) {
                            this.setHidePhase(PHASE_BURROWING);
                            this.setHiding(true);
                            this.hideTransitionInTicks = this.getHideTransitionInLength();
                            this.getNavigation().stop();
                            this.wantsToHide = false;
                        } else if (this.onGround()) {
                            // On ground but wrong block - abort
                            this.wantsToHide = false;
                            this.hideCooldown = (int) (this.getHideCooldown() * 0.25f);
                        }
                    } else if (this.canStartHiding()) {
                        this.wantsToHide = true;
                    }
                    break;

                case PHASE_BURROWING:
                    this.getNavigation().stop();
                    --this.hideTransitionInTicks;
                    if (this.hideTransitionInTicks <= 0) {
                        this.setHidePhase(PHASE_BURROWED);
                        this.hideTicks = this.getHideLength();
                    }
                    break;

                case PHASE_BURROWED:
                    this.getNavigation().stop();
                    --this.hideTicks;
                    if (this.hideTicks <= 0) {
                        this.setHidePhase(PHASE_SURFACING);
                        this.hideTransitionOutTicks = this.getHideTransitionOutLength();
                    }
                    break;

                case PHASE_SURFACING:
                    this.getNavigation().stop();
                    --this.hideTransitionOutTicks;
                    if (this.hideTransitionOutTicks <= 0) {
                        this.setHidePhase(PHASE_NONE);
                        this.setHiding(false);
                        this.hideCooldown = this.getHideCooldown();
                    }
                    break;
            }
        }

        int i = this.getAge();
        if (i == 0) {
            this.refreshDimensions();
        }
    }

    public AnimaliaEggTypes getEggType() {
        return AnimaliaEggTypes.LIVE_BIRTH;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal mate) {
        switch (this.getEggType()) {
            case ITEM_EGG:
                this.dropEggItem();
                break;
            case LIVE_BIRTH:
                this.giveBirth(level, mate);
                break;
            case BLOCK_EGG:
                this.dropEggItem(); //TODO implement layEggInNest()
        }
        this.setAge(6000);
        mate.setAge(6000);
        this.resetLove();
        mate.resetLove();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    public void giveBirth(ServerLevel level, Animal mate) {
        if(!level.isClientSide) {
            EntityType<?> type = this.getType();
            Entity entity = type.create(level);
            if (!(entity instanceof AnimaliaLandBase baby)) return;

            baby.setBaby(true);
            baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            baby.setVarSizeMultiplier(this.genVarSizeMultiplier());
            baby.setGender(this.random.nextInt(2));
            this.finalizeSpawnChildFromBreeding(level, mate, baby);
            level.addFreshEntityWithPassengers(baby);
        }
    }

    /**
     * TODO Edit this method so it drops LandEggItem. These item eggs are used for insects and such.
     * Birds and lizards lay eggs in mounds/nests
     * @return
     */
    public Item getEggItem() {
        return ModItems.FISH_EGG.get();
    }

    public void dropEggItem() {
        if (this.level().isClientSide) return;

        ItemStack egg = new ItemStack(getEggItem());
        FishEggItem.setEntity(egg, this.getType());
        this.spawnAtLocation(egg);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int i = this.getAge();

        if (!this.level().isClientSide && i == 0 && this.canFallInLove() && this.isBreedingItem(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.setInLove(player);
            return InteractionResult.SUCCESS;
        }

        float healAmount = itemstack.getFoodProperties(this) != null
                ? Objects.requireNonNull(itemstack.getFoodProperties(this)).getNutrition() : 2;
        if (this.isFood(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal(healAmount);
            this.startEating();
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setVarColor(1);
        this.setGender(this.random.nextInt(2));
        this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

}
