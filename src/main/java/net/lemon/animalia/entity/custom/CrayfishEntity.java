package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class CrayfishEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(CrayfishEntity.class, EntityDataSerializers.INT);
    private static final int PROCAMBARUS_PIXEL = 16;
    private static final int EXIT_ANIM_LENGTH = 20;
    private static final int ATTACK_ANIM_LENGTH = 10;
    private int attackCooldown;

    public CrayfishEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ThreatGoal(this, 5.0D, 200, EXIT_ANIM_LENGTH, ThreatGoal.ThreatOutcome.FLEE));
    }

    @Override
    public int getStrollInterval() {
        return 40;
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
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.PROCAMBARUS_CLARKII.get()) {
            return Component.translatable("trivia.animalia.procambarus_clarkii");
        } else if (this.getType() == ModEntities.PROCAMBARUS_LUCIFUGUS.get()) {
            return Component.translatable("trivia.animalia.procambarus_lucifugus");
        } else if (this.getType() == ModEntities.PROCAMBARUS_VIRGINALIS.get()) {
            return Component.translatable("trivia.animalia.procambarus_virginalis");
        } else if (this.getType() == ModEntities.PROCAMBARUS_ALLENI.get()) {
            return Component.translatable("trivia.animalia.procambarus_alleni");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.cambaridae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.decapoda");
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.PROCAMBARUS_CLARKII.get()) {
            return new ItemStack(ModItems.PROCAMBARUS_CLARKII_BUCKET.get());
        } else if (this.getType() == ModEntities.PROCAMBARUS_LUCIFUGUS.get()) {
            return new ItemStack(ModItems.PROCAMBARUS_LUCIFUGUS_BUCKET.get());
        } else if (this.getType() == ModEntities.PROCAMBARUS_VIRGINALIS.get()) {
            return new ItemStack(ModItems.PROCAMBARUS_VIRGINALIS_BUCKET.get());
        }
        return new ItemStack(ModItems.PROCAMBARUS_ALLENI_BUCKET.get());
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.PROCAMBARUS_LUCIFUGUS.get()) {
            return 40;
        }
        return 40;
    }

    @Override
    public int getScaleforDetailGUI() {
        return Scannable.super.getScaleforDetailGUI();
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(PROCAMBARUS_PIXEL, 20);
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.PROCAMBARUS_CLARKII, Scannable.AppName.FIELD, "Decapoda");
        HolonetEntities.register(ModEntities.PROCAMBARUS_ALLENI, Scannable.AppName.FIELD, "Decapoda");
        HolonetEntities.register(ModEntities.PROCAMBARUS_VIRGINALIS, Scannable.AppName.FIELD, "Decapoda");
        HolonetEntities.register(ModEntities.PROCAMBARUS_LUCIFUGUS, Scannable.AppName.FIELD, "Decapoda");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    @Override
    public int getWalkTime() {
        return 400 + random.nextInt(500);
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 0.9f;
        }
        return 1.5f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "threat_controller", 0, this::threatPredicate));
        controllers.add(new AnimationController<>(this, "attack_controller", 0, this::attackPredicate)
                .triggerableAnim("defensiveAttack", RawAnimation.begin().then("defensiveAttack", Animation.LoopType.PLAY_ONCE)));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if (!this.isInWater()) {
            controller.setAnimation(RawAnimation.begin().then("beached", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isWalking()) {
            if (!this.isActuallyMoving()) {
                controller.setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            controller.setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState threatPredicate(AnimationState<T> state) {
        AnimationController<T> controller = state.getController();

        if (this.getThreatPhase() == THREAT_PHASE_DISPLAY) {
            controller.setAnimation(RawAnimation.begin()
                    .then("stancing", Animation.LoopType.PLAY_ONCE)
                    .thenLoop("defensiveStance"));
            return PlayState.CONTINUE;
        }


        if (this.getThreatPhase() == THREAT_PHASE_LEAVING) {
            controller.setAnimation(RawAnimation.begin().then("unStancing", Animation.LoopType.PLAY_ONCE));
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

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .build();
    }

    @Override
    public boolean shouldJumpOnFlop() {
        return false;
    }

    @Override
    public boolean hasSwimToWalkTransition() {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    @Override
    public int getThreatPhase() {
        return this.entityData.get(THREAT_PHASE);
    }

    @Override
    public void setThreatPhase(int phase) {
        this.entityData.set(THREAT_PHASE, phase);
    }

    @Override
    public boolean startsWalking() {
        return true;
    }

    @Override
    public boolean canStartThreatening() {
        return !this.isHiding() && this.isWalking() && this.onGround();
    }

    @Override
    public void onThreatTick(LivingEntity threat) {
        if (this.attackCooldown > 0) {
            return;
        }
        if (this.getBoundingBox().inflate(0.3D).intersects(threat.getBoundingBox())) {
            this.doHurtTarget(threat);
            this.triggerAnim("attack_controller", "defensiveAttack");
            this.attackCooldown = ATTACK_ANIM_LENGTH;
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.ANY_FLOOR;
    }
}
