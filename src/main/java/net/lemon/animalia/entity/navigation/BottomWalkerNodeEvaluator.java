package net.lemon.animalia.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;

public class BottomWalkerNodeEvaluator extends WalkNodeEvaluator {

    @Override
    public Node getStart() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int y = this.mob.getBlockY();
        pos.set(this.mob.getX(), y, this.mob.getZ());
        BlockState state = this.level.getBlockState(pos);
        if (this.mob.isInWater()) {
            while (this.level.getFluidState(pos).is(FluidTags.WATER) && y > this.level.getMinBuildHeight()) {
                y--;
                pos.set(this.mob.getX(), y, this.mob.getZ());
            }

            y++;
        }
        else if (this.mob.onGround()) {
            y = Mth.floor(this.mob.getY() + 0.5D);
        }
        else {
            BlockPos blockpos;
            for (blockpos = this.mob.blockPosition();
                 (this.level.getBlockState(blockpos).isAir()
                         || this.level.getBlockState(blockpos).isPathfindable(this.level, blockpos, PathComputationType.LAND))
                         && blockpos.getY() > this.level.getMinBuildHeight();
                 blockpos = blockpos.below()) {}

            y = blockpos.above().getY();
        }

        BlockPos base = this.mob.blockPosition();
        return this.getStartNode(new BlockPos(base.getX(), y, base.getZ()));
    }

    @Override
    protected double getFloorLevel(BlockPos pos) {
        if (this.level.getFluidState(pos).is(FluidTags.WATER)) {
            BlockPos below = pos.below();
            return WalkNodeEvaluator.getFloorLevel(this.level, below);
        }
        return super.getFloorLevel(pos);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z, Mob mob) {
        BlockPathTypes type = super.getBlockPathType(level, x, y, z, mob);
        if (type == BlockPathTypes.WATER) {
            return BlockPathTypes.WALKABLE;
        }
        return type;
    }
}
