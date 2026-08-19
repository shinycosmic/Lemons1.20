package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.WaterDartBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class IndostomusEntity extends WaterDartBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int INDOSTOMUS_PARADOXUS_PIXEL = 16;


    public IndostomusEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .build();
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.SHALLOW;
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
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.HYDROCYNUS_GOLIATH.get()) {
            return 18;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.8f);
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.INDOSTOMUS_PARADOXUS, AppName.FISH, "Synbranchiformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.INDOSTOMUS_PARADOXUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(INDOSTOMUS_PARADOXUS_PIXEL, 20);
        }
        return 1;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.indostomus_paradoxus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.indostomidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.synbranchiformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.INDOSTOMUS_PARADOXUS_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (this.isMovementLockedByIdle() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle" + this.getCurrentBodyIdle(), Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        int twitch = this.getCurrentTwitchIdle();
        if (twitch >= 0 && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        state.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
