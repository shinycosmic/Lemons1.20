package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.lemon.animalia.entity.ai.GrazeGoal;
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
import net.minecraft.tags.ItemTags;
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
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class MuntiacusEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat  {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(MuntiacusEntity.class, EntityDataSerializers.INT);
    private LandPanicGoal landPanic;
    private boolean wasGrazing;
    private int barkCooldown;

    public MuntiacusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.MUNTIACUS_MUNTJAK, AppName.FIELD, "Ruminantia");

    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.MUNTIACUS_MUNTJAK.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(22, 35);
//        }
        int desiredCm = this.getGender() == 0 ? 89 : 135;
        return AnimaliaFunctionUtil.getScaleForSize(28, desiredCm);
    }

    @Override
    public Item getBreedingItem() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.LEAVES;
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
        return Component.translatable("trivia.animalia.muntiacus_muntjak");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.cervidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.artiodactyla");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.landPanic = new LandPanicGoal(this, 2.5D, 200, 8.0D);
        this.goalSelector.addGoal(1, this.landPanic);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 12.0D, 1.0D, Integer.MAX_VALUE, 0, ThreatGoal.ThreatOutcome.FLEE, entity -> entity instanceof Player player && !player.isCreative()));
        this.goalSelector.addGoal(4, new FindNearestBlockGoal(this, 1.0D, 8, ModTags.Blocks.CROSS_PLANTS, FindNearestBlockGoal.TargetLocation.IN));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
    }

    @Override
    public int getEatLength() { return 25; }

    @Override
    public boolean hasDimorphism() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    //TODO need to do bark animation, then tie a soundEvent to it
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        animationState.getController().setAnimationSpeed(1.0D);
        animationState.getController().transitionLength(5);
        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("forage", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                animationState.getController().setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
                int sleepIdle = this.getCurrentSleepIdle();
                if (sleepIdle >= 0 && !this.isBaby()) { //TODO this animation is actually a looper, so we need to give it random durations
                    animationState.getController().setAnimation(RawAnimation.begin().then("sleepIdle" + sleepIdle, Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
                animationState.getController().setAnimation(RawAnimation.begin().then("sleeping", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_EXITING:
                animationState.getController().setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
        }

        if (!this.isBaby() && this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            AnimationProcessor.QueuedAnimation current = animationState.getController().getCurrentAnimation();
            if (current != null && current.animation().name().equals("toThreat")) {
                animationState.getController().transitionLength(0);
            }
            animationState.getController().setAnimation(RawAnimation.begin()
                    .then("toThreat", Animation.LoopType.PLAY_ONCE)
                    .thenLoop("threat"));
            return PlayState.CONTINUE;
        }
        if (this.isRunning()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("run", Animation.LoopType.LOOP));
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
        if (twitch == 5) {
            state.getController().transitionLength(5);
            state.getController().setAnimation(RawAnimation.begin().then("bark", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (twitch >= 0 && !this.isBaby()) {
            state.getController().transitionLength(5);
            state.getController().setAnimation(RawAnimation.begin().then("idle" + twitch, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        AnimationProcessor.QueuedAnimation current = state.getController().getCurrentAnimation();
        if (current != null && !state.getController().hasAnimationFinished() && (current.animation().name().equals("idle1") || current.animation().name().equals("idle1T"))) {
            state.getController().transitionLength(0);
            state.getController().setAnimation(RawAnimation.begin().then("idle1T", Animation.LoopType.PLAY_ONCE));
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
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !player.isCreative() && !player.isSpectator()) {
            if (this.isAsleep()) {
                this.setSleepPhase(SLEEP_PHASE_NONE);
                this.setCurrentSleepIdle(-1);
            }
            this.landPanic.panicFrom(player.position());
        }
        super.playerTouch(player);
    }


    @Override
    public int getToSleepLength() {return 20;}

    @Override
    public int getUnSleepLength() {return 20;}


    @Override
    public int getIdleCount() {return 5;} //TODO +1 bark, the bark plays only if the player is within 3 blocks, and plays every 6-12 seconds if player still in threshold, playing immediately upon the player entering the threshold and bark being off cooldown

    @Override
    public IdleType getIdleType(int displayId) {
        return IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 0 -> 20 + this.random.nextInt(31);
            case 2, 3 -> 40 + this.random.nextInt(31);
            case 4 -> 40 + this.random.nextInt(61);
            default -> 10 + this.random.nextInt(21);
        };
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
            return 4;
        }
        return mob.getRandom().nextInt(4);
    }


    @Override
    public boolean isGrazableBlock(BlockState state) { return state.is(ModTags.Blocks.FORAGEABLE); }

    @Override
    public boolean canGraze() { return super.canGraze() && !this.isBaby(); }

    @Override
    public int getGrazeSearchHeight() { return 1; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.wasGrazing && !this.isGrazing()) { //right after ending graze, play the munching twitch idle
                this.setCurrTwitchIdle(0);
                this.setTwitchTicks(20);
            }
            this.wasGrazing = this.isGrazing();
            if (this.barkCooldown > 0) {
                this.barkCooldown--;
            } else if (this.getThreatPhase() == THREAT_PHASE_DISPLAY && this.getNavigation().isDone()
                    && this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 4.0D, true) != null) {
                this.setCurrTwitchIdle(5);
                this.setTwitchTicks(10);
                this.playSound(AnimaliaSound.MUNTIACUS_MUNTJAK_BARK.get());
                this.barkCooldown = 120 + this.random.nextInt(121);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && this.isAlive()) {
            Vec3 from = source.getEntity() != null ? source.getEntity().position() : this.position();
            this.landPanic.panicFrom(from);
        }
        return result;
    }


    @Override
    public int getSleepIdleCount() {return 1;}

    @Override
    public int getSleepIdleLength(int sleepIdleId) {return 60 + this.random.nextInt(61);}

}
