package net.lemon.animalia.entity.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class WaterBottomPathNavigation extends PathNavigation {
    public WaterBottomPathNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new BottomWalkerNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    //getEntityPosition
    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(1D), this.mob.getZ());
    }

    //canNavigate
    @Override
    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.canFloat() && this.isInLiquid();
    }
}
