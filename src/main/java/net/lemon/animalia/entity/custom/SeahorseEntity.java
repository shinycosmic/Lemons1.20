package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
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
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class SeahorseEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SeahorseEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .build();
    }

    public float getSwimSpeed() {
        return  0.8f;
    }

    @Override
    public SpawnBand spawnBand() {return SpawnBand.SHALLOW;}

    @Override
    public TagKey<Item> getFoodTag() {
        return null;
    }

    @Override
    public Item getBreedingItem() {
        return null;
    }

    @Override
    public ActivityTime activityTime() {return ActivityTime.NONE;}

    @Override
    public AppName getApp() {return AppName.FISH;}

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.hippocampus_ingens");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.syngnathidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.syngnathiformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return 35;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            currScale *= 0.8f;
        }
        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(34, 32);
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.HIPPOCAMPUS_INGENS, Scannable.AppName.FISH, "Syngnathiformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(!this.isActuallyMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
