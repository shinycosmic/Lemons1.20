package net.lemon.animalia.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.AABB;

public class AnimaliaSwimNodeEvaluator extends SwimNodeEvaluator {
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
        return this.getNode(x, y, z);
    }
}