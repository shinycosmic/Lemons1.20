package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FishBreedGoal;
import net.lemon.animalia.entity.bases.helpers.*;
import net.lemon.animalia.item.FishEggItem;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.spawning.ISpawnTime;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.Scannable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public abstract class AnimaliaBreedableWater extends WaterAnimal implements IActivityTime, IFoodEater, IIdles, IGrazer, IDimorphism, ISpawnTime {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VAR_COLOR = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> VAR_SIZE_MULTIPLIER = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_EATING = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_GRAZING = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HIDE_PHASE = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BODY_IDLE = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TWITCH_IDLE = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);

    private int eatTicks = 0;
    private int grazeTicks = 0;
    private int wantsToGrazeUntil;
    private int hideTicks = 0;
    private int hideCooldown = 0;
    private int burrowingTicks = 0;
    private int surfacingTicks = 0;
    public boolean wantsToHide = false;
    public int cooldown = 0;
    public int growthTicks = -12000;
    private int inLove;
    private int idleDisplayTicks;
    private int twitchIdleTicks;

    //Constants for hiding logic
    public static final int PHASE_NONE = 0;
    public static final int PHASE_BURROWING = 1;
    public static final int PHASE_BURROWED = 2;
    public static final int PHASE_SURFACING = 3;
    @Nullable
    private UUID loveCause;

    protected AnimaliaBreedableWater(EntityType<? extends AnimaliaBreedableWater> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if(this.useSmoothControl()) {
            this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, this.hasVerticalDrift());
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
        }
    }

    protected void customServerAiStep() {
        if (this.eatAge() != 0) {
            this.inLove = 0;
        }

        super.customServerAiStep();
    }

    public String getScientificName() {
        return Scannable.getScientificName(this.getType());
    }

    public boolean useSmoothControl() {
        return true;
    }

    public boolean hasVerticalDrift() {
        return true;
    }

    public boolean sinksWhenIdle() {
        return true;
    }

    @Override
    public int getIdleTicks() {
        return this.idleDisplayTicks;
    }

    @Override
    public void setIdleTicks(int ticks) {
        this.idleDisplayTicks = ticks;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new FishBreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, this.foodIngredients(), false));
        //this.goalSelector.addGoal(4, new EatDroppedItemsGoal<>(this, 1.2D, 10.0F));
        super.registerGoals();
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return pSize.height * 0.65F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
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

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 18) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public int getTwitchTicks() {
        return this.twitchIdleTicks;
    }

    @Override
    public void setTwitchTicks(int ticks) {
        this.twitchIdleTicks = ticks;
    }

    @Override
    public int getCurrRegIdle() {
        return this.entityData.get(BODY_IDLE);
    }

    @Override
    public void setCurrRegIdle(int displayId) {
        this.entityData.set(BODY_IDLE, displayId);
    }

    @Override
    public int getCurrTwitchIdle() {
        return this.entityData.get(TWITCH_IDLE);
    }

    @Override
    public void setCurrTwitchIdle(int displayId) {
        this.entityData.set(TWITCH_IDLE, displayId);
    }

    @Override
    public boolean canPlayIdle() {
        return IIdles.super.canPlayIdle() && !this.isHiding() && !this.isGrazing();
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

    public boolean shouldJumpOnFlop() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return this.eatAge() < 0;
    }

    public int eatAge() {
        return this.entityData.get(AGE);
    }

    public void setEatAge(int i) {
        this.entityData.set(AGE, i);
    }

    /// Override this to control how long to grow into adult
    public void setBaby(boolean condition) {
        this.setEatAge(condition ? this.growthTicks : 0);
        this.refreshDimensions();
    }
    public AnimaliaEggTypes getEggType() {
        return AnimaliaEggTypes.ITEM_EGG;
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

    @Override
    public int getGender() {return Mth.clamp(this.entityData.get(GENDER), 0, 2);}

    @Override
    public void setGender(int i) {this.entityData.set(GENDER, i);}

    public int getEatLength() { return 20; }

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
        return this.isInWater() && !this.isHiding() && !this.isEating() && !this.isInLove()
                && !this.isMovementLockedByIdle();
    }
    public boolean wantsToGraze() {
        return this.tickCount < this.wantsToGrazeUntil;
    }

    public void grazeWindow(int ticks) {
        this.wantsToGrazeUntil = this.tickCount + ticks;
    }

    public void clearWantsToGraze() {
        this.wantsToGrazeUntil = 0;
    }

    public void onGrazeStart() {
    }

    public void onRandomGraze() {
    }

    public void onGrazeStop() {
    }

    public int getGrazeLength() { return 20; }

    public boolean onHideableBlock(AnimaliaBreedableWater mob) {
        BlockPos pos = mob.blockPosition().below();
        BlockState blockState = mob.level().getBlockState(pos);
        return blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.SAND) || blockState.is(Tags.Blocks.GRAVEL) || blockState.is(BlockTags.CONVERTABLE_TO_MUD);
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
    public int getBurrowingLength() {
        return 20;
    }

    /** Override in subclass. Ticks for the surfacing animation. */
    public int getSurfacingLength() {
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
     * @return
     */
    public boolean canStartHiding() {
        return this.canHide() && !this.isHiding() && this.hideCooldown <= 0;
    }

    public float getSwimSpeed() {
        if(this.isHiding()){
            return 0f;
        }
        return 1.0f;
    };

    public boolean isActuallyMoving() {
        return this.walkAnimation.isMoving();
    }

    public boolean isFast() {
        if (!this.isActuallyMoving()) {
            return false;
        }
        double dx = this.getX() - this.xo;
        double dy = this.getY() - this.yo;
        double dz = this.getZ() - this.zo;
        double speedSqr = dx * dx + dy * dy + dz * dz;
        double threshold = this.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.getSwimSpeed() * this.getFastSwimScale();
        return speedSqr > threshold * threshold;
    }

    public double getFastSwimScale() {
        return 1.5D;
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
        pCompound.putInt("Age", this.eatAge());
        pCompound.putInt("Gender", this.getGender());
        pCompound.putInt("VarColor", this.getVarColor());
        pCompound.putInt("InLove", this.inLove);

        if(this.canHide()) {
            pCompound.putInt("HidePhase", this.getHidePhase());
            pCompound.putInt("HideTicks", this.hideTicks);
            pCompound.putInt("HideCooldown", this.hideCooldown);
            pCompound.putInt("BurrowingTicks", this.burrowingTicks);
            pCompound.putInt("SurfacingTicks", this.surfacingTicks);
            pCompound.putBoolean("WantsToHide", this.wantsToHide);
            pCompound.putBoolean("IsHiding", this.isHiding());
        }

        if (this.loveCause != null) {
            pCompound.putUUID("LoveCause", this.loveCause);
        }
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.inLove = pCompound.getInt("InLove");
        this.loveCause = pCompound.hasUUID("LoveCause") ? pCompound.getUUID("LoveCause") : null;
        this.setEatAge(pCompound.getInt("Age"));
        this.setGender(pCompound.getInt("Gender"));
        this.setVarColor(pCompound.getInt("VarColor"));

        if(this.canHide()) {
            this.setHiding(pCompound.getBoolean("IsHiding"));
            this.setHidePhase(PHASE_NONE);
            this.setHiding(false);
            this.hideTicks = 0;
            this.burrowingTicks = 0;
            this.surfacingTicks = 0;
            this.wantsToHide = false;
            // Give a short cooldown so it doesn't immediately re-burrow on spawn
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
                this.burrowingTicks = 0;
                this.surfacingTicks = 0;
                this.wantsToHide = false;
                this.hideCooldown = this.getHideCooldown();
            }
            if (!this.level().isClientSide && this.isMovementLockedByIdle()) {
                this.setIdleTicks(0);
                this.setCurrRegIdle(-1);
                this.onMovementLockingIdleEnd();
            }
            this.inLove = 0;
            return super.hurt(source, amount);
        }
    }

    public float getBabyScale() {
        return 0.3f;
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide && this.cooldown > 0) {
            --this.cooldown;
        }

        if (!this.level().isClientSide) {
            this.tickIdle(this);
        }

        if (!this.level().isClientSide && (this.isMovementLockedByIdle() || this.isGrazing())) {
            this.setYRot(this.yRotO);
            this.yBodyRot = this.yBodyRotO;
            this.yHeadRot = this.yHeadRotO;
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
                            this.burrowingTicks = this.getBurrowingLength();
                            this.getNavigation().stop();
                            this.wantsToHide = false;
                        } else if (this.onGround()) {
                            this.wantsToHide = false;
                            this.hideCooldown = (int) (this.getHideCooldown() * 0.25f);
                        }
                    } else if (this.canStartHiding()) {
                        this.wantsToHide = true;
                    }
                    break;

                case PHASE_BURROWING:
                    this.getNavigation().stop();
                    --this.burrowingTicks;
                    if (this.burrowingTicks <= 0) {
                        this.setHidePhase(PHASE_BURROWED);
                        this.hideTicks = this.getHideLength();
                    }
                    break;

                case PHASE_BURROWED:
                    this.getNavigation().stop();
                    --this.hideTicks;
                    if (this.hideTicks <= 0) {
                        this.setHidePhase(PHASE_SURFACING);
                        this.surfacingTicks = this.getSurfacingLength();
                    }
                    break;

                case PHASE_SURFACING:
                    this.getNavigation().stop();
                    --this.surfacingTicks;
                    if (this.surfacingTicks <= 0) {
                        this.setHidePhase(PHASE_NONE);
                        this.setHiding(false);
                        this.hideCooldown = this.getHideCooldown();
                    }
                    break;
            }
        }


        if (this.eatAge() != 0) {
            this.inLove = 0;
        }

        int i = this.eatAge();
        if (i < 0) {
            ++i;
            this.setEatAge(i);
            if (i == 0) {
                this.refreshDimensions();
            }
        } else if (i > 0) {
            --i;
            this.setEatAge(i);
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (!this.level().isClientSide && this.inLove % 10 == 0) {
                this.level().broadcastEntityEvent(this, (byte)18);
            }
        }
        super.aiStep();
    }

    /***
     * cannibalized from Animal class. Used to set Food Item (to heal/grow)
     * @return
     */
    public abstract TagKey<Item> getFoodTag();

    public Ingredient foodIngredients() {
        return Ingredient.of(getFoodTag());
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(getBreedingItem());
    }

    public abstract Item getBreedingItem();

    public boolean eats(ItemStack stack) {
        return stack.is(getFoodTag());
    }


    protected void usePlayerItem(Player pPlayer, InteractionHand hand, ItemStack stack) {
        if (!pPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable Player pPlayer) {
        this.inLove = 600;
        if (pPlayer != null) {
            this.loveCause = pPlayer.getUUID();
        }

        this.level().broadcastEntityEvent(this, (byte)18);
    }

    public void setInLoveTime(int pInLove) {
        this.inLove = pInLove;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            Player player = this.level().getPlayerByUUID(this.loveCause);
            return player instanceof ServerPlayer ? (ServerPlayer)player : null;
        }
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(AnimaliaBreedableWater otherMob) {
        if (otherMob == this) {
            return false;
        } else if (otherMob.getType() != this.getType()) {
            return false;
        } else {
            return this.isInLove() && otherMob.isInLove();
        }
    }

    public void spawnChildFromBreeding(ServerLevel level, AnimaliaBreedableWater fish) {
        switch (this.getEggType()) {
            case ITEM_EGG:
                this.dropEggItem();
                break;
            case LIVE_BIRTH:
                this.giveBirth(level);
                break;
            case BLOCK_EGG:
                this.dropEggItem();
        }
        this.setEatAge(6000);
        fish.setEatAge(6000);
        this.resetLove();
        fish.resetLove();
    }

    public void giveBirth(ServerLevel level) {
        if(!level.isClientSide) {
            EntityType<?> type = this.getType();
            Entity entity = type.create(level);
            if (!(entity instanceof AnimaliaBreedableWater baby)) return;

            baby.setEatAge(this.growthTicks);
            baby.copyPosition(this);
            baby.setVarSizeMultiplier(this.genVarSizeMultiplier());
            baby.setGender(this.random.nextInt(2));
            level.addFreshEntity(baby);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int i = this.eatAge();

        if (!this.level().isClientSide && i == 0 && this.canFallInLove() && this.isBreedingItem(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.setInLove(player);
            return InteractionResult.SUCCESS;
        }
        float healAmount = itemstack.getFoodProperties(this) != null ? Objects.requireNonNull(itemstack.getFoodProperties(this)).getNutrition() : 2;

        if (this.eats(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal(healAmount);
            this.startEating();
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }

    public Item getEggItem() {
        return ModItems.FISH_EGG.get();
    }

    public void dropEggItem() {
        if (this.level().isClientSide) return;

        ItemStack egg = new ItemStack(getEggItem());
        FishEggItem.setEntity(egg, this.getType());
        this.spawnAtLocation(egg);
    }

    public SpawnBand spawnBand() {
        return SpawnBand.ANY_WATER;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            return super.checkSpawnRules(level, reason);
        }
        return this.spawnBand().test(level, this.blockPosition()) && this.checkSpawnTime();
    }

    private boolean checkSpawnTime() {
        return switch (this.spawnTime()) {
            case DAY -> this.level().isDay();
            case NIGHT -> this.level().isNight();
            default -> true;
        };
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.HOT_FLOOR) || super.isInvulnerableTo(source);
    }

    @Override
    public void onInsideBubbleColumn(boolean downwards) {
    }

    @Override
    public void onAboveBubbleCol(boolean downwards) {
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if(reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setGender(this.random.nextInt(2));
        }
        if((reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) && this.random.nextFloat() < 0.05f) {
            this.setBaby(true);
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
