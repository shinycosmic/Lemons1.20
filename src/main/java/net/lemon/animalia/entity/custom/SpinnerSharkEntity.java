package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.EatDroppedItemsGoal;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;

public class SpinnerSharkEntity extends FishBase implements Scannable, GeoEntity {
    public static final int SPIN_PHASE_NONE = 0;
    public static final int SPIN_PHASE_SPIN = 1;
    public static final int SPIN_PHASE_BREACH = 2;
    private static final EntityDataAccessor<Integer> SPIN_PHASE = SynchedEntityData.defineId(SpinnerSharkEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SpinnerSharkEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPIN_PHASE, SPIN_PHASE_NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof AvoidEntityGoal);
        this.goalSelector.addGoal(4, new SpinnerSharkSpinGoal(this));
        this.goalSelector.addGoal(5, new EatDroppedItemsGoal<>(this, 1.2D, 10.0F));
    }

    public int getSpinPhase() { return this.entityData.get(SPIN_PHASE); }

    public void setSpinPhase(int phase) { this.entityData.set(SPIN_PHASE, phase); }

    public boolean isSpinning() { return this.getSpinPhase() != SPIN_PHASE_NONE; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isSpinning()) {
            this.setYRot(this.yRotO);
            this.yBodyRot = this.yBodyRotO;
            this.yHeadRot = this.yHeadRotO;
        }
    }

    @Override
    public boolean shouldJumpOnFlop() { return false; }

    @Override
    public boolean bucketable() { return this.isBaby(); }

    @Override
    public int getEatLength() { return 20; }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15D)
                .add(Attributes.MOVEMENT_SPEED, 0.7f)
                .add(Attributes.ATTACK_DAMAGE, 4f)
                .build();
    }

    @Override
    public SpawnBand spawnBand() {
        return SpawnBand.SHALLOW;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.RAW_FISH.get();
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
        return Component.translatable("trivia.animalia.carcharhinus_brevipinna");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.carcharhinidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carcharhiniformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.CARCHARHINUS_BREVIPINNA.get()) {
            return 18;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.7f);
    }

    public static void registerHolonet() {
        HolonetEntities.register(ModEntities.CARCHARHINUS_BREVIPINNA, AppName.FISH, "Carcharhiniformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.CARCHARHINUS_BREVIPINNA.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(44, this.genVarSize(170, 300, 200));
        }
        return 1;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        animationState.getController().transitionLength(5);
        if (this.getSpinPhase() == SPIN_PHASE_BREACH) {
            animationState.getController().transitionLength(0);
            animationState.getController().setAnimation(RawAnimation.begin().then("breach", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (this.getSpinPhase() == SPIN_PHASE_SPIN) {
            animationState.getController().setAnimation(RawAnimation.begin().then("toSpin", Animation.LoopType.PLAY_ONCE).thenLoop("spin"));
            return PlayState.CONTINUE;
        }
        if (this.onGround() || (this.isInWater() && !this.isActuallyMoving())) {
            animationState.getController().setAnimation(RawAnimation.begin().then("idle0", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        if (this.isFast()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("swimFast", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
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

    public static class SpinnerSharkSpinGoal extends Goal {
        private final SpinnerSharkEntity shark;
        private final Set<Entity> hit = new HashSet<>();
        @Nullable
        private FishBase leader;
        private Vec3 center;
        private Vec3 side;
        private Vec3 under;
        private Vec3 spinDir;
        private int spinTicks;
        private int breachTicks;
        private int spinCooldown;
        private int nextScan;
        private int deadline;

        public SpinnerSharkSpinGoal(SpinnerSharkEntity shark) {
            this.shark = shark;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.shark.isInWater() || this.shark.isBaby()
                    || this.shark.tickCount < this.spinCooldown || this.shark.tickCount < this.nextScan) {
                return false;
            }
            this.nextScan = this.shark.tickCount + 100 + this.shark.getRandom().nextInt(100);
            return this.findSchool();
        }

        @Override
        public boolean canContinueToUse() {
            return this.leader != null || this.shark.isSpinning();
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.deadline = this.shark.tickCount + 400;
            Vec3 toShark = new Vec3(this.shark.getX() - this.center.x, 0, this.shark.getZ() - this.center.z);
            if (toShark.lengthSqr() < 0.25) {
                double angle = this.shark.getRandom().nextDouble() * Math.PI * 2;
                this.side = new Vec3(Math.cos(angle), 0, Math.sin(angle));
            } else {
                this.side = toShark.normalize();
            }
            this.moveToUnder();
        }

        @Override
        public void tick() {
            switch (this.shark.getSpinPhase()) {
                case SPIN_PHASE_SPIN -> this.tickSpin();
                case SPIN_PHASE_BREACH -> this.tickBreach();
                default -> this.tickApproach();
            }
        }

        @Override
        public void stop() {
            this.leader = null;
            this.shark.getNavigation().stop();
            if (this.shark.isSpinning()) {
                this.shark.setSpinPhase(SPIN_PHASE_NONE);
            }
        }

        private boolean findSchool() {
            List<FishBase> fish = this.shark.level().getEntitiesOfClass(FishBase.class,
                    this.shark.getBoundingBox().inflate(24.0D, 12.0D, 24.0D),
                    f -> f.isAlive() && f.isInWater() && f.hasSchool() && f.getType().is(ModTags.EntityTypes.FORAGE_FISH));
            Map<FishBase, List<FishBase>> schools = new HashMap<>();
            for (FishBase f : fish) {
                schools.computeIfAbsent(f.getSchoolLeader(), k -> new ArrayList<>()).add(f);
            }
            FishBase best = null;
            int bestSize = 3;
            double bestDist = Double.MAX_VALUE;
            for (Map.Entry<FishBase, List<FishBase>> school : schools.entrySet()) {
                int size = school.getValue().size();
                double dist = this.shark.distanceToSqr(school.getKey());
                if (size > bestSize || (size == bestSize && dist < bestDist)) {
                    best = school.getKey();
                    bestSize = size;
                    bestDist = dist;
                }
            }
            if (best == null) {
                return false;
            }
            this.leader = best;
            this.center = this.mean(schools.get(best));
            return true;
        }

        private Vec3 mean(List<FishBase> members) {
            Vec3 sum = Vec3.ZERO;
            for (FishBase f : members) {
                sum = sum.add(f.position());
            }
            return sum.scale(1.0D / members.size());
        }

        private void refreshCenter() {
            List<FishBase> members = this.shark.level().getEntitiesOfClass(FishBase.class,
                    this.leader.getBoundingBox().inflate(8.0D),
                    f -> f.isAlive() && f.getSchoolLeader() == this.leader);
            if (!members.isEmpty()) {
                this.center = this.mean(members);
            }
        }

        private void moveToUnder() {
            BlockPos pos = BlockPos.containing(this.center);
            int depth = 0;
            while (depth < 6 && this.shark.level().getFluidState(pos.below()).is(FluidTags.WATER)) {
                pos = pos.below();
                depth++;
            }
            double d = Mth.clamp(depth - 1, 1, 5);
            this.under = this.center.add(this.side.scale(d)).add(0, -d, 0);
            this.shark.getNavigation().moveTo(this.under.x, this.under.y, this.under.z, 1.3D);
        }

        private void tickApproach() {
            if (this.leader == null || !this.leader.isAlive() || !this.leader.isSchoolLeader() || this.leader.getSchoolSize() < 2
                    || this.shark.hurtTime > 0 || this.shark.tickCount > this.deadline || !this.shark.isInWater()) {
                this.leader = null;
                return;
            }
            if (this.shark.tickCount % 20 == 0) {
                this.refreshCenter();
                this.moveToUnder();
            }
            if (this.shark.distanceToSqr(this.under) < 2.25D) {
                this.startSpin();
            }
        }

        private void startSpin() {
            this.shark.getNavigation().stop();
            this.spinDir = this.center.subtract(this.shark.position()).normalize();
            float yaw = (float) (Mth.atan2(this.spinDir.z, this.spinDir.x) * Mth.RAD_TO_DEG) - 90.0F;
            this.shark.setYRot(yaw);
            this.shark.yBodyRot = yaw;
            this.shark.setYHeadRot(yaw);
            this.spinTicks = 60 + this.shark.getRandom().nextInt(60);
            this.hit.clear();
            this.shark.setSpinPhase(SPIN_PHASE_SPIN);
        }

        private void tickSpin() {
            if (--this.spinTicks <= 0 || this.shark.horizontalCollision || this.shark.verticalCollision) {
                this.endSpin();
                return;
            }
            if (!this.shark.isUnderWater()) {
                this.startBreach();
                return;
            }
            this.shark.setDeltaMovement(this.spinDir.scale(0.5D));
            this.shark.hasImpulse = true;
            for (Entity e : this.shark.level().getEntities(this.shark, this.shark.getBoundingBox().inflate(0.2D),
                    e -> e instanceof LivingEntity && e.isAlive() && e.getType() != this.shark.getType() && !this.hit.contains(e))) {
                this.shark.doHurtTarget(e);
                this.hit.add(e);
            }
        }

        private void startBreach() {
            this.shark.setDeltaMovement(Vec3.ZERO);
            this.breachTicks = 30;
            this.shark.setSpinPhase(SPIN_PHASE_BREACH);
        }

        private void tickBreach() {
            if (!this.shark.isUnderWater()) {
                this.shark.setDeltaMovement(this.shark.getDeltaMovement().add(0, -0.01D, 0));
            }
            if (--this.breachTicks <= 0) {
                this.endSpin();
            }
        }

        private void endSpin() {
            this.shark.setSpinPhase(SPIN_PHASE_NONE);
            this.spinCooldown = this.shark.tickCount + 600 + this.shark.getRandom().nextInt(600);
            this.leader = null;
        }
    }
}

