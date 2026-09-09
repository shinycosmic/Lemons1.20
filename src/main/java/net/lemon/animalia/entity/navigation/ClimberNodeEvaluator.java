package net.lemon.animalia.entity.navigation;

import net.lemon.animalia.entity.bases.helpers.ICanClimb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.Nullable;

public class ClimberNodeEvaluator extends WalkNodeEvaluator {

    private final BlockPos.MutableBlockPos faceCursor = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos typeCursor = new BlockPos.MutableBlockPos();

    @Nullable
    private ICanClimb climber;
    private boolean climbing;
    private boolean climbUp;
    private boolean climbDown;
    private boolean ceilings;
    private float upMalus;
    private float downMalus;

    @Override
    public void prepare(PathNavigationRegion region, Mob mob) {
        super.prepare(region, mob);
        this.climber = (ICanClimb) mob;
        this.climbing = this.climber.canPathfindWithWalls();
        if (this.climbing) {
            this.climbUp = this.climber.canClimbUp();
            this.climbDown = this.climber.canClimbDown();
            this.ceilings = this.climber.canClimbCeilings();
            this.upMalus = this.climber.climbUpMalus();
            this.downMalus = this.climber.climbDownMalus();
        }
    }

    @Override
    public void done() {
        this.climber = null;
        this.climbing = false;
        super.done();
    }

    @Override
    public Node getStart() {
        if (this.climbing && this.climber.isAttached()) {
            return this.getStartNode(this.mob.blockPosition());
        }
        return super.getStart();
    }

    @Override
    public int getNeighbors(Node[] outputArray, Node node) {
        int i = super.getNeighbors(outputArray, node);
        if (!this.climbing) {
            return i;
        }
        BlockPathTypes above = this.getCachedBlockType(this.mob, node.x, node.y + 1, node.z);
        BlockPathTypes at = this.getCachedBlockType(this.mob, node.x, node.y, node.z);
        int step = this.mob.getPathfindingMalus(above) >= 0.0F && at != BlockPathTypes.STICKY_HONEY
                ? Mth.floor(Math.max(1.0F, this.mob.getStepHeight())) : 0;
        double floor = this.getFloorLevel(new BlockPos(node.x, node.y, node.z));

        if (this.climbUp) {
            Node up = this.findAcceptedNode(node.x, node.y + 1, node.z, Math.max(0, step - 1), floor, Direction.UP, at);
            if (this.isClimbNeighborValid(up, node, node.y + 1)) {
                up.costMalus += this.upMalus;
                outputArray[i++] = up;
            }
        }

        if (this.climbDown) {
            Node down = this.findAcceptedNode(node.x, node.y - 1, node.z, step, floor, Direction.DOWN, at);
            if (this.isClimbNeighborValid(down, node, node.y - 1) && at != BlockPathTypes.TRAPDOOR) {
                down.costMalus += this.downMalus;
                outputArray[i++] = down;
            }
        }
        return i;
    }

    private boolean isClimbNeighborValid(@Nullable Node neighbor, Node node, int expectedY) {
        return this.isNeighborValid(neighbor, node) && neighbor.y == expectedY
                && this.hasClimbableFace(this.level, neighbor.x, neighbor.y, neighbor.z);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
        BlockPathTypes type = super.getBlockPathType(level, x, y, z);
        if (this.climbing && type == BlockPathTypes.OPEN && this.hasClimbableFace(level, x, y, z)) {
            return WalkNodeEvaluator.checkNeighbourBlocks(level, this.typeCursor.set(x, y, z), BlockPathTypes.WALKABLE);
        }
        return type;
    }

    protected boolean hasClimbableFace(BlockGetter level, int x, int y, int z) {
        for (Direction face : ICanClimb.CLIMB_FACES) {
            if (face == Direction.UP && !this.ceilings) {
                continue;
            }
            this.faceCursor.set(x + face.getStepX(), y + face.getStepY(), z + face.getStepZ());
            if (this.climber.canAttachTo(level, this.faceCursor)) {
                return true;
            }
        }
        return false;
    }
}