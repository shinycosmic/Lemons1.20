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
import net.minecraft.world.item.Items;
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
    private static final float CHAUDHURIA_CAUDATA_PIXEL = 30;

    private int stillTicks = 0;
    private static final int FREEZE_DELAY = 10;

    public SynbranchusEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    public int getEatLength() { return 10; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.isActuallyMoving()) {
                this.stillTicks = 0;
            } else {
                this.stillTicks++;
            }
        }
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
        this.goalSelector.addGoal(5, new FishHideGoal(this));
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
        if(this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return "Synbranchus marmoratus";
        } else if(this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return "Chaudhuria caudata";
        }
        return "didn't work";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        if(this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return ModItems.TADPOLE.get();
        } else if(this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return ModItems.FISH_FOOD.get();
        }
        return ModItems.FISH_FOOD.get();
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return 15;
        } else if (this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return 24;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.6f);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.SYNBRANCHUS_MARMORATUS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.CHAUDHURIA_CAUDATA, Scannable.AppName.FISH, "Synbranchiformes");
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
        if (this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return Component.translatable("trivia.animalia.synbranchus_marmoratus");
        } else if (this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return Component.translatable("trivia.animalia.chaudhuria_caudata");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return Component.translatable("family.animalia.synbranchidae");
        } else if (this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return Component.translatable("family.animalia.chaudhuriidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.synbranchiformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.SYNBRANCHUS_MARMORATUS.get()) {
            return new ItemStack(ModItems.SYNBRANCHUS_MARMORATUS_BUCKET.get());
        } else if (this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return new ItemStack(ModItems.CHAUDHURIA_CAUDATA_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
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
        return 200 + random.nextInt(2800);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "flop_controller", 0, this::flopPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState flopPredicate(AnimationState<T> state) {
        if (!this.isInWater() && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
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

        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        if (this.stillTicks >= FREEZE_DELAY) {
            animationState.getController().setAnimationSpeed(0.3);
        } else {
            animationState.getController().setAnimationSpeed(1.0);
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState eatPredicate(AnimationState<T> state) {
        if (this.isEating()) {
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
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.CHAUDHURIA_CAUDATA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CHAUDHURIA_CAUDATA_PIXEL, this.genVarSize(15, 18, 25));
        }
        return AnimaliaFunctionUtil.getScaleForSize(SYNBRANCHUS_MARMORATUS_PIXEL, this.genVarSize(50, 150, 80));
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
