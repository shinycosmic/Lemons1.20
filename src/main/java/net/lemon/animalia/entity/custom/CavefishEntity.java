package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModBlocks;
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

public class CavefishEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final int AMBLYOPSIS_HOOSIERI_PIXEL = 15;
    private final int CYPRINODON_DIABOLIS_PIXEL = 12;
    private final int KRYPTOGLANIS_SHAJII_PIXEL = 24;
    private final int SINOCYCLOCHEILUS_PIXEL = 22;
    private final int GITCHAK_NAKANA_PIXEL = 13;


    public CavefishEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.5f)
                .build();
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.CAVE_WATER;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return ModTags.Items.CRUSTACEAN;
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return ModTags.Items.FISH_FOOD;
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return ModTags.Items.INVERTEBRATE;
        }
        return ModTags.Items.AQUATIC_PLANT;
    }

    @Override
    public Item getBreedingItem() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return ModItems.AMPHIPOD.get();
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return ModItems.FISH_FOOD.get();
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return ModItems.WORM.get();
        }
        return ModBlocks.ALGAE_MAT.get().asItem();
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
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("trivia.animalia.amblyopsis_hoosieri");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_anatirostris");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_hyalinus");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_longicornus");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("trivia.animalia.cyprinodon_diabolis");
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return Component.translatable("trivia.animalia.gitchak_nakana");
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return Component.translatable("trivia.animalia.kryptoglanis_shajii");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    //todo
    @Override
    public Component getFamily() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("family.animalia.amblyopsidae");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("family.animalia.cyprinidae");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("family.animalia.cyprinidae");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("family.animalia.cyprinidae");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("family.animalia.cyprinodontidae");
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return Component.translatable("family.animalia.cobitidae");
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return Component.translatable("family.animalia.kryptoglanidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("order.animalia.percopsiformes");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("order.animalia.cypriniformes");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("order.animalia.cypriniformes");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("order.animalia.cypriniformes");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("order.animalia.cyprinodontiformes");
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return Component.translatable("order.animalia.cypriniformes");
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return Component.translatable("order.animalia.siluriformes");
        }
        return Component.translatable("debug.animalia.order");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return 35;
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return 35;
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()){
            return 35;
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()){
            return 35;
        } else if (this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()){
            return 35;
        } else if (this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()){
            return 35;
        } else if (this.getType() == ModEntities.GITCHAK_NAKANA.get()){
            return 35;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            currScale *= 0.8f;
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            currScale *= 0.85f;
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            currScale *= 0.84f;
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            currScale *= 0.83f;
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            currScale *= 0.9;
        } else if(this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            currScale *= 1.2;
        } else if(this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            currScale *= 0.81f;
        }

        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(AMBLYOPSIS_HOOSIERI_PIXEL, 20);
        } else if (this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CYPRINODON_DIABOLIS_PIXEL, 13);
        } else if (this.getType() == ModEntities.GITCHAK_NAKANA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(GITCHAK_NAKANA_PIXEL, 15);
        } else if (this.getType() == ModEntities.KRYPTOGLANIS_SHAJII.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(KRYPTOGLANIS_SHAJII_PIXEL, 20);
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SINOCYCLOCHEILUS_PIXEL, 25);
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SINOCYCLOCHEILUS_PIXEL, 22);
        } else if (this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SINOCYCLOCHEILUS_PIXEL, 22);
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.AMBLYOPSIS_HOOSIERI, Scannable.AppName.FISH, "Percopsiformes");
        HolonetEntities.register(ModEntities.CYPRINODON_DIABOLIS, Scannable.AppName.FISH, "Cyprinodontiformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS, Scannable.AppName.FISH, "Cypriniformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_HYALINUS, Scannable.AppName.FISH, "Cypriniformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_LONGICORNUS, Scannable.AppName.FISH, "Cypriniformes");
        HolonetEntities.register(ModEntities.GITCHAK_NAKANA, Scannable.AppName.FISH, "Cypriniformes");
        HolonetEntities.register(ModEntities.KRYPTOGLANIS_SHAJII, Scannable.AppName.FISH, "Siluriformes");

    }

    @Override
    public ItemStack getBucketItemStack() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (this.isFast()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swimFast", Animation.LoopType.LOOP));
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
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
