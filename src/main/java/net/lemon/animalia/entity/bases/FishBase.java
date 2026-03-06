package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.aimove.BottomDwellingMoveHelperController;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class FishBase extends AbstractFish implements IActivityTime {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VAR_COLOR = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> VAR_SIZE_MULTIPLIER = SynchedEntityData.defineId(FishBase.class, EntityDataSerializers.FLOAT);
    public int cooldown = 0;
    public int growthTicks = -24000;
    private int inLove;
    @Nullable
    private UUID loveCause;
    //TODO bring schooling into this consolidated base
    @Nullable
    private FishBase leader;
    private int schoolSize = 1;

    public FishBase(EntityType<? extends AbstractFish> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.setCanPickUpLoot(true);
    }

    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        super.customServerAiStep();
    }

    abstract public String getScientificName();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(GENDER, 0);
        this.entityData.define(VAR_COLOR, 0);
        this.entityData.define(VAR_SIZE_MULTIPLIER, 1.0f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new FishBreedGoal(this, 1.0D));
        super.registerGoals();
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putFloat("BucketVarSize", this.getVarSizeMultiplier());
        compoundTag.putInt("Age", this.getAge());
        compoundTag.putInt("BucketGender", this.getGender());
        compoundTag.putInt("BucketVarColor", this.getVarColor());
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
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

    public int getGender() {
        return Mth.clamp(this.entityData.get(GENDER), 0, 2);
    }

    public void setGender(int i) {
        this.entityData.set(GENDER, i);
    }

    public boolean onHideableBlock(FishBase fish) {
        BlockPos pos = fish.blockPosition().below();
        BlockState blockState = fish.level().getBlockState(pos);
        return blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.SAND);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("VarSize", this.getVarSizeMultiplier());
        pCompound.putInt("Age", this.getAge());
        pCompound.putInt("Gender", this.getGender());
        pCompound.putInt("VarColor", this.getVarColor());
        pCompound.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            pCompound.putUUID("LoveCause", this.loveCause);
        }
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setAge(pCompound.getInt("Age"));
        this.setGender(pCompound.getInt("Gender"));
        this.setVarColor(pCompound.getInt("VarColor"));
        this.inLove = pCompound.getInt("InLove");
        this.loveCause = pCompound.hasUUID("LoveCause") ? pCompound.getUUID("LoveCause") : null;
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

        if (this.getAge() != 0) {
            this.inLove = 0;
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


        if (this.isAlive()) {
            if (this.level().isClientSide) {

            }
            int i = this.getAge();
            if (i < 0) {
                ++i;
                this.setAge(i);
            } else if (i > 0) {
                --i;
                this.setAge(i);
            }

            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            }

        }

        super.aiStep();
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        super.travel(pTravelVector);
    }

    /***
     * cannibalized from Animal class. Used to set breeding Item
     * @return
     */
    public abstract TagKey<Item> getFoodTag();

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

    public boolean canMate(FishBase otherFish) {
        if (otherFish == this) {
            return false;
        } else if (otherFish.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isInLove() && otherFish.isInLove();
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isFood(itemstack)) {
            int i = this.getAge();
            if (!this.level().isClientSide && i == 0 && this.canFallInLove()) {
                this.usePlayerItem(player, hand, itemstack);
                this.setInLove(player);
                return InteractionResult.SUCCESS;
            }

            this.usePlayerItem(player, hand, itemstack);
            this.heal((float) Objects.requireNonNull(itemstack.getFoodProperties(this)).getNutrition());

            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        return super.mobInteract(player, hand);
    }

    public void spawnChildFromBreeding(ServerLevel level, FishBase fish) {
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
            if (!(entity instanceof FishBase baby)) return;

            baby.setAge(this.growthTicks);
            baby.copyPosition(this);
            baby.setVarSizeMultiplier(this.genVarSizeMultiplier());
            baby.setGender(this.random.nextInt(2));
            level.addFreshEntity(baby);
        }
    }

    public void dropEggItem() {
        if(this.level().isClientSide) return;

        ItemStack egg = new ItemStack(ModItems.FISH_EGG.get());

        FishEggItem.setEntity(egg, this.getType());

        this.spawnAtLocation(egg);
    }

    /***
     * Override this to get a variant size. Default is random of 0.7 to 1.5 of base. Set base value in the renderer.
     * If there is NO random sizing for a creature, override this method to return 1, and set base value in the renderer.
     * @return
     */
    public float genVarSizeMultiplier() {
        return Mth.clamp((float) random.nextGaussian(), 0.7f, 1.5f);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if(reason != MobSpawnType.BUCKET) {
            this.setGender(this.random.nextInt(2));
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    //TODO HANDLE DAY_NIGHT ACTIVITY
}
