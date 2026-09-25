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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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

public class MoschusEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(MoschusEntity.class, EntityDataSerializers.INT);
    private LandPanicGoal landPanic;
    private boolean wasGrazing;
    private int currThreatPose = 0;

    public MoschusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.landPanic = new LandPanicGoal(this, 3.5D, 200, 8.0D);
        this.goalSelector.addGoal(1, this.landPanic);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 12.0D, 1.0D, Integer.MAX_VALUE, 0, ThreatGoal.ThreatOutcome.FLEE, entity -> entity instanceof Player player && !player.isCreative()));
        this.goalSelector.addGoal(4, new FindNearestBlockGoal(this, 1.0D, 8, ModTags.Blocks.CROSS_PLANTS, FindNearestBlockGoal.TargetLocation.IN));
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
        //TODO territorial goals — mark, leash, owner territory tick (intruder push)
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
        HolonetEntities.register(ModEntities.MOSCHUS_MOSCHIFERUS, AppName.FIELD, "Ruminantia");

    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.MOSCHUS_MOSCHIFERUS.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(22, 35);
//        }
        int desiredCm = this.getGender() == 0 ? 90 : 100;
        return AnimaliaFunctionUtil.getScaleForSize(28, desiredCm); //TODO placeholder
    }

    @Override
    public Item getBreedingItem() {
        return Items.GRASS;
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
        return Component.translatable("trivia.animalia.moschus_moschiferus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.moschidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.artiodactyla");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 5, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        animationState.getController().setAnimationSpeed(1.0D);
        animationState.getController().transitionLength(5);
        if (this.getThreatPhase() != THREAT_PHASE_DISPLAY) {
            this.currThreatPose = 0;
        }
        if (this.isGrazing() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("forage", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        switch (this.getSleepPhase()) {
            case SLEEP_PHASE_ENTERING:
                animationState.getController().setAnimation(RawAnimation.begin().then("toSleep", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            case SLEEP_PHASE_SLEEPING:
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
            if (this.getCurrTwitchIdle() == 3) {
                animationState.getController().transitionLength(10);
                animationState.getController().setAnimation(RawAnimation.begin().then("idle3", Animation.LoopType.LOOP));
                this.currThreatPose = 1;
            } else if (current != null && this.currThreatPose == 1) {
                animationState.getController().transitionLength(10);
                animationState.getController().setAnimation(RawAnimation.begin().then("threat", Animation.LoopType.LOOP));
            } else {
                animationState.getController().setAnimation(RawAnimation.begin().then("toThreat", Animation.LoopType.PLAY_ONCE)
                        .thenLoop("threat"));
            }
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
        if (twitch >= 0 && twitch != 3 && !this.isBaby() && !this.isThreatening()) {
            state.getController().transitionLength(10);
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


    /**
     * Idles ref:
     *  0- munch
     *  1- slight down
     *  2- more down
     *  3- threat second pose (threatEar)
     */

    @Override
    public int getIdleCount() { return 4; }

    @Override
    public IdleType getIdleType(int displayId) {
        return IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 0 -> 20 + this.random.nextInt(31);
            case 1, 2 -> 40 + this.random.nextInt(31);
            case 3 -> 60 + this.random.nextInt(61);
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
        if (type != IdleType.TWITCH || this.isBaby()) {
            return -1;
        }
        if (this.isThreatening()) {
            return 3;
        }
        return mob.getRandom().nextInt(3);
    }

    @Override
    public int getEatLength() { return 25; }

    @Override
    public boolean hasDimorphism() { return true; }

    @Override
    public int getToSleepLength() { return 20; }

    @Override
    public int getUnSleepLength() { return 20; }

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

    private BlockPos territoryPos;
    private int territoryRadius;

    public BlockPos getTerritoryPos() {
        return this.territoryPos;
    }

    public void setTerritoryPos(BlockPos pos) {
        this.territoryPos = pos;
    }

    public int getTerritoryRadius() {
        return this.territoryRadius;
    }

    public void setTerritoryRadius(int radius) {
        this.territoryRadius = radius;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.territoryPos != null) {
            tag.put("TerritoryPos", NbtUtils.writeBlockPos(this.territoryPos));
            tag.putInt("TerritoryRadius", this.territoryRadius);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("TerritoryPos")) {
            this.territoryPos = NbtUtils.readBlockPos(tag.getCompound("TerritoryPos"));
            this.territoryRadius = tag.getInt("TerritoryRadius");
        }
    }
}
