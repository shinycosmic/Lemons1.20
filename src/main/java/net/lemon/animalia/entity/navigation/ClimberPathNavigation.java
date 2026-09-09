package net.lemon.animalia.entity.navigation;

import com.google.common.collect.ImmutableSet;
import net.lemon.animalia.entity.bases.helpers.ICanClimb;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.Nullable;

public class ClimberPathNavigation extends GroundPathNavigation {

    private final ICanClimb climber;

    public ClimberPathNavigation(Mob mob, Level level) {
        super(mob, level);
        this.climber = (ICanClimb) mob;
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new ClimberNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return ICanClimb.onSolid(this.mob) || this.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    public @Nullable Path createPath(BlockPos pos, int accuracy) {
        if (this.climber.canPathfindWithWalls()) {
            return this.createPath(ImmutableSet.of(pos), 8, false, accuracy);
        }
        return super.createPath(pos, accuracy);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (this.climber.canPathfindWithWalls() && this.climber.hasClimbableFace(this.level, pos)) {
            return true;
        }
        return super.isStableDestination(pos);
    }
}