package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class CongolliEntity extends BottomWalkerSwimmerBase implements GeoEntity {
    private static final EntityDataAccessor<Boolean> IDLE_SAND = SynchedEntityData.defineId(CongolliEntity.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int dartTicks = 2000;
    private int dartCooldown = 0;
    private int ambientTicks;
    private int idleSandLength = 30;
    private boolean isDarting = false;
    private int sandTimer = idleSandLength;


    private final Random rand = new Random();


    public CongolliEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

    }

    private boolean isOnSeafloor() {
        return this.onGround();
    }

    @Override
    public String getScientificName() {
        if(this.getType() == ModEntities.PSEUDAPHRITIS_URVILLII.get()) {
            return "Pseudaphritis urvillii";
        }
        return "";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(ModTags.Items.CRUSTACEAN);
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.SALMON_BUCKET);
    } //TODO

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public int getWalkTime() {
        return 2000 + random.nextInt(1000);
    }

    @Override
    public int getSwimTime() {
        return 500 + random.nextInt(1000);
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 1.8f;
        }
        return 0.4f;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if(ambientTicks > 0) {
            ambientTicks = ambientTicks - rand.nextInt(3);
        }

        //handle sand state
        if(!this.level().isClientSide) {
            if (ambientTicks <= 0 && !getIsIdleSand() && isWalking()) {
                sandTimer = idleSandLength;
                this.setIsIdleSand(true);
            }
            if (sandTimer > 0 && getIsIdleSand()) {
                sandTimer--;
            } else if(getIsIdleSand()){
                ambientTicks = rand.nextInt(1000)+300;
                this.setIsIdleSand(false);
            }
        }

    }

    private void setIsIdleSand(boolean bool) {
        this.entityData.set(IDLE_SAND, bool);
    }

    private boolean getIsIdleSand() {
        return this.entityData.get(IDLE_SAND);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_SAND, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IdleSand", this.getIsIdleSand());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsIdleSand(pCompound.getBoolean("IdleSand"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if(this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("animation.notothen.swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isWalking()) {
            if (this.isActuallyMoving()) {
                controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else {
                if(this.getIsIdleSand()) {
                    controller.setAnimation(RawAnimation.begin().then("sand", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
                controller.setAnimation(RawAnimation.begin().then("alert", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        } else {
            // SWIMMING
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        if(dataTag != null) {
            if(dataTag.contains("BucketVarSize")) this.setVarSizeMultiplier(dataTag.getFloat("BucketVarSize"));
            if(dataTag.contains("Age")) this.setAge(dataTag.getInt("Age"));
            if(dataTag.contains("BucketGender")) this.setGender(dataTag.getInt("BucketGender"));
            if(dataTag.contains("BucketVarColor")) this.setVarColor(dataTag.getInt("BucketVarColor"));
        }
        this.ambientTicks = rand.nextInt(1000)+1000;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
