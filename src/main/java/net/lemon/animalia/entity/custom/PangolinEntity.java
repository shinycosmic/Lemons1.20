package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.ai.GuardGoal;
import net.lemon.animalia.entity.ai.SleepGoal;
import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanGuard;
import net.lemon.animalia.entity.bases.helpers.ICanSleep;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class PangolinEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanGuard, ICanSleep {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> GUARD_PHASE = SynchedEntityData.defineId(PangolinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLEEP_PHASE = SynchedEntityData.defineId(PangolinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLEEP_IDLE = SynchedEntityData.defineId(PangolinEntity.class, EntityDataSerializers.INT);
    private final int SMUTSIA_GIGANTEA_PIXEL = 39;

    private int wantsToGuardUntil;
    private int attackCooldown;
    private int attackTicks;
    private LivingEntity attackTarget;


    public PangolinEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GuardGoal(this, 6D, entity -> !(entity instanceof PangolinEntity) && !(entity instanceof Player player && player.isCreative())));
        this.goalSelector.addGoal(1, new SleepGoal(this));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
        super.registerGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GUARD_PHASE, 0);
        this.entityData.define(SLEEP_PHASE, 0);
        this.entityData.define(SLEEP_IDLE, -1);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .build();
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.TERMITE.get();
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.INVERTEBRATE;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.DIURNAL;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.smutsia_gigantea");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.manidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.pholidota");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.SMUTSIA_GIGANTEA.get()) {
            return 18;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.65f);
    }

    @Override
    public int getXOffsetForGUI() {
        int offset = 0;
        if(this.getType() == ModEntities.SMUTSIA_GIGANTEA.get()) {
            offset = -5;
        }
        return offset;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.SMUTSIA_GIGANTEA, AppName.FIELD, "Pholidota");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.SMUTSIA_GIGANTEA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(SMUTSIA_GIGANTEA_PIXEL, 137);
        }
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate).triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("graze", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        //Sleep is allowed on babies, and so is idle0, only graze and guard are not
        if (this.isAsleep()) {
            int sleepIdle = this.getCurrentSleepIdle();
            if (sleepIdle >= 0) {
                animationState.getController().setAnimation(RawAnimation.begin().then("sleepIdle" + sleepIdle, Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
            animationState.getController().setAnimation(RawAnimation.begin().then("sleeping", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        int bodyIdle = this.getCurrRegIdle();
        if (bodyIdle >= 0) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle" + bodyIdle, Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (!isBaby()) {
            int phase = this.getGuardPhase();
            switch (phase) {
                case GUARD_PHASE_ENTERING:
                    animationState.getController().setAnimation(RawAnimation.begin().then("toGuard", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
                case GUARD_PHASE_GUARDING:
                    animationState.getController().setAnimation(RawAnimation.begin().then("guard", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                case GUARD_PHASE_EXITING:
                    animationState.getController().setAnimation(RawAnimation.begin().then("unGuard", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
            }
        }

        if (this.isActuallyMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
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
    public int getGuardPhase() {
        return this.entityData.get(GUARD_PHASE);
    }

    @Override
    public void setGuardPhase(int phase) {
        this.entityData.set(GUARD_PHASE, phase);
    }

    @Override
    public boolean wantsToGuard() {return this.tickCount < this.wantsToGuardUntil;}

    @Override
    public void guardWindow(int ticks) {this.wantsToGuardUntil = this.tickCount + ticks;}

    @Override
    public void clearWantsToGuard() {this.wantsToGuardUntil = 0;}

    @Override
    public int getToGuardLength() {return 14;}

    @Override
    public int getUnGuardLength() {return 80;}

    @Override
    public GuardActivation getGuardActivation() {return GuardActivation.ATTACK;}

    @Override
    public boolean isGrazableBlock(BlockState state) {return state.is(ModTags.Blocks.TERMITE_MOUNDS);}

    @Override
    public int getGrazeCount() { return 1; }

    @Override
    public int getGrazeLength() {return 190;}

    @Override
    public double getGrazeReachSqr() {return 3.5D;}

    @Override
    public boolean canGraze() {return super.canGraze() && this.getGuardPhase() == GUARD_PHASE_NONE;}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.level().isClientSide && this.isAsleep()) {
            this.setSleepPhase(SLEEP_PHASE_NONE);
            this.setCurrentSleepIdle(-1);
        }
        if (!this.level().isClientSide && this.guardTriggersFrom(source)) {
            this.guardWindow(this.getGuardReAttackWindow());
        }
        return super.hurt(source, amount * this.getGuardDamageMultiplier(source));
    }

    @Override
    public void onGuardTick(LivingEntity threat) {
        if (this.attackCooldown > 0) {
            return;
        }
        if (this.getBoundingBox().inflate(0.3D).intersects(threat.getBoundingBox())) {
            this.triggerAnim("controller", "attack");
            this.attackTarget = threat;
            this.attackTicks = 12;
            this.attackCooldown = 15;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (!this.level().isClientSide && this.attackTicks > 0) {
            this.attackTicks--;
            if (this.attackTicks <= 0 && this.attackTarget != null) {
                if (this.attackTarget.isAlive() && this.getBoundingBox().inflate(0.3D).intersects(this.attackTarget.getBoundingBox())) {
                    this.doHurtTarget(this.attackTarget);
                }
                this.attackTarget = null;
            }
        }
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
    public int getSleepPhase() {return this.entityData.get(SLEEP_PHASE);}

    @Override
    public void setSleepPhase(int phase) {this.entityData.set(SLEEP_PHASE, phase);}

    @Override
    public int getCurrentSleepIdle() {return this.entityData.get(SLEEP_IDLE);}

    @Override
    public void setCurrentSleepIdle(int sleepIdleId) {this.entityData.set(SLEEP_IDLE, sleepIdleId);}

    @Override
    public int getSleepIdleCount() {return 1;}

    @Override
    public boolean canStartSleeping() {
        return ICanSleep.super.canStartSleeping() && this.getGuardPhase() == GUARD_PHASE_NONE
                && !this.isGrazing() && !this.isEating() && !this.isInLove() && !this.isMovementLockedByIdle();
    }

    @Override
    public int getIdleCount() {return 1;}

    @Override
    public IdleType getIdleType(int displayId) {return IdleType.MOVEMENT_NEGATIVE;}

    @Override
    public int getIdleLength(int displayId) {return 130;}
}
