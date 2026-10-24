package net.lemon.animalia.entity.custom;

import net.lemon.animalia.client.player.capability.StickingProjectileTypes;
import net.lemon.animalia.client.player.capability.StickingProjectilesCapability;
import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

import java.util.function.Predicate;

public class HystrixCristataEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(HystrixCristataEntity.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> THREAT_TARGETS = entity -> (entity instanceof Player player && !player.isCreative() && !player.isSpectator()) || entity instanceof Enemy;
    private static final int ATTACK_ANIM_LENGTH = 15;
    private int attackCooldown;
    private int attackStartedAt = -1;
    private int currThreatPose;
    @Nullable
    private LivingEntity attackTarget;

    public HystrixCristataEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ThreatGoal(this, 2D, 0.0D, Integer.MAX_VALUE, 0, ThreatGoal.ThreatOutcome.NONE, THREAT_TARGETS));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .add(Attributes.ATTACK_DAMAGE, 2f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.HYSTRIX_CRISTATA, AppName.FIELD, "Hystricomorpha");
    }

    //this one is calculated based on head to body, not including tail as i couldnt find reliable full body size estimates
    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(17, 86);
    }

    @Override
    public Item getBreedingItem() {
        return Items.WHEAT_SEEDS;
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
    public boolean isSoftActivity() {
        return true;
    }

    @Override
    public int getThreatPhase() { return this.entityData.get(THREAT_PHASE); }

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public boolean canStartThreatening() {
        return !this.isAttached() && this.onGround() && !this.isEating() && !this.isGrazing() && !this.isAsleep();
    }

    @Override
    public boolean canThreat(Player player) {
        return true;
    }

    @Override
    public int getThreatCooldown() {
        return this.isBold() ? 3000 : 1500;
    }
    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.hystrix_cristata");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.hystricidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.rodentia");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 15, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 10, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
        controllers.add(new AnimationController<>(this, "attack_controller", 0, this::attackPredicate)
                .triggerableAnim("threatAttack", RawAnimation.begin().then("threatAttack", Animation.LoopType.PLAY_ONCE)));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();
        controller.transitionLength(15);
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        if (current != null && (current.animation().name().equals("toRest") || current.animation().name().equals("toSleep"))) {
            controller.transitionLength(0);
        }
        if (this.getThreatPhase() != THREAT_PHASE_DISPLAY) {
            this.currThreatPose = 0;
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                controller.setAnimation(RawAnimation.begin().then("toRest", Animation.LoopType.PLAY_ONCE).thenLoop(this.isBaby() ? "resting" : "rest"));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
                controller.setAnimation(this.isBaby()
                        ? RawAnimation.begin().then("restingTosleeping", Animation.LoopType.PLAY_ONCE).thenLoop("sleeping")
                        : RawAnimation.begin().then("toSleep", Animation.LoopType.PLAY_ONCE).thenLoop("sleep"));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_EXITING:
                controller.setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
        }
        if (!this.isBaby() && this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            controller.transitionLength(0);
            if (this.getCurrRegIdle() == 5) {
                controller.setAnimation(RawAnimation.begin().then("threatStomp", Animation.LoopType.PLAY_ONCE));
                this.currThreatPose = 1;
            } else if (this.currThreatPose == 1) {
                controller.setAnimation(RawAnimation.begin().then("threat", Animation.LoopType.LOOP));
            } else {
                controller.setAnimation(RawAnimation.begin().then("toThreat", Animation.LoopType.PLAY_ONCE).thenLoop("threat"));
            }
            return PlayState.CONTINUE;
        }
        if (this.isGrazing() && !this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("forage", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (this.getCurrRegIdle() == 2) {
            controller.setAnimation(RawAnimation.begin().then("idle2", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (this.isActuallyMoving()) {
            controller.setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState idlesPredicate(AnimationState<T> state) {
        int twitch = this.getCurrTwitchIdle();
        if (twitch == 0 || twitch == 1) {
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (twitch == 3 || twitch == 4) {
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

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> state) {
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
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    @Override
    public void onThreatStart(LivingEntity threat) {
        this.attackCooldown = 40;
        this.attackStartedAt = -1;
        this.attackTarget = null;
    }

    @Override
    public void onThreatTick(LivingEntity threat) {
        if (this.attackStartedAt >= 0) {
            int elapsed = this.tickCount - this.attackStartedAt;
            if (elapsed == 7) {
                this.quillHit(this.attackTarget != null ? this.attackTarget : threat);
            }
            if (elapsed >= ATTACK_ANIM_LENGTH) {
                this.attackStartedAt = -1;
                this.attackTarget = null;
                this.attackCooldown = 40;
            }
            return;
        }
        if (this.attackCooldown <= 0 && this.isInAttackReach(threat)) {
            this.startAttack();
        }
    }

    private void startAttack() {
        this.triggerAnim("attack_controller", "threatAttack");
        this.attackStartedAt = this.tickCount;
    }

    private boolean isInAttackReach(LivingEntity target) {
        return target.isAlive() && this.getBoundingBox().inflate(1D).intersects(target.getBoundingBox());
    }

    private void quillHit(LivingEntity target) {
        if (!this.isInAttackReach(target)) {
            return;
        }
        if (target.hurt(this.damageSources().thorns(this), 2.0F)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800, 0), this);
            StickingProjectilesCapability.add(target, StickingProjectileTypes.QUILL);
        }
    }

    @Override
    public boolean isThreatGone(LivingEntity threat, double threatRange) {
        if (this.attackStartedAt >= 0) {
            return false;
        }
        return !this.hasLineOfSight(threat) || this.isThreatGone(threat, 6.0D);
    }

    @Override
    public boolean onThreatHurt(LivingEntity threat) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && this.getThreatPhase() == THREAT_PHASE_DISPLAY && this.attackStartedAt < 0
                && source.getEntity() instanceof LivingEntity attacker) {
            this.attackTarget = attacker;
            this.startAttack();
        }
        return hurt;
    }

    @Override
    protected void doPush(Entity entity) {
        super.doPush(entity);
        if (!this.level().isClientSide && entity instanceof LivingEntity living && THREAT_TARGETS.test(living)) {
            living.hurt(this.damageSources().thorns(this), 1.0F);
        }
    }



    @Override
    public int getGrazeSearchHeight() { return 1; }

    @Override
    public boolean isGrazableBlock(BlockState state) { return state.is(BlockTags.DIRT); }

    @Override
    public boolean canGraze() { return super.canGraze() && !this.isBaby() && !this.isThreatening(); }

    @Override
    public int getGrazeLength() { return 45; }


    /***
     * Idles ref
     * 0- head lowered, twitch
     * 1- head lowered shaking, twitch
     * 2- sniff ground, neg
     * 3- blink, twitch
     * 4- raise quills, twitch
     * threatStomp- random stomp during threat
     */

    @Override
    public int getIdleCount() { return 6; }

    @Override
    public IdleType getIdleType(int displayId) {
        return displayId == 2 || displayId == 5 ? IdleType.MOVEMENT_NEGATIVE : IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 2 -> 45;
            case 3 -> 10;
            case 4 -> 60;
            case 5 -> 8;
            default -> 100 + this.random.nextInt(101);
        };
    }

    @Override
    public boolean canPlayIdle() {
        if (this.isAsleep() || this.isGrazing() || this.isEating() || this.isBaby()) {
            return false;
        }
        return !this.isInWater() || this.onGround();
    }

    @Override
    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        if (this.isThreatening()) {
            return type == IdleType.MOVEMENT_NEGATIVE && this.attackStartedAt < 0 ? 5 : -1;
        }
        return switch (type) {
            case MOVEMENT_NEGATIVE -> 2;
            case TWITCH -> switch (this.random.nextInt(4)) {
                case 0 -> 0;
                case 1 -> 1;
                case 2 -> 3;
                default -> 4;
            };
            default -> -1;
        };
    }

    @Override
    public int getToSleepLength() { return 15 + 60 + this.random.nextInt(101); }

    @Override
    public int getUnSleepLength() { return this.isBaby() ? 10 : 20; }

    @Override
    public int getEatLength() { return 10; }
}
