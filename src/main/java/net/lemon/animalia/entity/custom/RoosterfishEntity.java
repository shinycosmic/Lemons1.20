package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.WaterStartleGoal;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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

public class RoosterfishEntity extends FishBase implements GeoEntity, Scannable {

    private static final EntityDataAccessor<Boolean> IS_STARTLED = SynchedEntityData.defineId(RoosterfishEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int NEMATISTIUS_PECTORALIS_PIXEL = 41;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int startleCooldown = 0;
    private WaterStartleGoal startleGoal;

    public RoosterfishEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof AvoidEntityGoal);
        this.startleGoal = new WaterStartleGoal(this, 5.0F, 2D, 100);
        this.goalSelector.addGoal(4, this.startleGoal);
        this.goalSelector.addGoal(6, new RandomSprintGoal(this));
    }

    public int getEatLength() { return 5; }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_STARTLED, false);
    }

    public boolean isStartled() {
        return this.entityData.get(IS_STARTLED);
    }

    public void setStartled(boolean startled) {
        this.entityData.set(IS_STARTLED, startled);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (startleCooldown > 0) startleCooldown--;

            if (startleGoal.isActive() && !isStartled()) {
                setStartled(true);
            } else if (!startleGoal.isActive() && isStartled() && startleCooldown <= 0) {
                setStartled(false);
            }
        }
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.MOVEMENT_SPEED, 0.8f)
                .build();
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.SHALLOW;
    }

    public float getSwimSpeed() {
        return this.isStartled() ? 4.5f : 2f;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.RAW_FISH.get();
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
        return Component.translatable("trivia.animalia.nematistius_pectoralis");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.nematistiidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carangiformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.NEMATISTIUS_PECTORALIS_BUCKET.get());
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.NEMATISTIUS_PECTORALIS.get()) {
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
         HolonetEntities.register(ModEntities.NEMATISTIUS_PECTORALIS, Scannable.AppName.FISH, "Carangiformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.NEMATISTIUS_PECTORALIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(NEMATISTIUS_PECTORALIS_PIXEL, this.genVarSize(160, 200, 180));
        }
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isStartled()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swimfast", Animation.LoopType.LOOP));
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        }
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

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    static class RandomSprintGoal extends Goal {
        private final RoosterfishEntity fish;
        private int cooldown;
        private int duration;

        public RandomSprintGoal(RoosterfishEntity fish) {
            this.fish = fish;
            this.cooldown = getRandomCooldown();
        }

        private int getRandomCooldown() {
            return 100 + fish.getRandom().nextInt(200);
        }

        @Override
        public boolean canUse() {
            if (!fish.isInWater()) return false;
            if (fish.isStartled()) return false;
            if (fish.startleCooldown > 0) return false;
            return --cooldown <= 0;
        }

        @Override
        public void start() {
            fish.setStartled(true);
            this.duration = 40 + fish.getRandom().nextInt(60);
        }

        @Override
        public boolean canContinueToUse() {
            return --duration > 0 && fish.isInWater();
        }

        @Override
        public void stop() {
            fish.setStartled(false);
            fish.startleCooldown = 30;
            this.cooldown = getRandomCooldown();
        }
    }
}