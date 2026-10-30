package net.lemon.animalia.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AnimaliaSwimNodeEvaluator extends SwimNodeEvaluator {
    private Node start;
    public AnimaliaSwimNodeEvaluator() {
        super(false);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z, Mob mob) {
        BlockPathTypes type = super.getBlockPathType(level, x, y, z, mob);
        if (type != BlockPathTypes.WATER) {
            return type;
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = x; i < x + this.entityWidth; ++i) {
            for (int j = y; j < y + this.entityHeight; ++j) {
                for (int k = z; k < z + this.entityDepth; ++k) {
                    pos.set(i, j, k);
                    if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }
        return type;
    }

    @Override
    public Node getStart() {
        AABB box = this.mob.getBoundingBox();
        int x = Mth.floor(box.minX);
        int y = Mth.floor(box.minY + 0.5D);
        int z = Mth.floor(box.minZ);
        if (this.getBlockPathType(this.level, x, y, z, this.mob) == BlockPathTypes.BLOCKED) {
            y = Mth.floor(box.minY);
        }
        this.start = this.getNode(x, y, z);
        return this.start;
    }

    @Override
    public int getNeighbors(Node[] pOutputArray, Node pNode) {
        int count = super.getNeighbors(pOutputArray, pNode);
        if (pNode != this.start) {
            return count;
        }
        int kept = 0;
        for (int i = 0; i < count; ++i) {
            Node node = pOutputArray[i];
            Vec3 move = new Vec3(node.x + 0.5D, node.y, node.z + 0.5D).subtract(this.mob.position());
            if (this.mob.level().noCollision(this.mob, this.mob.getBoundingBox().expandTowards(move))) {
                pOutputArray[kept++] = node;
            }
        }
        return kept;
    }
}