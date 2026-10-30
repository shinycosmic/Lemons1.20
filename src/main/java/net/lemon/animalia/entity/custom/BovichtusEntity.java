package net.lemon.animalia.entity.custom;

import com.google.common.collect.ImmutableMap;
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

import java.util.Map;

public class BovichtusEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

//    private static final Map<EntityType, Integer> MOB_ID = ImmutableMap.<EntityType, Integer>builder()
//            .put(ModEntities.BOVICHTUS_VARIEGATUS.get(), 0)
//            .put(ModEntities.CONGIOPODUS_TORVUS.get(), 1).build();

    public BovichtusEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.45f)
                .build();
    }

    public float getSwimSpeed() {
        return  0.6f;
    }

    @Override
    public SpawnBand spawnBand() {return SpawnBand.ANY_FLOOR;}

    public int getWalkTime() {return 400 + random.nextInt(200);}

    @Override
    public TagKey<Item> getFoodTag() {return ModTags.Items.INVERTEBRATE;}

    @Override
    public Item getBreedingItem() {return ModItems.ARTEMIA.get();}

    @Override
    public ActivityTime activityTime() {return ActivityTime.NONE;}

    @Override
    public AppName getApp() {return AppName.FISH;}

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.BOVICHTUS_VARIEGATUS.get()) {
            return Component.translatable("trivia.animalia.bovichtus_variegatus");
        }
        return Component.translatable("trivia.animalia.congiopodus_torvus");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.BOVICHTUS_VARIEGATUS.get()) {
            return Component.translatable("trivia.animalia.bovichtus_variegatus");
        }
        return Component.translatable("family.animalia.congiopodidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.BOVICHTUS_VARIEGATUS.get()) {
            return 30;
        }
        return 26;
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.BOVICHTUS_VARIEGATUS.get()) {
            currScale *= 0.8f;
        } else {
            currScale *= 0.7f;
        }
        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.BOVICHTUS_VARIEGATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(20, 28);
        } else if (this.getType() == ModEntities.CONGIOPODUS_TORVUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(32, 78);
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.BOVICHTUS_VARIEGATUS, AppName.FISH, "Perciformes");
        HolonetEntities.register(ModEntities.CONGIOPODUS_TORVUS, AppName.FISH, "Perciformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(!this.isActuallyMoving() && !this.isBaby()) {
            if (this.isWalking()) {
                if (this.getGroundIdleSwitch() == 0) {
                    animationState.getController().setAnimation(RawAnimation.begin().then("idle0", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else {
                    animationState.getController().setAnimation(RawAnimation.begin().then("idle1", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
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


}
