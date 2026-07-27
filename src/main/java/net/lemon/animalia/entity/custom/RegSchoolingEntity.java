package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
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

public class RegSchoolingEntity extends FishBase implements GeoEntity, Scannable {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float SCATOPHAGUS_ARGUS_PIXEL = 14;

    public RegSchoolingEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getScientificName() {
        if(this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return "Scatophagus argus";
        }
        return "";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        return ModBlocks.ALGAE_MAT.get().asItem();
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
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("trivia.animalia.scatophagus_argus");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("family.animalia.scatophagidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("order.animalia.acanthuriformes");
        }
        return Component.translatable("debug.animalia.order");
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .build();
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return new ItemStack(ModItems.SCATOPHAGUS_ARGUS_BUCKET.get());
        }
        return new ItemStack(ModItems.CHAENOCEPHALUS_ACERATUS_BUCKET.get());
    }

    @Override
    public int maxNeighbors() {
        return 2;
    }

    @Override
    public int getMaxSchoolSize() {
        return 6;
    }

    @Override
    public boolean isSchoolingFish() {
        return true;
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return 24;
        }

        return Scannable.super.getScaleforGUI();

    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.6f);
    }

    public float getSwimSpeed() {
        return  0.8f;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SCATOPHAGUS_ARGUS_PIXEL, this.genVarSize(20, 45, 40));
        }
        return 1;
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.SCATOPHAGUS_ARGUS, Scannable.AppName.FISH, "Acanthuriformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
