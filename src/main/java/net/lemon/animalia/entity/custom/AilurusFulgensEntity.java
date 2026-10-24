package net.lemon.animalia.entity.custom;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

public class AilurusFulgensEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(AilurusFulgensEntity.class, EntityDataSerializers.INT);

    public AilurusFulgensEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 7D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AilurusPanicGoal(this));
        this.goalSelector.addGoal(2, new ThreatGoal(this, 8.0D, 160 + this.random.nextInt(60), 10, ThreatGoal.ThreatOutcome.NONE));
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.AILURUS_FULGENS, AppName.FIELD, "Caniformia");
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(39, 115);
    }

    @Override
    public Item getBreedingItem() {
        return Items.BAMBOO;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.BAMBOOS;
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
    public boolean canClimb() {
        return true;
    }

    @Override
    public boolean babyClimbEnabled() {
        return false;
    }

    @Override
    public boolean isClimbableBlock(BlockState state) {
        return state.is(ModTags.Blocks.SOFT_CLIMBABLE);
    }

    @Override
    public boolean wantsToClimb() {
        return this.climbEnabled();
    }

    @Override
    public boolean canStartSleeping() {
        return super.canStartSleeping() && !this.isAttached();
    }

    @Override
    public int getThreatPhase() { return this.entityData.get(THREAT_PHASE); }

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public boolean canStartThreatening() {return !this.isAttached() && this.onGround() && !this.isEating();}

    @Override
    public boolean canThreat(Player player) {
        return this.isBold() && !player.isCrouching();
    }

    @Override
    public int getThreatCooldown() {return 12000;}

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.ailurus_fulgens");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.ailuridae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carnivora");
    }

    @Override
    public int getEatLength() {
        return 100 + this.random.nextInt(100);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 5, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();
        controller.transitionLength(5);
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        if (current != null && (current.animation().name().equals("toRest") || current.animation().name().equals("toSleep") || current.animation().name().equals("toThreat") || current.animation().name().equals("toSit"))) {
            controller.transitionLength(0);
        }
        if (this.isAttached()) {
            controller.setAnimation(RawAnimation.begin().then("climb", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                controller.setAnimation(RawAnimation.begin().then("toRest", Animation.LoopType.PLAY_ONCE).thenLoop(this.isBaby() ? "resting" : "rest"));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
                if (this.isBaby()) {
                    controller.setAnimation(RawAnimation.begin().then("restingTosleeping", Animation.LoopType.PLAY_ONCE).thenLoop("sleeping"));
                } else {
                    controller.setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.PLAY_ONCE).thenLoop("sleep"));
                }
                return PlayState.CONTINUE;
            case SLEEP_PHASE_EXITING:
                controller.setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
        }
        if (!this.isBaby()) {
            if (this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
                controller.setAnimation(RawAnimation.begin().then("toThreat", Animation.LoopType.PLAY_ONCE).thenLoop("threat"));
                return PlayState.CONTINUE;
            }
            if (this.getThreatPhase() == THREAT_PHASE_EXITING) {
                controller.setAnimation(RawAnimation.begin().then("threatEnd", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
        }
        int idle = this.getCurrRegIdle();
        if (idle == 3) {
            controller.setAnimation(RawAnimation.begin().then("toSit", Animation.LoopType.PLAY_ONCE).thenLoop("sit"));
            return PlayState.CONTINUE;
        }
        if (idle >= 0) {
            controller.setAnimation(RawAnimation.begin().then("idle" + idle, idle == 1 ? Animation.LoopType.LOOP : Animation.LoopType.PLAY_ONCE));
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
        int sleepIdle = this.getCurrentSleepIdle();
        if (sleepIdle >= 0) {
            if (this.getSleepPhase() == SLEEP_PHASE_ENTERING) {
                controller.setAnimation(RawAnimation.begin().then("idleRest" + sleepIdle, sleepIdle == 0 ? Animation.LoopType.PLAY_ONCE : Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            if (this.getSleepPhase() == SLEEP_PHASE_SLEEPING) {
                controller.setAnimation(RawAnimation.begin().then(sleepIdle == 0 ? "dreamL" : "dreamR", Animation.LoopType.PLAY_ONCE));
                return PlayState.CONTINUE;
            }
        }
        int twitch = this.getCurrTwitchIdle();
        if (twitch == 4 && this.getCurrRegIdle() == 3) {
            controller.setAnimation(RawAnimation.begin().then("idleSit0", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (twitch == 0) {
            controller.setAnimation(RawAnimation.begin().then("idle0", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        controller.forceAnimationReset();
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState eatPredicate(AnimationState<T> state) {
        if (this.isEating() && !this.isBaby() && !this.isAsleep()) {
            state.getController().setAnimation(RawAnimation.begin().then("eat", Animation.LoopType.PLAY_ONCE).thenLoop("eating"));
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby() && this.eats(itemstack)) {
            if (!this.onGround() || this.isAttached() || this.isAsleep() || this.isThreatening() || this.isEating()) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide) {
                if (this.isMovementLockedByIdle()) {
                    this.setIdleTicks(0);
                    this.setCurrRegIdle(-1);
                    this.onMovementLockingIdleEnd();
                }
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                this.usePlayerItem(player, hand, itemstack);
                this.heal(2.0F);
                this.startEating();
                this.getNavigation().stop();
                if (this.getAge() == 0 && this.canFallInLove()) {
                    this.setInLove(player);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && !this.isEating() && !this.getMainHandItem().isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && this.isEating()) {
            this.setEating(false);
        }
        return super.hurt(source, amount);
    }

    @Override
    protected boolean isMovementLocked() {
        return super.isMovementLocked() || (this.isEating() && !this.isBaby());
    }

    /***
     * Idles ref
     * 0- blink
     * 1- pant
     * 2- yawn
     * idleSit0- sniff (3)
     * idleRest0- sniff (4)
     * idleRest1- pant (4)
     * dreamL- ear twitch left
     * dreamR- ear twitch right
     */

    @Override
    public int getIdleCount() {return 5;}

    @Override
    public IdleType getIdleType(int displayId) {
        return switch (displayId) {
            case 1, 2, 3 -> IdleType.MOVEMENT_NEGATIVE;
            default -> IdleType.TWITCH;
        };
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 1 -> 40 + this.random.nextInt(60);
            case 2 -> 17;
            case 3 -> 400 + this.random.nextInt(801);
            case 4 -> 29;
            default -> 10;
        };
    }

    @Override
    public boolean canPlayIdle() {
        if (this.isAsleep() || this.isThreatening() || this.isGrazing() || this.isRunning() || this.isEating() || this.isAttached() || this.isBaby()) {
            return false;
        }
        return !this.isInWater() || this.onGround();
    }

    @Override
    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        if (this.isBaby()) {
            return -1;
        }
        if (this.getSleepPhase() == SLEEP_PHASE_ENTERING) {

        }
        return switch (type) {
            case TWITCH -> this.getCurrRegIdle() == 3 && this.random.nextBoolean() ? 4 : 0;
            case MOVEMENT_NEGATIVE -> 1 + this.random.nextInt(this.isActiveWindow(this) ? 3 : 2);
            default -> -1;
        };
    }

    @Override
    public int regChance() { return 100; }

    @Override
    public int getUnSleepLength() {return 28;}

    @Override
    public int getToSleepLength() {return 200 + this.random.nextInt(400);}

    @Override
    public int getSleepIdleCount() {return this.isBaby() ? 0 : 2;}

    @Override
    public int getSleepIdleLength(int sleepIdleId) {
        if (this.getSleepPhase() == SLEEP_PHASE_SLEEPING) {
            return 10;
        }
        return sleepIdleId == 0 ? 38 : 40 + this.random.nextInt(80);
    }

    @Override
    public int sleepIdleChance() {return 60;}

    private static class AilurusPanicGoal extends LandPanicGoal {
        public AilurusPanicGoal(AilurusFulgensEntity mob) {
            super(mob, 2.5D, 200, 8.0D);
        }

        @Override
        protected boolean canScan() {
            return !this.mob.isBold() && !this.mob.isEating();
        }

        @Override
        public void start() {
            this.mob.setEating(false);
            super.start();
        }
    }

    /***
     * Eating notes
     * must be hand-fed, if hand fed (like vanilla panda) eat plays first (this is the pick up the item from your hand with its mouth)
     * then play eating, which the item you gave it is rendered in its hands as it eats.
     */

    /***
     * threat notes
     * two types: this animal should proximity panic away from any player that approaches it without crouching by default.
     * if it is bold -> play threat instead. once threat expires, initiate a 1 minecraft day cooldown. Bold red pandas can threat players but cannot panic from players
     */

}
