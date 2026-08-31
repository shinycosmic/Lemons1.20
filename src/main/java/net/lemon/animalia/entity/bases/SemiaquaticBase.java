package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.function.Predicate;

public abstract class SemiaquaticBase extends AnimaliaLandBase{

    private int dryTicks;

    protected SemiaquaticBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, false);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof FloatGoal || g.getGoal() instanceof WaterAvoidingRandomStrollGoal);
        this.goalSelector.addGoal(3, new GoToShallowGoal(this, 1.3D, 12));
        this.goalSelector.addGoal(4, new GoToWaterGoal(this, 1.3D, 12));
        this.goalSelector.addGoal(4, new GoToLandGoal(this, 1.3D, 16));
        this.goalSelector.addGoal(5, new LandStrollGoal(this, 1.0D));
        if (this.waterPreference() > 0.0F && !this.isBottomWalker()) this.goalSelector.addGoal(5, new WaterStrollGoal(this, 1.0D, (int) (120 / this.waterPreference())));
        if (this.canDrownInFluidType(ForgeMod.WATER_TYPE.get())) this.goalSelector.addGoal(0, new BreathAirGoal(this));
    }

    public boolean isBottomWalker() {
        return false;
    }

    public float waterPreference() {
        return 0.5f;
    }

    public int dryTolerance() {
        return 0;
    }

    public int depthTolerance() {
        return 64;
    }

    public int surfaceY(BlockPos pos) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() < this.level().getMaxBuildHeight() && this.level().isWaterAt(cursor)) {
            cursor.move(Direction.UP);
        }
        return cursor.getY();
    }

    public int shallowY(BlockPos pos) {
        return this.surfaceY(pos) - this.depthTolerance();
    }

    public boolean tooDeep(BlockPos pos, int shallowY) {
        return this.level().isWaterAt(new BlockPos(pos.getX(), shallowY - 1, pos.getZ()));
    }

    public boolean tooDeep(BlockPos pos) {
        return this.tooDeep(pos, this.shallowY(pos));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.dryTolerance() > 0) {
            if (this.isInWater()) {
                this.dryTicks = 0;
            } else {
                ++this.dryTicks;
            }
        }
    }

    public float getSwimSpeed() {
        return 1.0f;
    }

    public boolean wantsToSwimUp() {
        return this.getMoveControl().hasWanted() && this.getMoveControl().getWantedY() > this.getY() + 0.5D;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && (this.isUnderWater() || (this.isInWater() && !this.onGround()))
                && !(this.isBottomWalker() && this.onGround() && !this.wantsToSwimUp())) {
            this.moveRelative(0.01F, this.isMovementLockedByIdle() || this.isGrazing() ? Vec3.ZERO : pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.getSwimSpeed()));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.isBottomWalker() && this.getNavigation().isDone()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal mate) {
        if (this.getBirthLocation() == BirthLocation.ANY) {
            super.spawnChildFromBreeding(level, mate);
            return;
        }
        this.setPregnant(true);
        this.setAge(6000);
        mate.setAge(6000);
        this.resetLove();
        mate.resetLove();
    }

    public boolean prefersShallow() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean babyFollowsParent() {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SemiaquaticPathNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (this.waterPreference() > 0.5F && level.getFluidState(pos).is(FluidTags.WATER)) {
            return 10.0F * (this.waterPreference() - 0.5F) * 2.0F;
        }
        return super.getWalkTargetValue(pos, level);
    }

    static class SemiaquaticPathNavigation extends AmphibiousPathNavigation {
        SemiaquaticPathNavigation(SemiaquaticBase mob, Level level) {
            super(mob, level);
        }

        @Override
        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new SemiaquaticNodeEvaluator(((SemiaquaticBase) this.mob).prefersShallow());
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }

        @Override
        public boolean canCutCorner(BlockPathTypes pathType) {
            return pathType != BlockPathTypes.WATER_BORDER && super.canCutCorner(pathType);
        }
    }

    static class SemiaquaticNodeEvaluator extends AmphibiousNodeEvaluator {
        SemiaquaticNodeEvaluator(boolean prefersShallowSwimming) {
            super(prefersShallowSwimming);
        }

        @Override
        public void prepare(PathNavigationRegion region, Mob mob) {
            super.prepare(region, mob);
            if(mob instanceof SemiaquaticBase semiaquatic) {
                float pref = semiaquatic.waterPreference();
                mob.setPathfindingMalus(BlockPathTypes.WATER, pref <= 0.0F ? (mob.isInWater() ? 8.0F : -1.0F) : Math.max(0.0F, (0.5F - pref) * 12.0F));
                mob.setPathfindingMalus(BlockPathTypes.WALKABLE, Math.max(0.0F, (pref - 0.5F) * 12.0F));
            }
        }
    }

    static class GoToWaterGoal extends FindNearestBlockGoal {
        private final SemiaquaticBase semiaquatic;

        GoToWaterGoal(SemiaquaticBase mob, double speedMult, int searchRange) {
            super(mob, speedMult, searchRange, state -> state.getFluidState().is(FluidTags.WATER), 1, false);
            this.semiaquatic = mob;
        }

        @Override
        protected boolean passCheck() {
            return this.semiaquatic.dryTicks > this.semiaquatic.dryTolerance();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.semiaquatic.isInWater() && super.canContinueToUse();
        }

        @Override
        public boolean isInterruptable() {
            return true;
        }
    }

    static class GoToShallowGoal extends FindNearestBlockGoal {
        private final SemiaquaticBase semiaquatic;
        private int shallowY;

        GoToShallowGoal(SemiaquaticBase mob, double speedMult, int searchRange) {
            super(mob, speedMult, searchRange, state -> state.getFluidState().is(FluidTags.WATER), 1, false);
            this.semiaquatic = mob;
        }

        @Override
        protected boolean passCheck() {
            if (!this.semiaquatic.isInWater() || this.semiaquatic.depthTolerance() <= 0 || !this.semiaquatic.getNavigation().isDone()) {
                return false;
            }
            this.shallowY = this.semiaquatic.shallowY(this.semiaquatic.blockPosition());
            return this.semiaquatic.tooDeep(this.semiaquatic.blockPosition(), this.shallowY);
        }

        @Override
        protected boolean isTarget(BlockPos pos) {
            return super.isTarget(pos) && !this.semiaquatic.tooDeep(pos, this.shallowY);
        }

        @Override
        public boolean canContinueToUse() {
            return this.semiaquatic.isInWater() && super.canContinueToUse();
        }

        @Override
        public boolean isInterruptable() {
            return true;
        }
    }

    static class GoToLandGoal extends FindNearestBlockGoal {
        private final SemiaquaticBase semiaquatic;

        GoToLandGoal(SemiaquaticBase mob, double speedMult, int searchRange) {
            super(mob, speedMult, searchRange, state -> true, 1, false);
            this.semiaquatic = mob;
        }

        @Override
        protected boolean passCheck() {
            return this.semiaquatic.isInWater()
                    && (this.semiaquatic.depthTolerance() <= 0
                    || !this.semiaquatic.isActiveTime(this.semiaquatic));
        }

        @Override
        protected boolean isTarget(BlockPos pos) {
            return super.isTarget(pos) && isLand(this.semiaquatic.level(), pos);
        }

        @Override
        public boolean canContinueToUse() {
            return this.semiaquatic.isInWater() && super.canContinueToUse();
        }

        @Override
        public boolean isInterruptable() {
            return true;
        }
    }

    static class LandStrollGoal extends RandomStrollGoal {
        private final SemiaquaticBase semiaquatic;

        LandStrollGoal(SemiaquaticBase mob, double speedMult) {
            super(mob, speedMult);
            this.semiaquatic = mob;
        }

        @Override
        protected Vec3 getPosition() {
            Level level = this.mob.level();
            RandomSource random = this.mob.getRandom();
            for (int i = 0; i < 10; ++i) {
                BlockPos pos = this.mob.blockPosition().offset(random.nextInt(21) - 10, random.nextInt(15) - 7, random.nextInt(21) - 10);
                if (this.semiaquatic.isBottomWalker() ? isFloor(level, pos) && !this.semiaquatic.tooDeep(pos) : isLand(level, pos)) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }

    static class WaterStrollGoal extends RandomStrollGoal {
        private final SemiaquaticBase semiaquatic;

        WaterStrollGoal(SemiaquaticBase mob, double speedMult, int interval) {
            super(mob, speedMult, interval);
            this.semiaquatic = mob;
        }

        @Override
        protected Vec3 getPosition() {
            RandomSource random = this.mob.getRandom();
            boolean deep = this.semiaquatic.tooDeep(this.semiaquatic.blockPosition());
            for (int i = 0; i < 10; ++i) {
                BlockPos pos = this.mob.blockPosition().offset(random.nextInt(21) - 10, random.nextInt(15) - 7, random.nextInt(21) - 10);
                if (this.mob.level().getFluidState(pos).is(FluidTags.WATER) && (deep || !this.semiaquatic.tooDeep(pos))) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }

    public static class SemiaquaticPanicGoal extends LandPanicGoal {
        private final Predicate<BlockState> targetType;
        private final int range;

        private BlockPos targetPos;

        public SemiaquaticPanicGoal(SemiaquaticBase mob, double speedMult, int fleeLength, double proximityRange, int range) {
            this(mob, speedMult, fleeLength, proximityRange, range, state -> state.getFluidState().is(FluidTags.WATER));
        }

        public SemiaquaticPanicGoal(SemiaquaticBase mob, double speedMult, int fleeLength, double proximityRange, int range, TagKey<Block> tag) {
            this(mob, speedMult, fleeLength, proximityRange, range, state -> state.is(tag));
        }

        public SemiaquaticPanicGoal(SemiaquaticBase mob, double speedMult, int fleeLength, double proximityRange, int range, Block block) {
            this(mob, speedMult, fleeLength, proximityRange, range, state -> state.is(block));
        }

        private SemiaquaticPanicGoal(SemiaquaticBase mob, double speedMult, int fleeLength, double proximityRange, int range, Predicate<BlockState> refuge) {
            super(mob, speedMult, fleeLength, proximityRange);
            this.range = range;
            this.targetType = refuge;
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            this.targetPos = this.findTarget();
            return this.targetPos != null;
        }

        private BlockPos findTarget() {
            Level level = this.mob.level();
            BlockPos pos = this.mob.blockPosition();
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return null;
            }
            BlockPos found = BlockPos.findClosestMatch(pos, this.range, 1, p -> this.targetType.test(level.getBlockState(p))).orElse(null);
            if (found == null) {
                return null;
            }
            Path path = this.mob.getNavigation().createPath(found, 1);
            return path != null && path.canReach() ? found.immutable() : null;
        }

        @Override
        protected void moveAway() {
            this.mob.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speedMult);
        }

        @Override
        public void stop() {
            super.stop();
            this.targetPos = null;
        }
    }


}
