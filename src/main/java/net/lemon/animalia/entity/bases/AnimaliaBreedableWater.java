package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FishBreedGoal;
import net.lemon.animalia.entity.bases.interfaces.IActivityTime;
import net.lemon.animalia.item.FishEggItem;
import net.lemon.animalia.registry.ModItems;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public abstract class AnimaliaBreedableWater extends WaterAnimal implements IActivityTime {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VAR_COLOR = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> VAR_SIZE_MULTIPLIER = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_EATING = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(AnimaliaBreedableWater.class, EntityDataSerializers.BOOLEAN);

    private int eatTicks = 0;
    private int hideTicks = 0;
    private int hideCooldown = 0;
    public boolean wantsToHide = false;
    public int cooldown = 0;
    public int growthTicks = -12000;
    private int inLove;
    @Nullable
    private UUID loveCause;

    protected AnimaliaBreedableWater(EntityType<? extends AnimaliaBreedableWater> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if(this.useSmoothControl()) {
            this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
        }
    }

    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        super.customServerAiStep();
    }

    abstract public String getScientificName();

    public boolean useSmoothControl() {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, this.foodIngredients(), false));
        this.goalSelector.addGoal(2, new FishBreedGoal(this, 1.0D));
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
        this.entityData.define(IS_HIDING, false);
    }

    public boolean isEating() {
        return this.entityData.get(IS_EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(IS_EATING, eating);
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public int getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(int i) {
        this.entityData.set(AGE, i);
    }

    /// Override this to control how long to grow into adult
    public void setBaby(boolean condition) {
        this.setAge(condition ? this.growthTicks : 0);
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

    public boolean onHideableBlock(AnimaliaBreedableWater mob) {
        BlockPos pos = mob.blockPosition().below();
        BlockState blockState = mob.level().getBlockState(pos);
        return blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.SAND);
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
        return this.canHide() && !this.isHiding() && this.hideTicks <= 0;
    }

    /***
     * Override this to get a variant size. Default is random of 0.7 to 1.5 of base. Set base value in the renderer.
     * If there is NO random sizing for a creature, override this method to return 1, and set base value in the renderer.
     * @return
     */
    public float genVarSizeMultiplier() {
        float value = 1.0f + (float) random.nextGaussian() * 0.25f;
        return Mth.clamp(value, 0.7f, 1.5f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("VarSize", this.getVarSizeMultiplier());
        pCompound.putInt("Age", this.getAge());
        pCompound.putInt("Gender", this.getGender());
        pCompound.putInt("VarColor", this.getVarColor());
        pCompound.putInt("InLove", this.inLove);
        pCompound.putInt("HideTicks", this.hideTicks);
        pCompound.putInt("HideCooldown", this.hideCooldown);
        pCompound.putBoolean("WantsToHide", this.wantsToHide);
        pCompound.putBoolean("IsHiding", this.isHiding());
        if (this.loveCause != null) {
            pCompound.putUUID("LoveCause", this.loveCause);
        }
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.inLove = pCompound.getInt("InLove");
        this.loveCause = pCompound.hasUUID("LoveCause") ? pCompound.getUUID("LoveCause") : null;
        this.setAge(pCompound.getInt("Age"));
        this.setGender(pCompound.getInt("Gender"));
        this.setVarColor(pCompound.getInt("VarColor"));
        this.hideTicks = pCompound.getInt("HideTicks");
        this.hideCooldown = pCompound.getInt("HideCooldown");
        this.wantsToHide = pCompound.getBoolean("WantsToHide");
        this.setHiding(pCompound.getBoolean("IsHiding"));
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
            this.inLove = 0;
            return super.hurt(source, amount);
        }
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide && this.cooldown > 0) {
            --this.cooldown;
        }

        if (!this.level().isClientSide && this.eatTicks > 0) {
            --this.eatTicks;
            if (this.eatTicks <= 0) {
                this.setEating(false);
            }
        }
        //TODO Implement Mastacembelus as a test for this AI, we need to override some methods, and apply hiding animation playing logic.
        // Also need to add logic to bottom swimmers with smooth swimming to target the ground when wanting to hide.
        if(!this.level().isClientSide && this.canHide()) {
            if(this.hideCooldown > 0) {
                --this.hideCooldown;
            }
            if(this.isHiding()){
                --this.hideTicks;
                this.getNavigation().stop();

                if (this.hideTicks <= 0) {
                    this.setHiding(false);
                    this.hideCooldown = this.getHideCooldown();
                }
            } else if (this.wantsToHide) {
                if(this.onGround() && this.onHideableBlock(this)) {
                    this.setHiding(true);
                    this.hideTicks = this.getHideLength();
                    this.getNavigation().stop();
                    this.wantsToHide = false;
                } else if (this.onGround()) {
                    this.wantsToHide = false;
                    this.hideCooldown = (int) (this.getHideCooldown()*0.25f);
                }
            } else if (this.canStartHiding()) {
                this.wantsToHide = true;
            }
        }


        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        int i = this.getAge();
        if (i < 0) {
            ++i;
            this.setAge(i);
        } else if (i > 0) {
            --i;
            this.setAge(i);
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
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

    /***
     * Used to set breeding Item
     * @return
     */
    public abstract boolean isBreedingItem(ItemStack stack);

    public boolean isFood(ItemStack stack) {
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
        } else if (otherMob.getClass() != this.getClass()) {
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
        this.setAge(6000);
        fish.setAge(6000);
        this.resetLove();
        fish.resetLove();
    }

    public void giveBirth(ServerLevel level) {
        if(!level.isClientSide) {
            EntityType<?> type = this.getType();
            Entity entity = type.create(level);
            if (!(entity instanceof AnimaliaBreedableWater baby)) return;

            baby.setAge(this.growthTicks);
            baby.copyPosition(this);
            baby.setVarSizeMultiplier(this.genVarSizeMultiplier());
            baby.setGender(this.random.nextInt(2));
            level.addFreshEntity(baby);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int i = this.getAge();

        if (!this.level().isClientSide && i == 0 && this.canFallInLove() && this.isBreedingItem(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.setInLove(player);
            return InteractionResult.SUCCESS;
        }

        if (this.isFood(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal((float) Objects.requireNonNull(itemstack.getFoodProperties(this)).getNutrition());
            this.setEating(true);
            this.eatTicks = getEatLength();

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
}
