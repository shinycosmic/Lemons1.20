package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.ai.SchoolBoidGoal;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class GrazeSchoolingEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int ZANCLUS_CORNUTUS_PIXEL = 15;
    private static final int PARACANTHURUS_HEPATUS_PIXEL = 23;

    public GrazeSchoolingEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return Items.SPONGE;
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return ModBlocks.ALGAE_MAT.get().asItem();
        }
        return ModItems.FISH_FOOD.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new GrazeGoal<>(this, 1.5f));
        super.registerGoals();
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
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return Component.translatable("trivia.animalia.zanclus_cornutus");
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return Component.translatable("trivia.animalia.paracanthurus_hepatus");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return Component.translatable("family.animalia.zanclidae");
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return Component.translatable("family.animalia.acanthuridae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.acanthuriformes");
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .build();
    }

    @Override
    public int getIdleDisplayCount() {
        return 0;
    }

    @Override
    public IdleType getIdleType(int displayId) {
//        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
//            return switch (displayId) {
//                default -> IdleType.MOVEMENT_POSITIVE;
//            };
//        }
        return IdleType.TWITCH;
    }

    @Override
    public int getIdleDisplayLength(int displayId) {
        return 70;
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return new ItemStack(ModItems.PARACANTHURUS_HEPATUS_BUCKET.get());
        } else if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return new ItemStack(ModItems.ZANCLUS_CORNUTUS_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    public int getEatLength() { return 10; }

    @Override
    public double getSchoolSeparationRange() {
        return 0.8;
    }
    @Override
    public double getSchoolFleeSpeedMultiplier() {
        return 2.5;
    }

    @Override
    public int getMaxSchoolSize() {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return 15;
        }
        return super.getMaxSchoolSize();
    }

    @Override
    public boolean isSchoolingFish() {
        if(this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return false;
        }
        return true;
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return 24;
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
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
        return  1.2f;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(ZANCLUS_CORNUTUS_PIXEL, this.genVarSize(28, 40, 33));
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(PARACANTHURUS_HEPATUS_PIXEL, this.genVarSize(20, 40, 30));
        }
        return 1;
    }

    @Override
    public double getGrazeReachSqr() {
        return 1.2D;
    }

    @Override
    public boolean isGrazableBlock(BlockState state) {
        if (this.getType() == ModEntities.ZANCLUS_CORNUTUS.get()) {
            return state.is(BlockTags.CORAL_BLOCKS) || state.is(BlockTags.CORALS) || state.is(BlockTags.WALL_CORALS);
        } else if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get()) {
            return state.is(ModBlocks.ALGAE_MAT.get());
        }
        return super.isGrazableBlock(state);
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.ZANCLUS_CORNUTUS, AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.PARACANTHURUS_HEPATUS, AppName.FISH, "Acanthuriformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("graze", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (this.getType() == ModEntities.PARACANTHURUS_HEPATUS.get() && this.isFast()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swimfast", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        int twitch = this.getCurrentTwitchIdle();
        if (twitch >= 0 && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        state.getController().forceAnimationReset();
        return PlayState.STOP;
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

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
