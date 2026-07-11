package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.BottomDwellingGoal;
import net.lemon.animalia.entity.ai.FishFrySwimmingGoal;
import net.lemon.animalia.entity.ai.FishHideGoal;
import net.lemon.animalia.entity.aimove.BottomDwellingMoveHelperController;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
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

public class MastacembelusEntity extends FishBase implements GeoEntity, Scannable {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int stillTicks = 0;
    private static final int FREEZE_DELAY = 10;

    public MastacembelusEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new BottomDwellingMoveHelperController(this);
    }

    @Override
    public float getSwimSpeed() {
        return 0.6f;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BottomDwellingGoal(this, 1.0D, 160, 8, 4));
        this.goalSelector.addGoal(2, new FishFrySwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(3, new FishHideGoal(this, this.activityTime()));        super.registerGoals();
    }

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

    @Override
    public String getScientificName() {
        if(this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return "Mastacembelus armatus";
        } else if (this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            return "Mastacembelus erythrotaenia";
        } else if (this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            return "Macrognathus siamensis";
        } else if (this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            return "Mastacembelus brichardi";
        } else if (this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            return "Sinobdella sinensis";
        }
        return "didnt work";
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
    public boolean canHide() {
        return true;
    }

    @Override
    public int getHideCooldown() {
        return super.getHideCooldown();
    }

    @Override
    public int getHideLength() {
        return 200 + random.nextInt(1000);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        if(this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return ModItems.RAW_FISH.get();
        } else if(this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            return ModItems.ARTEMIA.get();
        } else if(this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            return ModItems.ARTEMIA.get();
        }
        return ModItems.FISH_FOOD.get();
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
        if (this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return Component.translatable("trivia.animalia.mastacembelus_armatus");
        } else if (this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            return Component.translatable("trivia.animalia.mastacembelus_erythrotaenia");
        } else if (this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            return Component.translatable("trivia.animalia.macrognathus_siamensis");
        } else if (this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            return Component.translatable("trivia.animalia.mastacembelus_brichardi");
        } else if (this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            return Component.translatable("trivia.animalia.sinobdella_sinensis");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.mastacembelidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.synbranchiformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return 54;
        } else if (this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            return 50;
        } else if (this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()){
            return 90;
        } else if (this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()){
            return 88;
        } else if (this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()){
            return 88;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            currScale *= 1.6f;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            currScale *= 1.6f;
        } else if(this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            currScale *= 1.75f;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            currScale *= 1.75f;
        } else if(this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            currScale *= 1.7f;
        }

        return currScale;
    }

    @Override
    public int getXOffsetForGUI() {
        int offset = 0;
        if(this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            offset = -5;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            offset = -5;
        } else if(this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            offset = -1;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            offset = -8;
        } else if(this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            offset = -5;
        }

        return offset;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.MASTACEMBELUS_ARMATUS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.MASTACEMBELUS_ERYTHROTAENIA, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.MACROGNATHUS_SIAMENSIS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.MASTACEMBELUS_BRICHARDI, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.SINOBDELLA_SINENSIS, Scannable.AppName.FISH, "Synbranchiformes");

    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return new ItemStack(ModItems.MASTACEMBELUS_ARMATUS_BUCKET.get());
        } else if (this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            return new ItemStack(ModItems.MASTACEMBELUS_ERYTHROTAENIA_BUCKET.get());
        } else if (this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            return new ItemStack(ModItems.MACROGNATHUS_SIAMENSIS_BUCKET.get());
        } else if (this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            return new ItemStack(ModItems.MASTACEMBELUS_BRICHARDI_BUCKET.get());
        } else if (this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            return new ItemStack(ModItems.SINOBDELLA_SINENSIS_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
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

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
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
