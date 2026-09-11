package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ClimbPanicGoal;
import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
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

public class CrossarchusEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(CrossarchusEntity.class, EntityDataSerializers.INT);
    private ClimbPanicGoal climbPanic;

    public CrossarchusEntity(EntityType<? extends Animal> entityType, Level level) {
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
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
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
        if (this.getType() == ModEntities.CROSSARCHUS_OBSCURUS.get()) {
            return Component.translatable("trivia.animalia.crossarchus_obscurus");
        }
        return Component.translatable("trivia.animalia.crossarchus_platycephalus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.herpestidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carnivora");
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.CROSSARCHUS_OBSCURUS, AppName.FIELD, "Carnivora");
        HolonetEntities.register(ModEntities.CROSSARCHUS_PLATYCEPHALUS, AppName.FIELD, "Carnivora");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.CROSSARCHUS_OBSCURUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(27, 35);
        }
        return AnimaliaFunctionUtil.getScaleForSize(27, 57);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.climbPanic = new ClimbPanicGoal(this, 1.6D, 200, 8.0D, 16);
        this.goalSelector.addGoal(1, climbPanic);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 12.0D, 4.0D, Integer.MAX_VALUE, 0, ThreatGoal.ThreatOutcome.FLEE, entity -> entity instanceof Player player && !player.isCreative()));
        this.goalSelector.addGoal(4, new FindNearestBlockGoal(this, 1.0D, 8, pos -> !this.level().canSeeSky(pos) && this.level().getBlockState(pos.below())
                .isFaceSturdy(this.level(), pos.below(), Direction.UP), FindNearestBlockGoal.TargetLocation.IN));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
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
        return state.is(BlockTags.LOGS);
    }

    @Override
    public int getEatLength() {
        return 13;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        animationState.getController().setAnimationSpeed(1.0D);
        animationState.getController().transitionLength(0);
        if (this.isAttached()) {
            animationState.getController().transitionLength(5);
            if (!this.isActuallyMoving()) {
                animationState.getController().setAnimationSpeed(0.0D);
            }
            animationState.getController().setAnimation(RawAnimation.begin().then("climb", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("forage", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
//        switch (this.getSleepPhase()) {
//            case SLEEP_PHASE_ENTERING:
//                animationState.getController().setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
//                return PlayState.CONTINUE;
//            case SLEEP_PHASE_SLEEPING:
//                animationState.getController().setAnimation(RawAnimation.begin().then("sleep", Animation.LoopType.LOOP));
//                return PlayState.CONTINUE;
//            case SLEEP_PHASE_EXITING:
//                animationState.getController().setAnimation(RawAnimation.begin().then("unSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
//                return PlayState.CONTINUE;
//        }
        int idleNum = this.getCurrRegIdle();
        if(idleNum < 2 && idleNum >= 0 ) {
            animationState.getController().transitionLength(5);
        }
        if (idleNum >= 0) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle" + idleNum, Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (!this.isBaby() && this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            animationState.getController().setAnimation(RawAnimation.begin()
                    .then("toThreat", Animation.LoopType.PLAY_ONCE).thenLoop("threat"));
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
    public int getIdleCount() {return 4;}

    @Override
    public IdleType getIdleType(int displayId) {
        return IdleType.MOVEMENT_NEGATIVE;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 0 -> 10 + this.random.nextInt(45);
            case 2 -> 10 + this.random.nextInt(25);
            case 3 -> 8;
            default -> 20 + this.random.nextInt(50);
        };
    }

    //first use of isAttached here just means it cant play idles during climbs
    @Override
    public boolean canPlayIdle() {
        if (this.isAsleep() || this.isGrazing() || this.isRunning() || this.isAttached() || this.isBaby()) {
            return false;
        }
        return !this.isInWater() || this.onGround();
    }

    //all of its idles are movement negative minus idle3, threatBark
    @Override
    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        if (type != IdleType.MOVEMENT_NEGATIVE) {
            return -1;
        }
        if (this.isThreatening()) {
            return 3; //idle3 is threatBark
        }
        return mob.getRandom().nextInt(3);
    }



    @Override
    public int getThreatPhase() { return this.entityData.get(THREAT_PHASE); }

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public boolean canStartThreatening() {return !this.isAttached() && this.onGround();}

    @Override
    public void onThreatFlee(LivingEntity threat) {
        this.climbPanic.panicFrom(threat.position());
    }


    @Override
    public int getGrazeSearchHeight() { return 1; }

    @Override
    public boolean isGrazableBlock(BlockState state) { return state.is(BlockTags.SAND) || state.is(BlockTags.DIRT); }

    @Override
    public boolean canGraze() { return super.canGraze() && !this.isBaby(); }

    @Override
    public int getGrazeLength() {
        return 45;
    }
}
