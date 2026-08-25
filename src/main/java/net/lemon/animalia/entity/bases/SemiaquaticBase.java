package net.lemon.animalia.entity.bases;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
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
import net.minecraftforge.fluids.FluidType;

public abstract class SemiaquaticBase extends AnimaliaLandBase{


    protected SemiaquaticBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, this.waterMalus());
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
    }

    public float waterPreference() {
        return 0.5f;
    }

    private float waterMalus() {
        return this.waterPreference() <= 0.0F ? -1.0F : Math.max(0.0F, (0.5F - this.waterPreference()) * 16.0F);
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
                mob.setPathfindingMalus(BlockPathTypes.WATER,
                        pref <= 0.0F ? -1.0F : Math.max(0.0F, (0.5F - pref) * 12.0F));
                mob.setPathfindingMalus(BlockPathTypes.WALKABLE,
                        Math.max(0.0F, (pref - 0.5F) * 12.0F));
            }
        }
    }

}
