package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
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

import java.util.Random;

public class ChaenocephalusEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private int cooldown;
    private int CHAENOCEPHALUS_ACERATUS_PIXEL = 19;
    private int CYGNODRACO_MAWSONI_PIXEL = 19;

    private final Random rand = new Random();

    public ChaenocephalusEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getScientificName() {
        if(this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return "Chaenocephalus aceratus";
        }
        return "";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.RAW_ICEFISH.get();
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
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return Component.translatable("trivia.animalia.chaenocephalus_aceratus");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return Component.translatable("family.animalia.channichthyidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.CHAENOCEPHALUS_ACERATUS_BUCKET.get());
    }

    @Override
    public float getSwimSpeed() {
        if(this.isBaby()) {
            return 1.2f;
        }
        //if guarding nest, speed mult=0
//        if(this.getIsResting() && this.isWalking()) {
//            return 0;
//        }
        if(this.isWalking()) {
            return 0.6f;
        }
        return 0.4f;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return 40;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.9f);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.CHAENOCEPHALUS_ACERATUS, Scannable.AppName.FISH, "Perciformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CHAENOCEPHALUS_ACERATUS_PIXEL, this.genVarSize(34, 50, 40));
        }
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if(this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        //Guarding animations, there are right and left transitions.
        // SWIMMING
        controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
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
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        this.cooldown = rand.nextInt(1000)+1000;

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
