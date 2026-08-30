package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.ai.SleepGoal;
import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.SemiaquaticBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanSleep;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.entity.bases.helpers.IGrazer;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class HyemoschusEntity extends SemiaquaticBase implements GeoEntity, Scannable, ICanThreat, ICanSleep {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(HyemoschusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLEEP_PHASE = SynchedEntityData.defineId(HyemoschusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLEEP_IDLE = SynchedEntityData.defineId(HyemoschusEntity.class, EntityDataSerializers.INT);

    private SemiaquaticPanicGoal waterPanic;
    private LandPanicGoal landPanic;
    private boolean wasGrazing;
    public HyemoschusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
        this.entityData.define(SLEEP_PHASE, 0);
        this.entityData.define(SLEEP_IDLE, -1);
    }

    @Override
    protected void registerGoals() {
        this.waterPanic = new SemiaquaticPanicGoal(this, 1.5D, 200, 8.0D, 12) {
            @Override
            protected boolean canScan() {
                return HyemoschusEntity.this.isBaby();
            }
        };
        this.landPanic = new LandPanicGoal(this, 1.5D, 200, 8.0D) {
            @Override
            protected boolean canScan() {
                return HyemoschusEntity.this.isBaby();
            }
        };
        this.goalSelector.addGoal(1, this.waterPanic);
        this.goalSelector.addGoal(1, this.landPanic);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 8.0D, 2.0D, Integer.MAX_VALUE, 0, ThreatGoal.ThreatOutcome.FLEE,
                entity -> entity instanceof Player player && !player.isCreative() && !player.isCrouching()));
        this.goalSelector.addGoal(2, new SleepGoal(this));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
        super.registerGoals();
    }

    @Override
    public Item getBreedingItem() {
        return Items.APPLE;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FRUITS_SEEDS;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NOCTURNAL;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.hyemoschus_aquaticus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.tragulidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.artiodactyla");
    }

    @Override
    public int getScaleforGUI() {
        return 22;

    }

    public static void registerHolonet(){
//        HolonetEntities.register(ModEntities.HYEMOSCHUS_AQUATICUS, AppName.FIELD, "Artiodactyla");
    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.HYEMOSCHUS_AQUATICUS.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(21, 85);
//        }
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("graze", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                animationState.getController().setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
                animationState.getController().setAnimation(RawAnimation.begin().then("sleep", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_EXITING:
                animationState.getController().setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
        }
        if (!this.isBaby() && this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            animationState.getController().setAnimation(RawAnimation.begin()
                    .then("toThreat", Animation.LoopType.PLAY_ONCE)
                    .thenLoop("threat"));
            return PlayState.CONTINUE;
        }
        if (this.isRunning()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("run", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isInWater() && !this.onGround()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("diving", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.getCurrRegIdle() >= 0) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle2", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isActuallyMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        int twitch = this.getCurrTwitchIdle();
        if (twitch >= 0 && !this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.LOOP));
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
        if (dataTag == null) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }



    @Override
    public boolean isBottomWalker() {return true;}

    @Override
    public int depthTolerance() {return 5;}

    @Override
    public int getMaxAirSupply() {return 4800;}



    @Override
    public int getThreatPhase() {return this.entityData.get(THREAT_PHASE);}

    @Override
    public void setThreatPhase(int phase) {this.entityData.set(THREAT_PHASE, phase);}

    @Override
    public boolean canStartThreatening() {return !this.isInWater();}

    @Override
    public void onThreatFlee(LivingEntity threat) {
        this.waterPanic.panicFrom(threat.position());
        this.landPanic.panicFrom(threat.position());
        if (this.getCurrTwitchIdle() >= 0) {
            this.setTwitchTicks(0);
            this.setCurrTwitchIdle(-1);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !player.isCreative() && !player.isSpectator()) {
            this.waterPanic.panicFrom(player.position());
            this.landPanic.panicFrom(player.position());
        }
        super.playerTouch(player);
    }



    @Override
    public int getSleepPhase() {return this.entityData.get(SLEEP_PHASE);}

    @Override
    public void setSleepPhase(int phase) {this.entityData.set(SLEEP_PHASE, phase);}

    @Override
    public int getCurrentSleepIdle() {return this.entityData.get(SLEEP_IDLE);}

    @Override
    public void setCurrentSleepIdle(int sleepIdleId) {this.entityData.set(SLEEP_IDLE, sleepIdleId);}

    @Override
    public int getToSleepLength() {return 20;}

    @Override
    public int getUnSleepLength() {return 20;}



    @Override
    public int getIdleCount() {return 5;}

    @Override
    public IdleType getIdleType(int displayId) {
        return displayId == 2 ? IdleType.MOVEMENT_NEGATIVE : IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 0 -> 10 + this.random.nextInt(21);
            case 2 -> 10 + this.random.nextInt(11);
            case 3 -> 15;
            default -> 20 + this.random.nextInt(21);
        };
    }

    @Override
    public boolean canIdleInWater(int displayId) {
        return displayId == 2;
    }

    @Override
    public boolean canPlayIdle() {
        if (this.isAsleep() || this.isGrazing() || this.isRunning()) {
            return false;
        }
        return !this.isInWater() || this.onGround();
    }

    @Override
    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        if (type == IdleType.MOVEMENT_NEGATIVE) {
            return this.isThreatening() ? -1 : 2;
        }
        if (type != IdleType.TWITCH || this.isBaby()) {
            return -1;
        }
        if (this.isThreatening()) {
            return 3 + mob.getRandom().nextInt(2);
        }
        return mob.getRandom().nextInt(2);
    }



    @Override
    public int regChance() {return 100;}

    @Override
    public boolean isGrazableBlock(BlockState state) {return state.is(ModTags.Blocks.FORAGEABLE);}

    @Override
    public boolean canGraze() {return super.canGraze() && !this.isBaby();}

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.wasGrazing && !this.isGrazing()) {
                this.setCurrTwitchIdle(0);
                this.setTwitchTicks(20);
            }
            this.wasGrazing = this.isGrazing();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && this.isAlive()) {
            Vec3 from = source.getEntity() != null ? source.getEntity().position() : this.position();
            this.waterPanic.panicFrom(from);
            this.landPanic.panicFrom(from);
        }
        return result;
    }
}
