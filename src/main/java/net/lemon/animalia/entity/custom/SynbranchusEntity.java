package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.FishHideGoal;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.ChainBuffer;
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
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
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

public class SynbranchusEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float SYNBRANCHUS_MARMORATUS_PIXEL = 48;

    public SynbranchusEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 0.4f;
        }
        return 0.6f;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FishHideGoal(this));
    }

    @Override
    public int getBurrowingLength() {
        return 50;
    }

    @Override
    public int getSurfacingLength() {
        return 60;
    }

    @Override
    public String getScientificName() {
        return "Synbranchus marmoratus";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        return null; //TODO NEED TO MAKE TADPOLE ITEM
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return 60;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 1.8f);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.SYNBRANCHUS_MARMORATUS, Scannable.AppName.FISH, "Synbranchiformes");
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NOCTURNAL;
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.synbranchus_marmoratus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.synbranchidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.synbranchiformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.SYNBRANCHUS_MARMORATUS_BUCKET.get());
    }

    @Override
    public boolean canHide() {
        return true;
    }

    @Override
    public int getHideCooldown() {
        return super.getHideCooldown();
    }

    @Override
    public int getHideLength() {
        return 2000;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        int phase = this.getHidePhase();

        // BURROWING
        if(phase == AnimaliaBreedableWater.PHASE_BURROWING) {
            animationState.getController().setAnimationSpeed(1.0);
            animationState.getController().setAnimation(RawAnimation.begin().then("burrowing", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        // BURROWED
        if(phase == AnimaliaBreedableWater.PHASE_BURROWED) {
            animationState.getController().setAnimationSpeed(1.0);
            animationState.getController().setAnimation(RawAnimation.begin().then("burrowed", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        // SURFACING
        if(phase == AnimaliaBreedableWater.PHASE_SURFACING) {
            animationState.getController().setAnimationSpeed(1.0);
            animationState.getController().setAnimation(RawAnimation.begin().then("surfacing", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }

        // Normal swim — freeze frame when not moving
        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        if(this.isActuallyMoving()) {
            animationState.getController().setAnimationSpeed(1.0);
        } else {
            animationState.getController().setAnimationSpeed(0.0);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public float genVarSize() {
        float min = 50;
        float max = 150;
        float mode = 100f;

        float u = this.random.nextFloat();
        float c = (mode - min) / (max - min);

        if (u < c) {
            return min + (float) Math.sqrt(u * (max - min) * (mode - min));
        } else {
            return max - (float) Math.sqrt((1 - u) * (max - min) * (max - mode));
        }
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(SYNBRANCHUS_MARMORATUS_PIXEL, this.genVarSize());
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        if(dataTag != null) {
            if(dataTag.contains("BucketVarSize")) this.setVarSizeMultiplier(dataTag.getFloat("BucketVarSize"));
            if(dataTag.contains("Age")) this.setAge(dataTag.getInt("Age"));
            if(dataTag.contains("BucketGender")) this.setGender(dataTag.getInt("BucketGender"));
            if(dataTag.contains("BucketVarColor")) this.setVarColor(dataTag.getInt("BucketVarColor"));
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
