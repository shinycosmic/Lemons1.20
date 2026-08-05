package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
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

public class RegSchoolingEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int SCATOPHAGUS_ARGUS_PIXEL = 14;
    private static final int NASO_BREVIROSTRIS_PIXEL = 33;
    private static final int POMACANTHUS_IMPERATOR_PIXEL = 22;

    public RegSchoolingEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return ModBlocks.ALGAE_MAT.get().asItem();
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Items.SPONGE;
        }
        return ModItems.FISH_FOOD.get();
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
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("trivia.animalia.scatophagus_argus");
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Component.translatable("trivia.animalia.pomacanthus_imperator");
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return Component.translatable("trivia.animalia.naso_brevirostris");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("family.animalia.scatophagidae");
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Component.translatable("family.animalia.pomacanthidae");
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return Component.translatable("family.animalia.acanthuridae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("order.animalia.acanthuriformes");
        }
        return Component.translatable("debug.animalia.order");
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .build();
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return new ItemStack(ModItems.SCATOPHAGUS_ARGUS_BUCKET.get());
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return new ItemStack(ModItems.POMACANTHUS_IMPERATOR_BUCKET.get());
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return new ItemStack(ModItems.NASO_BREVIROSTRIS_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    public int getEatLength() { return 5; }

    @Override
    public double getSchoolSeparationRange() {
        return 0.8;
    }
    @Override
    public double getSchoolFleeSpeedMultiplier() {
        return 2.5;
    }

    @Override
    public int getMaxSchoolSize() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return 6;
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return 12;
        }
        return super.getMaxSchoolSize();
    }

    @Override
    public boolean isSchoolingFish() {
        if(this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return false;
        }
        return true;
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return 24;
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return 24;
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return 24;
        }

        return Scannable.super.getScaleforGUI();

    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.6f);
    }

    public float getSwimSpeed() {
        return  1.2f;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SCATOPHAGUS_ARGUS_PIXEL, this.genVarSize(20, 45, 40));
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(POMACANTHUS_IMPERATOR_PIXEL, this.genVarSize(20, 50, 40));
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(NASO_BREVIROSTRIS_PIXEL, this.genVarSize(40, 60, 50));
        }
        return 1;
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.SCATOPHAGUS_ARGUS, Scannable.AppName.FISH, "Acanthuriformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
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
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
