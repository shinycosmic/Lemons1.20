package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public abstract class SemiaquaticBase extends AnimaliaLandBase{

    private int dryTicks;

    protected SemiaquaticBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(
                g -> g.getGoal() instanceof FloatGoal
                        || g.getGoal() instanceof WaterAvoidingRandomStrollGoal
        );
        if (this.dryTolerance() > 0) {
            this.goalSelector.addGoal(4, new GoToWaterGoal(this, 1.3D, 12));
        }
        this.goalSelector.addGoal(5, new LandStrollGoal(this, 1.0D));
        if (this.waterPreference() > 0.0F) {
            this.goalSelector.addGoal(5, new WaterStrollGoal(this, 1.0D, (int) (120 / this.waterPreference())));
        }
        if (this.canDrownInFluidType(ForgeMod.WATER_TYPE.get())) {
            this.goalSelector.addGoal(0, new BreathAirGoal(this));
        }
    }

    public float waterPreference() {
        return 0.5f;
    }

    public int dryTolerance() {
        return 0;
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

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01F, this.isMovementLockedByIdle() || this.isGrazing() ? Vec3.ZERO : pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.getSwimSpeed()));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
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

    static class LandStrollGoal extends RandomStrollGoal {
        LandStrollGoal(SemiaquaticBase mob, double speedMult) {
            super(mob, speedMult);
        }

        @Override
        protected Vec3 getPosition() {
            Level level = this.mob.level();
            RandomSource random = this.mob.getRandom();
            for (int i = 0; i < 10; ++i) {
                BlockPos pos = this.mob.blockPosition().offset(random.nextInt(21) - 10, random.nextInt(15) - 7, random.nextInt(21) - 10);
                if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                        && level.getFluidState(pos).isEmpty()
                        && !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty()) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }

    static class WaterStrollGoal extends RandomStrollGoal {
        WaterStrollGoal(SemiaquaticBase mob, double speedMult, int interval) {
            super(mob, speedMult, interval);
        }

        @Override
        protected Vec3 getPosition() {
            RandomSource random = this.mob.getRandom();
            for (int i = 0; i < 10; ++i) {
                BlockPos pos = this.mob.blockPosition().offset(random.nextInt(21) - 10, random.nextInt(15) - 7, random.nextInt(21) - 10);
                if (this.mob.level().getFluidState(pos).is(FluidTags.WATER)) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }

}
