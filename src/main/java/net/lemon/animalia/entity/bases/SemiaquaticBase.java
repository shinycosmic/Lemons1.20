package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.FindNearestBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
                mob.setPathfindingMalus(BlockPathTypes.WATER, pref <= 0.0F ? -1.0F : Math.max(0.0F, (0.5F - pref) * 12.0F));
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

}
