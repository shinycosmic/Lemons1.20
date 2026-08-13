package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class PogonophryneEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private static final EntityDataAccessor<Boolean> IDLE_REST = SynchedEntityData.defineId(PogonophryneEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private int restTicks;
    private int cooldown;
    private static final int POGONOPHRYNE_MARMORATA_PIXEL = 19;

    private final Random rand = new Random();

    public PogonophryneEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    public float getSwimSpeed() {
        if(this.isBaby()) {
            return 1.2f;
        }
        if(this.getIsResting() && this.isWalking()) {
            return 0;
        }
        if(this.isWalking()) {
            return 0.6f;
        }
        return 0.4f;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.CRUSTACEAN;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.AMPHIPOD.get();
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .build();
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.pogonophryne_marmorata");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.harpagiferidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.POGONOPHRYNE_MARMORATA.get()) {
            return 40;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.9f);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.POGONOPHRYNE_MARMORATA, Scannable.AppName.FISH, "Perciformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.POGONOPHRYNE_MARMORATA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(POGONOPHRYNE_MARMORATA_PIXEL, 29);
        }
        return 1;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.POGONOPHRYNE_MARMORATA_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if(this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isWalking()) {
            if(this.getIsResting()) {
                controller.setAnimation(RawAnimation.begin().then("resting", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        }
        // SWIMMING
        controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState eatPredicate(AnimationState<T> state) {
        if (this.isEating() && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("eat", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_REST, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IdleRest", this.getIsResting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsResting(pCompound.getBoolean("IdleRest"));
    }

    private void setIsResting(boolean bool) {
        this.entityData.set(IDLE_REST, bool);
    }

    private boolean getIsResting() {
        return this.entityData.get(IDLE_REST);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(cooldown > 0) {
            cooldown = cooldown - rand.nextInt(3);
        }

        if(!this.level().isClientSide) {
            if(!this.isBaby()) {
                if (cooldown <= 0 && isWalking()) {
                    restTicks = 600 + rand.nextInt(2000);
                    this.setIsResting(true);
                }
                if (restTicks > 0 && getIsResting()) {
                    restTicks--;
                } else if (getIsResting()) {
                    cooldown = rand.nextInt(2000) + 600;
                    this.setIsResting(false);
                }
            }
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        this.cooldown = rand.nextInt(1000)+1000;
        this.restTicks = 0;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
