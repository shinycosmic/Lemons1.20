package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.WaterStartleGoal;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class RoosterfishEntity extends FishBase implements GeoEntity, Scannable {

    private static final EntityDataAccessor<Boolean> IS_STARTLED = SynchedEntityData.defineId(RoosterfishEntity.class, EntityDataSerializers.BOOLEAN);

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int startleCooldown = 0;
    private WaterStartleGoal startleGoal;

    public RoosterfishEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof AvoidEntityGoal);
        this.startleGoal = new WaterStartleGoal(this, 5.0F, 1.8D, 50);
        this.goalSelector.addGoal(2, this.startleGoal);
        this.goalSelector.addGoal(3, new RandomSprintGoal(this));
    }

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
                .add(Attributes.MAX_HEALTH, 6D)
                .add(Attributes.MOVEMENT_SPEED, 0.5f)
                .build();
    }

    public float getSwimSpeed() {
        return this.isStartled() ? 1.6f : 1.0f;
    }

    @Override
    public String getScientificName() {
        return "Nematistius pectoralis";
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
        return ItemStack.EMPTY; // TODO: wire to ModItems.ROOSTERFISH_BUCKET.get()
    }

    public static void registerHolonet() {
        // TODO: uncomment once ModEntities.ROOSTERFISH exists
        // HolonetEntities.register(ModEntities.ROOSTERFISH, Scannable.AppName.FISH, "Carangiformes");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if (!this.isInWater()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isStartled()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swimfast", Animation.LoopType.LOOP));
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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