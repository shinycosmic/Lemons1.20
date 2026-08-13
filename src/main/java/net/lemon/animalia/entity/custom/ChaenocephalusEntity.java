package net.lemon.animalia.entity.custom;

import net.lemon.animalia.block.MoundNestBlock;
import net.lemon.animalia.block.entities.MoundNestBlockEntity;
import net.lemon.animalia.entity.bases.*;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class ChaenocephalusEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {

    private static final EntityDataAccessor<Integer> NEST_PHASE = SynchedEntityData.defineId(ChaenocephalusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GUARD_DIRECTION = SynchedEntityData.defineId(ChaenocephalusEntity.class, EntityDataSerializers.INT);

    public static final int NEST_PHASE_IDLE = 0;
    public static final int NEST_PHASE_NESTING = 1;
    public static final int NEST_PHASE_GUARDING = 2;
    public static final int NEST_PHASE_HATCHING = 3;
    public static final int NEST_PHASE_COOLDOWN = 4;

    public static final int GUARD_DIR_CENTER = 0;
    public static final int GUARD_DIR_LEFT = 1;
    public static final int GUARD_DIR_RIGHT = 2;
    public static final int GUARD_DIR_TO_LEFT = 3;
    public static final int GUARD_DIR_TO_RIGHT = 4;
    public static final int GUARD_DIR_FROM_LEFT = 5;
    public static final int GUARD_DIR_FROM_RIGHT = 6;

    private static final int NEST_MAKING_TICKS = 42;
    private static final int ROTATION_TICKS = 30;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int CHAENOCEPHALUS_ACERATUS_PIXEL = 37;
    private static final int CYGNODRACO_MAWSONI_PIXEL = 31;
    private final Random rand = new Random();

    private int nestMakingTicks;
    private int nestGuardTicks;
    private int nestCooldown;
    private int guardDirectionTicks;
    private boolean wantsToNest;
    private int wantsToNestTimeout;
    private int attackCooldown;
    @Nullable
    private BlockPos nestPos;

    public ChaenocephalusEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NEST_PHASE, NEST_PHASE_IDLE);
        this.entityData.define(GUARD_DIRECTION, GUARD_DIR_CENTER);
    }

    public int getNestPhase() {
        return this.entityData.get(NEST_PHASE);
    }

    public void setNestPhase(int phase) {
        this.entityData.set(NEST_PHASE, phase);
    }

    public int getGuardDirection() {
        return this.entityData.get(GUARD_DIRECTION);
    }

    public void setGuardDirection(int direction) {
        this.entityData.set(GUARD_DIRECTION, direction);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.RAW_ICEFISH.get();
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
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return Component.translatable("trivia.animalia.chaenocephalus_aceratus");
        } else if (this.getType() == ModEntities.CYGNODRACO_MAWSONI.get()) {
            return Component.translatable("trivia.animalia.cygnodraco_mawsoni");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return Component.translatable("family.animalia.channichthyidae");
        } else if (this.getType() == ModEntities.CYGNODRACO_MAWSONI.get()) {
            return Component.translatable("family.animalia.bathydraconidae");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

    @Override
    public ItemStack getBucketItemStack() {
        if (this.getType() == ModEntities.CYGNODRACO_MAWSONI.get()) {
            return new ItemStack(ModItems.CYGNODRACO_MAWSONI_BUCKET.get());
        }
        return new ItemStack(ModItems.CHAENOCEPHALUS_ACERATUS_BUCKET.get());
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return 24;
        } else if (this.getType() == ModEntities.CYGNODRACO_MAWSONI.get()) {
            return 28;
        }

        return Scannable.super.getScaleforGUI();

    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.6f);
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.CHAENOCEPHALUS_ACERATUS, Scannable.AppName.FISH, "Perciformes");
        HolonetEntities.register(ModEntities.CYGNODRACO_MAWSONI, Scannable.AppName.FISH, "Perciformes");

    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.CHAENOCEPHALUS_ACERATUS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CHAENOCEPHALUS_ACERATUS_PIXEL, this.genVarSize(45, 65, 60));
        } else if (this.getType() == ModEntities.CYGNODRACO_MAWSONI.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CYGNODRACO_MAWSONI_PIXEL, this.genVarSize(41, 55, 45));
        }
        return 1;
    }

    @Override
    public AnimaliaEggTypes getEggType() {
        return AnimaliaEggTypes.BLOCK_EGG;
    }

    @Override
    public int getWalkTime() {
        if (this.getNestPhase() != NEST_PHASE_IDLE) {
            return Integer.MAX_VALUE;
        }
        return 600 + random.nextInt(800);
    }

    @Override
    public int getSwimTime() {
        return 80 + random.nextInt(120);
    }

    @Override
    public boolean canRandomSwim() {
        if (this.getNestPhase() != NEST_PHASE_IDLE) {
            return false;
        }
        return super.canRandomSwim();
    }

    @Override
    public float getSwimSpeed() {
        if (this.isBaby()) {
            return 1.2f;
        }
        if (this.getNestPhase() == NEST_PHASE_GUARDING || this.getNestPhase() == NEST_PHASE_NESTING) {
            return 0;
        }
        if (this.isWalking()) {
            return 1f;
        }
        return 0.4f;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimaliaBreedableWater partner) {
        this.beginNesting();

        this.setAge(6000);
        partner.setAge(6000);
        this.resetLove();
        partner.resetLove();
    }

    private void beginNesting() {
        if (this.isWalking() && this.onGround() && this.isOnValidSubstrate()) {
            this.setNestPhase(NEST_PHASE_NESTING);
            this.nestMakingTicks = NEST_MAKING_TICKS;
            this.getNavigation().stop();
        } else {
            this.wantsToNest = true;
            this.wantsToNestTimeout = 200;
            this.wantsToWalk = true;
        }
    }

    private boolean isOnValidSubstrate() {
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);
        return state.is(Blocks.SAND) || state.is(Blocks.GRAVEL)
                || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            return;
        }

        int phase = this.getNestPhase();

        if (this.wantsToNest && phase == NEST_PHASE_IDLE) {
            this.wantsToNestTimeout--;
            if (this.wantsToNestTimeout <= 0) {
                this.wantsToNest = false;
            } else if (this.isWalking() && this.onGround() && this.isOnValidSubstrate()) {
                this.wantsToNest = false;
                this.setNestPhase(NEST_PHASE_NESTING);
                this.nestMakingTicks = NEST_MAKING_TICKS;
                this.getNavigation().stop();
            }
        }

        switch (phase) {
            case NEST_PHASE_IDLE:
                if (this.nestCooldown > 0) {
                    this.nestCooldown--;
                }
                break;

            case NEST_PHASE_NESTING:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
                this.nestMakingTicks--;
                if (this.nestMakingTicks <= 0) {
                    this.placeNest();
                }
                break;

            case NEST_PHASE_GUARDING:
                if (this.nestPos != null && !(this.level().getBlockState(this.nestPos).getBlock() instanceof MoundNestBlock)) {
                    this.nestPos = null;
                    this.setNestPhase(NEST_PHASE_IDLE);
                    this.setGuardDirection(GUARD_DIR_CENTER);
                    this.nestCooldown = 200 + rand.nextInt(400);
                    break;
                }
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
                this.tickGuardRotation();
                this.nestGuardTicks--;
                if (this.nestGuardTicks <= 0) {
                    this.setNestPhase(NEST_PHASE_HATCHING);
                }
                if (this.attackCooldown <= 0 && this.nestPos != null) {
                    List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                            new AABB(this.nestPos).inflate(0.1, 0.5, 0.1),
                            e -> e != this && e.isAlive());
                    for (LivingEntity target : entities) {
                        target.hurt(this.damageSources().mobAttack(this), 1.0F);
                        this.startEating();
                        this.attackCooldown = 40;
                        break;
                    }
                }
                if (this.attackCooldown > 0) {
                    this.attackCooldown--;
                }
                break;

            case NEST_PHASE_HATCHING:
                this.hatchEggs();
                break;

            case NEST_PHASE_COOLDOWN:
                this.nestCooldown--;
                if (this.nestCooldown <= 0) {
                    this.setNestPhase(NEST_PHASE_IDLE);
                }
                break;
        }
    }

    private void placeNest() {
        BlockPos pos = this.blockPosition();
        BlockState nestState = MoundNestBlock.createForPosition(this.level(), pos);
        this.level().setBlock(pos, nestState, 3);

        BlockEntity be = this.level().getBlockEntity(pos);
        if (be instanceof MoundNestBlockEntity nestBE) {
            nestBE.setEgg(this.getType());
        }

        this.nestPos = pos;
        this.setNestPhase(NEST_PHASE_GUARDING);
        this.nestGuardTicks = 6000;
        this.setGuardDirection(GUARD_DIR_CENTER);
        this.guardDirectionTicks = 200 + rand.nextInt(400);
    }

    private void tickGuardRotation() {
        if (this.guardDirectionTicks > 0) {
            this.guardDirectionTicks--;
            return;
        }

        int currentDir = this.getGuardDirection();

        switch (currentDir) {
            case GUARD_DIR_CENTER:
                int target = rand.nextBoolean() ? GUARD_DIR_TO_LEFT : GUARD_DIR_TO_RIGHT;
                this.setGuardDirection(target);
                this.guardDirectionTicks = ROTATION_TICKS;
                break;

            case GUARD_DIR_TO_LEFT:
                this.setGuardDirection(GUARD_DIR_LEFT);
                this.guardDirectionTicks = 200 + rand.nextInt(400);
                break;

            case GUARD_DIR_TO_RIGHT:
                this.setGuardDirection(GUARD_DIR_RIGHT);
                this.guardDirectionTicks = 200 + rand.nextInt(400);
                break;

            case GUARD_DIR_LEFT:
                this.setGuardDirection(GUARD_DIR_FROM_LEFT);
                this.guardDirectionTicks = ROTATION_TICKS;
                break;

            case GUARD_DIR_RIGHT:
                this.setGuardDirection(GUARD_DIR_FROM_RIGHT);
                this.guardDirectionTicks = ROTATION_TICKS;
                break;

            case GUARD_DIR_FROM_LEFT:
            case GUARD_DIR_FROM_RIGHT:
                this.setGuardDirection(GUARD_DIR_CENTER);
                this.guardDirectionTicks = 200 + rand.nextInt(400);
                break;
        }
    }

    private void hatchEggs() {
        if (this.nestPos != null) {
            BlockEntity be = this.level().getBlockEntity(this.nestPos);
            if (be instanceof MoundNestBlockEntity nestBE) {
                nestBE.clearEgg();
            }
            this.level().destroyBlock(this.nestPos, false);

            Entity baby = this.getType().create(this.level());
            if (baby instanceof ChaenocephalusEntity fry) {
                fry.setAge(-12000);
                fry.setVarSizeMultiplier(fry.genVarSizeMultiplier());
                fry.setGender(rand.nextInt(2));
                fry.moveTo(this.nestPos.getX() + 0.5, this.nestPos.getY() + 0.5, this.nestPos.getZ() + 0.5, rand.nextFloat() * 360, 0);
                this.level().addFreshEntity(fry);
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.nestPos.getX() + 0.5, this.nestPos.getY() + 0.5, this.nestPos.getZ() + 0.5,
                        10, 0.5, 0.3, 0.5, 0.0);
            }

            this.nestPos = null;
        }

        this.setNestPhase(NEST_PHASE_COOLDOWN);
        this.nestCooldown = 12000;
        this.setGuardDirection(GUARD_DIR_CENTER);
    }

    @Override
    public boolean canFallInLove() {
        return this.getNestPhase() == NEST_PHASE_IDLE && this.nestCooldown <= 0 && super.canFallInLove();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if (this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        int phase = this.getNestPhase();

        if (phase == NEST_PHASE_NESTING) {
            controller.setAnimation(RawAnimation.begin().then("nestmaking", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (phase == NEST_PHASE_GUARDING) {
            int dir = this.getGuardDirection();
            switch (dir) {
                case GUARD_DIR_CENTER:
                    controller.setAnimation(RawAnimation.begin().then("guarding", Animation.LoopType.LOOP));
                    break;
                case GUARD_DIR_LEFT:
                    controller.setAnimation(RawAnimation.begin().then("guardingL", Animation.LoopType.LOOP));
                    break;
                case GUARD_DIR_RIGHT:
                    controller.setAnimation(RawAnimation.begin().then("guardingR", Animation.LoopType.LOOP));
                    break;
                case GUARD_DIR_TO_LEFT:
                    controller.setAnimation(RawAnimation.begin().then("rotatingL", Animation.LoopType.PLAY_ONCE));
                    break;
                case GUARD_DIR_TO_RIGHT:
                    controller.setAnimation(RawAnimation.begin().then("rotatingR", Animation.LoopType.PLAY_ONCE));
                    break;
                case GUARD_DIR_FROM_LEFT:
                    controller.setAnimation(RawAnimation.begin().then("rotatingBL", Animation.LoopType.PLAY_ONCE));
                    break;
                case GUARD_DIR_FROM_RIGHT:
                    controller.setAnimation(RawAnimation.begin().then("rotatingBR", Animation.LoopType.PLAY_ONCE));
                    break;
            }

            return PlayState.CONTINUE;
        }

        if(this.isWalking() && !this.isActuallyMoving()) {
            controller.setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
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
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("NestPhase", this.getNestPhase());
        pCompound.putInt("NestGuardTicks", this.nestGuardTicks);
        pCompound.putInt("NestCooldown", this.nestCooldown);
        pCompound.putInt("NestMakingTicks", this.nestMakingTicks);
        pCompound.putInt("GuardDirection", this.getGuardDirection());
        pCompound.putInt("GuardDirectionTicks", this.guardDirectionTicks);
        pCompound.putBoolean("WantsToNest", this.wantsToNest);
        if (this.nestPos != null) {
            pCompound.putInt("NestX", this.nestPos.getX());
            pCompound.putInt("NestY", this.nestPos.getY());
            pCompound.putInt("NestZ", this.nestPos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setNestPhase(pCompound.getInt("NestPhase"));
        this.nestGuardTicks = pCompound.getInt("NestGuardTicks");
        this.nestCooldown = pCompound.getInt("NestCooldown");
        this.nestMakingTicks = pCompound.getInt("NestMakingTicks");
        this.setGuardDirection(pCompound.getInt("GuardDirection"));
        this.guardDirectionTicks = pCompound.getInt("GuardDirectionTicks");
        this.wantsToNest = pCompound.getBoolean("WantsToNest");
        if (pCompound.contains("NestX")) {
            this.nestPos = new BlockPos(pCompound.getInt("NestX"), pCompound.getInt("NestY"), pCompound.getInt("NestZ"));
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
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new NestGuardGoal(this));
        super.registerGoals();
    }

    public static class NestGuardGoal extends Goal {
        private final ChaenocephalusEntity fish;

        public NestGuardGoal(ChaenocephalusEntity fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            int phase = this.fish.getNestPhase();
            return phase == NEST_PHASE_NESTING || phase == NEST_PHASE_GUARDING;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            this.fish.getNavigation().stop();
        }
    }
}