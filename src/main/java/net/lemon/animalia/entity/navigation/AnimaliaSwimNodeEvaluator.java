package net.lemon.animalia.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

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
}