package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.AnimaliaEggTypes;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.registry.spawning.SpawnTime;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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

public class SeahorseEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_BROODING = SynchedEntityData.defineId(SeahorseEntity.class, EntityDataSerializers.BOOLEAN);

    private int broodTicks;

    public SeahorseEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FindNearestBlockGoal(this, 1.4D, 8, ModTags.Blocks.AQUATIC_PLANTS, 10));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_BROODING, false);
    }

    public boolean isBrooding() {
        return this.entityData.get(IS_BROODING);
    }

    public void setBrooding(boolean brooding) {
        this.entityData.set(IS_BROODING, brooding);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .build();
    }

    public float getSwimSpeed() {
        return  1.5f;
    }

    @Override
    public SpawnBand spawnBand() {return SpawnBand.SHALLOW;}

    @Override
    public AnimaliaEggTypes getEggType() {
        return AnimaliaEggTypes.LIVE_BIRTH;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimaliaBreedableWater partner) {
        this.setBrooding(true);
        this.broodTicks = 400 + this.random.nextInt(400);

        this.setAge(6000);
        partner.setAge(6000);
        this.resetLove();
        partner.resetLove();
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.isBrooding();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide || !this.isBrooding()) {
            return;
        }

        this.broodTicks--;
        if (this.broodTicks <= 0) {
            int num = 1 + this.random.nextInt(3);
            for (int i = 0; i < num; i++) {
                this.giveBirth((ServerLevel) this.level());
            }
            this.setBrooding(false);
        }
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.CRUSTACEAN;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.AMPHIPOD.get();
    }

    @Override
    public ActivityTime activityTime() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return ActivityTime.NOCTURNAL;
        }
        return ActivityTime.DIURNAL;
    }

    @Override
    public SpawnTime spawnTime() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return SpawnTime.NIGHT;
        }
        return super.spawnTime();
    }

    @Override
    public AppName getApp() {return AppName.FISH;}

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return Component.translatable("trivia.animalia.hippocampus_ingens");
        }
        return Component.translatable("trivia.animalia.hippocampus_reidi");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.syngnathidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.syngnathiformes");
    }

    @Override
    public int getScaleforGUI() {
        return 30;
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        currScale *= 0.8f;
        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.HIPPOCAMPUS_INGENS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(12, 20);
        } else if (this.getType() == ModEntities.HIPPOCAMPUS_REIDI.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(12, 12);
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.HIPPOCAMPUS_INGENS, Scannable.AppName.FISH, "Syngnathiformes");
        HolonetEntities.register(ModEntities.HIPPOCAMPUS_REIDI, Scannable.AppName.FISH, "Syngnathiformes");

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsBrooding", this.isBrooding());
        pCompound.putInt("BroodTicks", this.broodTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setBrooding(pCompound.getBoolean("IsBrooding"));
        this.broodTicks = pCompound.getInt("BroodTicks");
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putBoolean("BucketIsBrooding", this.isBrooding());
        compoundTag.putInt("BucketBroodTicks", this.broodTicks);
    }

    @Override
    public void loadFromBucketTag(CompoundTag pTag) {
        super.loadFromBucketTag(pTag);
        if (pTag.contains("BucketIsBrooding")) {
            this.setBrooding(pTag.getBoolean("BucketIsBrooding"));
            this.broodTicks = pTag.getInt("BucketBroodTicks");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "brood_controller", 10, this::broodPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

//        if(!this.isActuallyMoving()) {
//            animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
//            return PlayState.CONTINUE;
//        }
        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState broodPredicate(AnimationState<T> animationState) {
        if (this.isBrooding()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("brood", Animation.LoopType.LOOP));
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
