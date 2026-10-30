package net.lemon.animalia.entity.navigation;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AnimaliaSwimPathNavigation extends WaterBoundPathNavigation {
    public AnimaliaSwimPathNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new AnimaliaSwimNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    @Override
    protected void followThePath() {
        Vec3 pos = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        Vec3i next = this.path.getNextNodePos();

        double dx = Math.abs(this.mob.getX() - (next.getX() + (this.mob.getBbWidth() + 1.0D) / 2.0D));
        double dy = next.getY() - this.mob.getY();
        double dz = Math.abs(this.mob.getZ() - (next.getZ() + (this.mob.getBbWidth() + 1.0D) / 2.0D));

        Vec3 toNode = this.path.getNextEntityPos(this.mob).subtract(this.mob.position());
        boolean reached = dx <= this.maxDistanceToWaypoint && dz <= this.maxDistanceToWaypoint && Math.abs(dy) < 1.0D && this.level.noCollision(this.mob, this.mob.getBoundingBox().expandTowards(toNode));
        int after = this.path.getNextNodeIndex() + 1;
        boolean last = after >= this.path.getNodeCount();
        boolean clearToAfter = !last && this.canMoveDirectly(pos, this.path.getEntityPosAtNode(this.mob, after));
        boolean shortcut = clearToAfter
                && pos.closerThan(Vec3.atBottomCenterOf(next), 2.0D)
                && this.canCutCorner(this.path.getNextNode().type);

        if (reached && (last || clearToAfter) || shortcut) {
            this.path.advance();
        }
        this.doStuckDetection(pos);
    }

    @Override
    protected boolean canMoveDirectly(Vec3 pPosVec31, Vec3 pPosVec32) {
        Vec3 move = pPosVec32.subtract(this.mob.position());
        return this.level.noCollision(this.mob, this.mob.getBoundingBox().expandTowards(move));
    }
}