package net.lemon.animalia.entity.custom;

import com.eliotlash.mclib.math.functions.classic.Sin;
import net.lemon.animalia.entity.ai.BottomDwellingGoal;
import net.lemon.animalia.entity.ai.FishFrySwimmingGoal;
import net.lemon.animalia.entity.aimove.BottomDwellingMoveHelperController;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.interfaces.IActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class ToothfishEntity extends FishBase implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public ToothfishEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new BottomDwellingMoveHelperController(this);
    }

    @Override
    public boolean useSmoothControl() {
        return false;
    }

    @Override
    public String getScientificName() {
        if(this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return "Dissostichus eleginoides";
        } else if (this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return "Eleginops maclovinus";
        }
        return "didnt work";
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public ItemStack getBucketItemStack() {
        if(this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return new ItemStack(ModItems.CHILEANSEABASS_BUCKET.get());
        } else if(this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return new ItemStack(ModItems.ELEGINOPS_MACLOVINUS_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BottomDwellingGoal(this, 1.0D, 160, 8, 4));
        this.goalSelector.addGoal(2, new FishFrySwimmingGoal(this, 1.0D, 40));
        super.registerGoals();
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return super.getDefaultLootTable();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if(!this.isInWater()) {
            return PlayState.CONTINUE;
        } else if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.notothen.swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then(this.getSwimAnim(), Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    public String getSwimAnim() {
        if(this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return "animation.notothen.swim";
        } else { //ModEntities.ELEGINOPS_MACLOVINUS.get()
            return "animation.eleginops.swim";
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
        super.travel(pTravelVector);
    }

    @Override
    public float genVarSizeMultiplier() {
        float minCm = 70F;
        float maxCm = 230F;
        float modeCm = 80F; // most common size

        float u = this.random.nextFloat();
        float c = (modeCm - minCm) / (maxCm - minCm);

        float lengthCm;

        if (u < c) {
            lengthCm = minCm + (float)Math.sqrt(u * (maxCm - minCm) * (modeCm - minCm));
        } else {
            lengthCm = maxCm - (float)Math.sqrt((1 - u) * (maxCm - minCm) * (maxCm - modeCm));
        }

        // Convert cm to scale (assuming 230cm = scale 1.0) the scale would be the base size without variant sizing.
        float scale = lengthCm / 230;

        return scale;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    /***
     * Eleginops maclovinus: Amphipod
     * Dissostichus eleginoides: Any Fish
     */
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        if(this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return stack.is(ModItems.AMPHIPOD.get());
        }
        return stack.is(getFoodTag());
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        if(dataTag != null) {
            if(dataTag.contains("BucketVarSize")) {
                this.setVarSizeMultiplier(dataTag.getFloat("BucketVarSize"));
            }
            if(dataTag.contains("Age")) {
                this.setAge(dataTag.getInt("Age"));
            }
            if(dataTag.contains("BucketGender")) {
                this.setGender(dataTag.getInt("BucketGender"));
            }
            if(dataTag.contains("BucketVarColor")){
                this.setVarColor(dataTag.getInt("BucketVarColor"));
            }
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
