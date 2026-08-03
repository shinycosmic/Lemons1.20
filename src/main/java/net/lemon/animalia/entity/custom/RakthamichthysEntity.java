package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
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

public class RakthamichthysEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int stillTicks = 0;
    private static final int FREEZE_DELAY = 10;

    public RakthamichthysEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.isActuallyMoving()) {
                this.stillTicks = 0;
            } else {
                this.stillTicks++;
            }
        }
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 0.6f;
        }
        return 0.75f;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .build();
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.FISH_FOOD.get();
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.rakthamichthys_indicus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.synbranchidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.synbranchiformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.RAKTHAMICHTHYS_INDICUS_BUCKET.get());
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.RAKTHAMICHTHYS_INDICUS.get()) {
            return 500;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 2f);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.RAKTHAMICHTHYS_INDICUS, Scannable.AppName.FISH, "Synbranchiformes");
    }

    @Override
    public boolean shouldJumpOnFlop() {
        return false;
    }

    public int getWalkTime() {
        return 800 + random.nextInt(2000); // 10-20 seconds default
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        if (this.stillTicks >= FREEZE_DELAY) {
            animationState.getController().setAnimationSpeed(0.3);
        } else {
            animationState.getController().setAnimationSpeed(1.0);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.ANY_FLOOR;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
