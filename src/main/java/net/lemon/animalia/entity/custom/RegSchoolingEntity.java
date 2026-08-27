package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.helpers.ActivityTime;
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

    public RegSchoolingEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return ModItems.FISH_FOOD.get();
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Items.SPONGE;
        } else if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get()) {
            return ModItems.WORM.get();
        } else if (this.getType() == ModEntities.SIGANUS_VULPINUS.get()) {
            return Items.SEAGRASS;
        } else if (this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return ModItems.RAW_CRUSTACEAN.get();
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
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("trivia.animalia.scatophagus_argus");
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Component.translatable("trivia.animalia.pomacanthus_imperator");
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return Component.translatable("trivia.animalia.naso_brevirostris");
        } else if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get()) {
            return Component.translatable("trivia.animalia.chelmon_rostratus");
        } else if (this.getType() == ModEntities.CHAETODON_AURIGA.get()) {
            return Component.translatable("trivia.animalia.chaetodon_auriga");
        } else if (this.getType() == ModEntities.SIGANUS_VULPINUS.get()) {
            return Component.translatable("trivia.animalia.siganus_vulpinus");
        } else if (this.getType() == ModEntities.ACANTHURUS_LEUCOSTERNON.get()) {
            return Component.translatable("trivia.animalia.acanthurus_leucosternon");
        } else if (this.getType() == ModEntities.ACANTHURUS_ACHILLES.get()) {
            return Component.translatable("trivia.animalia.acanthurus_achilles");
        } else if (this.getType() == ModEntities.ACANTHURUS_JAPONICUS.get()) {
            return Component.translatable("trivia.animalia.acanthurus_japonicus");
        } else if (this.getType() == ModEntities.ACANTHURUS_SOHAL.get()) {
            return Component.translatable("trivia.animalia.acanthurus_sohal");
        } else if (this.getType() == ModEntities.ACANTHURUS_COERULEUS.get()) {
            return Component.translatable("trivia.animalia.acanthurus_coeruleus");
        } else if (this.getType() == ModEntities.ACANTHURUS_LINEATUS.get()) {
            return Component.translatable("trivia.animalia.acanthurus_lineatus");
        } else if (this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return Component.translatable("trivia.animalia.leptobrama_muelleri");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()) {
            return Component.translatable("family.animalia.scatophagidae");
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return Component.translatable("family.animalia.pomacanthidae");
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()
                || this.getType() == ModEntities.ACANTHURUS_LINEATUS.get()
                || this.getType() == ModEntities.ACANTHURUS_SOHAL.get()
                || this.getType() == ModEntities.ACANTHURUS_COERULEUS.get()
                || this.getType() == ModEntities.ACANTHURUS_JAPONICUS.get()
                || this.getType() == ModEntities.ACANTHURUS_ACHILLES.get()
                || this.getType() == ModEntities.ACANTHURUS_LEUCOSTERNON.get()) {
            return Component.translatable("family.animalia.acanthuridae");
        } else if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get() || this.getType() == ModEntities.CHAETODON_AURIGA.get()) {
            return Component.translatable("family.animalia.chaetodontidae");
        }else if (this.getType() == ModEntities.SIGANUS_VULPINUS.get()) {
            return Component.translatable("family.animalia.siganidae");
        } else if (this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return Component.translatable("family.animalia.leptobramidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        if(this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return Component.translatable("order.animalia.carangiformes");
        }
        return Component.translatable("order.animalia.acanthuriformes");
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public int getIdleCount() {
        if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get()) {
            return 1;
        } else if (this.getType() == ModEntities.CHAETODON_AURIGA.get()) {
            return 1;
        } else if (this.getType() == ModEntities.SIGANUS_VULPINUS.get()) {
            return 1;
        }
        return 0;
    }

    @Override
    public IdleType getIdleType(int displayId) {
//        if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get()) {
//            return switch (displayId) {
//                default -> IdleType.MOVEMENT_POSITIVE;
//            };
//        }
        return IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return 70;
    }

    @Override
    public int getEatLength() { return 10; }

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
        } else if (this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return 20;
        } else if (this.getType() == ModEntities.ACANTHURUS_ACHILLES.get() || this.getType() == ModEntities.ACANTHURUS_JAPONICUS.get()) {
            return 2;
        } else if (this.getType() == ModEntities.ACANTHURUS_LEUCOSTERNON.get() || this.getType() == ModEntities.ACANTHURUS_LINEATUS.get()) {
            return 5;
        } else if (this.getType() == ModEntities.ACANTHURUS_COERULEUS.get()) {
            return 8;
        }
        return super.getMaxSchoolSize();
    }

    @Override
    public boolean isSchoolingFish() {
        if(this.getType() == ModEntities.NASO_BREVIROSTRIS.get()
                || this.getType() == ModEntities.SCATOPHAGUS_ARGUS.get()
                || this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()
                || this.getType() == ModEntities.ACANTHURUS_LEUCOSTERNON.get()
                || this.getType() == ModEntities.ACANTHURUS_JAPONICUS.get()
                || this.getType() == ModEntities.ACANTHURUS_LINEATUS.get()
                || this.getType() == ModEntities.ACANTHURUS_ACHILLES.get()
                || this.getType() == ModEntities.ACANTHURUS_COERULEUS.get()) {
            return true;
        }
        return false;
    }

    @Override
    public int getScaleforGUI() {
        return 24;

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
            return AnimaliaFunctionUtil.getScaleForSize(14, this.genVarSize(20, 45, 40));
        } else if (this.getType() == ModEntities.POMACANTHUS_IMPERATOR.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(22, this.genVarSize(20, 50, 40));
        } else if (this.getType() == ModEntities.NASO_BREVIROSTRIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(33, this.genVarSize(40, 80, 60));
        } else if (this.getType() == ModEntities.CHELMON_ROSTRATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(16, 22);
        } else if (this.getType() == ModEntities.CHAETODON_AURIGA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(16, 25);
        } else if (this.getType() == ModEntities.SIGANUS_VULPINUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(19, 25);
        } else if (this.getType() == ModEntities.LEPTOBRAMA_MUELLERI.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(19, 38);
        } else if (this.getType() == ModEntities.ACANTHURUS_LINEATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(24, 38);
        } else if (this.getType() == ModEntities.ACANTHURUS_SOHAL.get() || this.getType() == ModEntities.ACANTHURUS_COERULEUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(24, 40);
        } else if (this.getType() == ModEntities.ACANTHURUS_LEUCOSTERNON.get() || this.getType() == ModEntities.ACANTHURUS_JAPONICUS.get() || this.getType() == ModEntities.ACANTHURUS_ACHILLES.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(24, 25);
        }
        return 1;
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.SCATOPHAGUS_ARGUS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.POMACANTHUS_IMPERATOR, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.NASO_BREVIROSTRIS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.CHELMON_ROSTRATUS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.CHAETODON_AURIGA, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.SIGANUS_VULPINUS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.LEPTOBRAMA_MUELLERI, Scannable.AppName.FISH, "Carangiformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_LEUCOSTERNON, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_JAPONICUS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_ACHILLES, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_COERULEUS, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_SOHAL, Scannable.AppName.FISH, "Acanthuriformes");
        HolonetEntities.register(ModEntities.ACANTHURUS_LINEATUS, Scannable.AppName.FISH, "Acanthuriformes");

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        int twitch = this.getCurrTwitchIdle();
        if (twitch >= 0 && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        state.getController().forceAnimationReset();
        return PlayState.STOP;
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
