package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.FishHideGoal;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
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
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Predicate;

public class HeterocongerEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> STATE_SWITCH = SynchedEntityData.defineId(HeterocongerEntity.class, EntityDataSerializers.INT);
    public static final int PHASE_RETREAT = 4;
    public static final int PHASE_HIDDEN = 5;
    public static final int PHASE_RETURN = 6;
    private int retreatTicks;
    private int hiddenTicks;
    private int returnTicks;
    private int nextCheck;
    private final TargetingConditions retreatCon = TargetingConditions.forNonCombat().range(3.0D).selector(THREAT);
    private final TargetingConditions clearCon = TargetingConditions.forNonCombat().range(4.0D).selector(THREAT);
    private static final Predicate<LivingEntity> THREAT = ((Predicate<LivingEntity>) entity -> (entity.getBbWidth() >= 0.5F || entity.getBbHeight() >= 0.9F) && !(entity instanceof HeterocongerEntity) && !(entity instanceof Player player && player.isCreative())).and(EntitySelector.NO_SPECTATORS);

    public HeterocongerEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FishHideGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE_SWITCH, 0);
    }

    public int getStateSwitch() {
        return entityData.get(STATE_SWITCH);
    }

    public void setStateSwitch(int state) {
        entityData.set(STATE_SWITCH, state);
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.ANY_FLOOR;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FISH_FOOD;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.FISH_FOOD.get();
    }

    @Override
    public boolean canPlayIdle() {
        return getHidePhase() == PHASE_BURROWED;
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
        if(this.getType() == ModEntities.HETEROCONGER_HASSI.get()) {
            return Component.translatable("trivia.animalia.heteroconger_hassi");
        }
        return Component.translatable("trivia.animalia.gorgasia_preclara");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.congridae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.anguilliformes");
    }

    @Override
    public int getScaleforGUI() {
        return 14;
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        currScale *= 0.2f;
        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(50, 50);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.HETEROCONGER_HASSI, Scannable.AppName.FISH, "Anguilliformes");
        HolonetEntities.register(ModEntities.GORGASIA_PRECLARA, Scannable.AppName.FISH, "Anguilliformes");

    }

    @Override
    public int getHideCooldown() {
        return 0;
    }

    @Override
    public int getHideLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getBurrowingLength() {
        return 50;
    }

    @Override
    public boolean canHide() {
        return !this.isBaby();
    }

    @Override
    public boolean canStartHiding() {
        return this.canHide() && !this.isHiding() && this.isInWater() && this.onGround() && this.onHideableBlock(this);
    }

    @Override
    public boolean isPushable() {
        return !this.isHiding();
    }

    @Override
    protected void doPush(Entity p_20971_) {
        if(!this.isHiding()) {
            super.doPush(p_20971_);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isHiding();
    }

    @Override
    public int getIdleCount() {
        return 3;
    }

    @Override
    public IdleType getIdleType(int displayId) {
        return IdleType.TWITCH;
    }

    @Override
    public int getIdleLength(int displayId) {
        return switch (displayId) {
            case 0, 1 -> 30;
            default -> 20;
        };
    }

    @Override
    public int pickIdleOfType(PathfinderMob mob, IdleType type) {
        if(type != IdleType.TWITCH) {
            return -1;
        }
        return this.getStateSwitch() == 0 ? 0 : 1 + mob.getRandom().nextInt(2);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            int phase = this.getHidePhase();

            if (phase == PHASE_BURROWED && this.getCurrTwitchIdle() < 0
                    && this.random.nextInt(250) == 0) {
                this.setStateSwitch(this.getStateSwitch() == 0 ? 1 : 0);
            }

            if (this.isBurrowedPhase(phase) && this.tickCount % 10 == 0
                    && (!this.onHideableBlock(this))) {
                this.leaveBurrow();
            }

            switch (phase) {
                case PHASE_BURROWED:
                    if (this.tickCount >= this.nextCheck) {
                        this.nextCheck = this.tickCount + 5 + this.random.nextInt(10);
                        if (this.findThreat(this.retreatCon, 3.0D) != null) {
                            this.startRetreat();
                        }
                    }
                    break;
                case PHASE_RETREAT:
                    --this.retreatTicks;
                    if (this.retreatTicks <= 0) {
                        this.setHidePhase(PHASE_HIDDEN);
                        this.hiddenTicks = 0;
                    }
                    break;
                case PHASE_HIDDEN:
                    if (this.tickCount >= this.nextCheck) {
                        this.nextCheck = this.tickCount + 5 + this.random.nextInt(10);
                        if (this.findThreat(this.clearCon, 4.0D) != null) {
                            this.hiddenTicks = 0;
                        }
                    }
                    ++this.hiddenTicks;
                    if (this.hiddenTicks >= 100) {
                        this.setHidePhase(PHASE_RETURN);
                        this.returnTicks = 10;
                    }
                    break;
                case PHASE_RETURN:
                    if (this.tickCount >= this.nextCheck) {
                        this.nextCheck = this.tickCount + 5 + this.random.nextInt(10);
                        if (this.findThreat(this.retreatCon, 3.0D) != null) {
                            this.startRetreat();
                            break;
                        }
                    }
                    --this.returnTicks;
                    if (this.returnTicks <= 0) {
                        this.setHidePhase(PHASE_BURROWING);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean sinksWhenIdle() {
        return true;
    }

    private LivingEntity findThreat(TargetingConditions conditions, double range) {
        return this.level().getNearestEntity(LivingEntity.class, conditions, this,
                this.getX(), this.getY(), this.getZ(),
                this.getBoundingBox().inflate(range));
    }

    private boolean isBurrowedPhase(int phase) {
        return phase == PHASE_BURROWED || phase == PHASE_RETREAT
                || phase == PHASE_HIDDEN || phase == PHASE_RETURN;
    }

    private void leaveBurrow() {
        this.setHidePhase(PHASE_NONE);
        this.setHiding(false);
        this.wantsToHide = false;
        this.retreatTicks = 0;
        this.hiddenTicks = 0;
        this.returnTicks = 0;
    }

    private void startRetreat() {
        this.setHidePhase(PHASE_RETREAT);
        this.retreatTicks = 7;
        this.hiddenTicks = 0;
        if (this.getCurrTwitchIdle() >= 0) {
            this.setTwitchTicks(0);
            this.setCurrTwitchIdle(-1);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int phase = this.getHidePhase();
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && this.isAlive()
                && this.isBurrowedPhase(phase)
                && this.onHideableBlock(this)) {
            this.setHiding(true);
            if (phase == PHASE_HIDDEN) {
                this.setHidePhase(PHASE_HIDDEN);
                this.hiddenTicks = 0;
            } else if (phase == PHASE_RETREAT) {
                this.setHidePhase(PHASE_RETREAT);
            } else {
                this.startRetreat();
            }
        }
        return result;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.getAge() == 0 && this.isBreedingItem(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            HeterocongerEntity partner = this.findBreedPartner();
            if (partner != null) {
                this.dropEggItem();
                this.setAge(6000);
                partner.setAge(6000);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isHiding()) {
            super.knockback(strength, x, z);
        }
    }

    private HeterocongerEntity findBreedPartner() {
        List<HeterocongerEntity> neighbors = this.level().getEntitiesOfClass(HeterocongerEntity.class,
                this.getBoundingBox().inflate(5.0D),
                eel -> eel != this && eel.getAge() == 0 && eel.getType() == this.getType());
        return neighbors.isEmpty() ? null : neighbors.get(0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "idles_controller", 0, this::idlesPredicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        int suffix = this.getStateSwitch() == 0 ? 1 : 2;
        switch (this.getHidePhase()) {
            case PHASE_BURROWING:
                animationState.getController().setAnimation(RawAnimation.begin().then("burrowing", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            case PHASE_BURROWED:
                animationState.getController().setAnimation(RawAnimation.begin().then("burrowed" + suffix, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            case PHASE_RETREAT:
                animationState.getController().setAnimation(RawAnimation.begin().then("toHide" + suffix, Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            case PHASE_HIDDEN:
                animationState.getController().setAnimation(RawAnimation.begin().then("hiding", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
//            case PHASE_RETURN:
//                animationState.getController().setAnimation(RawAnimation.begin().then("unHide", Animation.LoopType.HOLD_ON_LAST_FRAME));
//                return PlayState.CONTINUE;
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
        this.setStateSwitch(this.random.nextInt(2));
        this.setPos(this.getX() + (this.random.nextDouble() - 0.5D) * 0.7D, this.getY(), this.getZ() + (this.random.nextDouble() - 0.5D) * 0.7D);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
