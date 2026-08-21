package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import software.bernie.geckolib.core.object.PlayState;

public class AenigmachannaEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int idleSwitch = 0;

    public AenigmachannaEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.45f)
                .build();
    }

    public float getSwimSpeed() {
        return  0.8f;
    }

    @Override
    public SpawnBand spawnBand() {return SpawnBand.CAVE_WATER;}

    public int getWalkTime() {return 400 + random.nextInt(200);}

    @Override
    public TagKey<Item> getFoodTag() {return ModTags.Items.CRUSTACEAN;}

    @Override
    public Item getBreedingItem() {return ModItems.ARTEMIA.get();}

    @Override
    public ActivityTime activityTime() {return ActivityTime.NONE;}

    @Override
    public AppName getApp() {return AppName.FISH;}

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.aenigmachanna_gollum");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.aenigmachannidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.anabantiformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.AENIGMACHANNA_GOLLUM.get()) {
            return 35;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.AENIGMACHANNA_GOLLUM.get()) {
            currScale *= 0.8f;
        }
        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.AENIGMACHANNA_GOLLUM.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(34, 32);
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.AENIGMACHANNA_GOLLUM, Scannable.AppName.FISH, "Anabantiformes");

    }

    @Override
    public void aiStep() {
        if (this.isInWater() && !this.level().isClientSide) {
            if(this.wantsToWalk && this.idleSwitch == 0) {

            }
        }
        super.aiStep();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("beached", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(!this.isActuallyMoving()) {
            if (this.isWalking()) {
                if (this.getGroundIdleSwitch() == 0) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("idle2", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else {
                    animationState.getController().setAnimation(RawAnimation.begin().then("idle3", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
            } else {
                animationState.getController().setAnimation(RawAnimation.begin().then("idle0", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
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

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
