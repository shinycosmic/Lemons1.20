package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class WalkingCrustaceanEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public WalkingCrustaceanEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public int getStrollInterval() {
        return 40;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.INVERTEBRATE;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.AMPHIPOD.get();
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.MUNIDOPSIS_POLYMORPHA.get()) {
            return Component.translatable("trivia.animalia.munidopsis_polymorpha");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.munidopsidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.decapoda");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.MUNIDOPSIS_POLYMORPHA.get()) {
            return 30;
        }
        return 30;
    }

    @Override
    public int getScaleforDetailGUI() {
        return Scannable.super.getScaleforDetailGUI();
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(16, 20);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.MUNIDOPSIS_POLYMORPHA, AppName.FIELD, "Decapoda");
    }

    @Override
    public int getWalkTime() {
        return 400 + random.nextInt(500);
    }

    @Override
    public int getSwimTime() {
        return 0;
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 0.9f;
        }
        return 1.5f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();
        if (this.isActuallyMoving()) {
            controller.setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        controller.setAnimation(RawAnimation.begin().then("idle0", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .build();
    }

    @Override
    public boolean shouldJumpOnFlop() {
        return false;
    }

    @Override
    public boolean hasSwimToWalkTransition() {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public boolean startsWalking() {
        return true;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.ANY_FLOOR;
    }
}
