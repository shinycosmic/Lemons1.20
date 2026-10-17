package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.AnimaliaSound;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class GalictisEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(GalictisEntity.class, EntityDataSerializers.INT);
    private LandPanicGoal landPanic;
    private int barkCooldown;

    public GalictisEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.MOVEMENT_SPEED, 0.13f)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.landPanic = new LandPanicGoal(this, 2D, 200, 8.0D);
        this.goalSelector.addGoal(1, this.landPanic);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 12.0D, 0.0D, 160 + this.random.nextInt(141), 0, ThreatGoal.ThreatOutcome.FLEE, entity -> entity instanceof Player player && !player.isCreative()));
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.GALICTIS_VITTATA, AppName.FIELD, "Caniformia");
        HolonetEntities.register(ModEntities.GALICTIS_CUJA, AppName.FIELD, "Caniformia");

    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.8f);
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.GALICTIS_VITTATA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(33, this.genVarSize(60, 90, 75));
        }
        return AnimaliaFunctionUtil.getScaleForSize(33, this.genVarSize(45, 80, 60));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    @Override
    public float getWalkSpeed() {
        return 0.9f;
    }

    @Override
    public Item getBreedingItem() {
        return Items.CHICKEN;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.MEATS;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.DIURNAL;
    }

    @Override
    public int getEatLength() { return 10; }

    @Override
    public int getThreatPhase() {return this.entityData.get(THREAT_PHASE);}

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public int getThreatCooldown() { return 60; }

    @Override
    public void onThreatFlee(LivingEntity threat) {
        this.landPanic.panicFrom(threat.position());
        if (this.getCurrTwitchIdle() >= 0) {
            this.setTwitchTicks(0);
            this.setCurrTwitchIdle(-1);
        }
        if (this.isMovementLockedByIdle()) {
            this.setIdleTicks(0);
            this.setCurrRegIdle(-1);
            this.onMovementLockingIdleEnd();
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !player.isCreative() && !player.isSpectator()) {
            if (this.isAsleep()) {
                this.setSleepPhase(SLEEP_PHASE_NONE);
                this.setCurrentSleepIdle(-1);
            }
            if (this.isMovementLockedByIdle()) {
                this.setIdleTicks(0);
                this.setCurrRegIdle(-1);
                this.onMovementLockingIdleEnd();
            }
            this.landPanic.panicFrom(player.position());
        }
        super.playerTouch(player);
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.GALICTIS_VITTATA.get()) {
            return Component.translatable("trivia.animalia.galictis_vittata");
        }
        return Component.translatable("trivia.animalia.galictis_cuja");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.mustelidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carnivora");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();
        controller.transitionLength(5);
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        if (current != null && (current.animation().name().equals("toRest") || current.animation().name().equals("toSleep") || current.animation().name().equals("toThreat"))) {
            controller.transitionLength(0);
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                controller.setAnimation(RawAnimation.begin().then("toRest", Animation.LoopType.PLAY_ONCE).thenLoop("rest"));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
                controller.setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.PLAY_ONCE).thenLoop("sleep"));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_EXITING:
                controller.setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
        }
        if (!this.isBaby() && this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            controller.setAnimation(RawAnimation.begin().then("toThreat", Animation.LoopType.PLAY_ONCE).thenLoop("threat"));
            return PlayState.CONTINUE;
        }
        int idle = this.getCurrRegIdle();
        if (idle >= 0) {
            controller.setAnimation(RawAnimation.begin().then("idle" + idle, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isRunning()) {
            controller.setAnimation(RawAnimation.begin().then("run", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isActuallyMoving()) {
            controller.setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        AnimationController<T> controller = state.getController();
        controller.transitionLength(5);
        int twitch = this.getCurrTwitchIdle();
        if (twitch == 4 || twitch == 5) {
            controller.transitionLength(0);
            controller.setAnimation(RawAnimation.begin().then(twitch == 4 ? "threatGrumble" : "threatBark", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        int sleepIdle = this.getCurrentSleepIdle();
        if (sleepIdle >= 0 && this.getSleepPhase() == SLEEP_PHASE_ENTERING) {
            controller.setAnimation(RawAnimation.begin().then("restIdle" + sleepIdle, sleepIdle == 0 ? Animation.LoopType.LOOP : Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (twitch >= 0 && !this.isThreatening()) {
            controller.setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        controller.forceAnimationReset();
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

    /***
     * Idles Ref
     * 0- twitch head bob
     * 1- neg, look short
     * 2- neg, look long
     * 3- twitch, blink
     */

    @Override
    public int getIdleCount() {return 4;}

    @Override
    public IdleType getIdleType(int displayId) {
        return switch (displayId) {
            case 1, 2 -> IdleType.MOVEMENT_NEGATIVE;
            default -> IdleType.TWITCH;
        };
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 1 -> 25;
            case 2 -> 40;
            default -> 20 + this.random.nextInt(60);
        };
    }

    @Override
    public boolean canPlayIdle() {
        if (this.isAsleep() || this.isThreatening() || this.isGrazing() || this.isRunning() || this.isBaby()) {
            return false;
        }
        return !this.isInWater() || this.onGround();
    }

    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        return this.isBaby() ? -1 : super.pickIdleOfType(mob, type);
    }

    @Override
    public int regChance() { return 100; }

    @Override
    public int getUnSleepLength() {return 23;}

    @Override
    public int getToSleepLength() {return 200 + this.random.nextInt(200);}

    @Override
    public int getSleepIdleCount() {return 2;}

    @Override
    public int getSleepIdleLength(int sleepIdleId) {
        return sleepIdleId == 0 ? 40 + this.random.nextInt(80) : 31;
    }

    @Override
    public int sleepIdleChance() {return 60;}

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.getThreatPhase() != THREAT_PHASE_DISPLAY) {
            this.barkCooldown = 20;
            return;
        }
        if (this.barkCooldown > 0) {
            this.barkCooldown--;
            return;
        }
        int call = 4 + this.random.nextInt(2);
        this.setCurrTwitchIdle(call);
        this.setTwitchTicks(7);
        this.playSound(call == 4 ? AnimaliaSound.GALICTIS_GRUMBLE.get() : AnimaliaSound.GALICTIS_BARK.get());
        this.barkCooldown = 40 + this.random.nextInt(81);
    }
}
