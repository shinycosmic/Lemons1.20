package net.lemon.animalia.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;

public class TermiteMoundFeature extends Feature<TermiteMoundConfiguration> {

    public TermiteMoundFeature(Codec<TermiteMoundConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TermiteMoundConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        TermiteMoundConfiguration config = context.config();

        if (!level.getFluidState(origin).isEmpty() || !level.getFluidState(origin.below()).isEmpty()) {
            return false;
        }

        BlockState sandstone = config.sandstone().defaultBlockState();
        BlockState mound = config.mound().defaultBlockState();
        if (level.getBlockState(origin.below()).is(config.borderGround())) {
            sandstone = config.borderSandstone().defaultBlockState();
            mound = config.borderMound().defaultBlockState();
        }

        List<BlockPos> layer = this.baseFootprint(origin, random);
        int wanted = layer.size();
        layer.removeIf(column -> !this.canStandOn(level, column));
        if (layer.size() * 2 < wanted) {
            return false;
        }

        int height = 3 + random.nextInt(3);
        double shrink = 0.35D + random.nextDouble() * 0.4D;
        BlockPos anyPlaced = null;
        boolean placedMound = false;

        for (int y = 0; y < height; y++) {
            if (y > 0) {
                layer = this.thinTo(layer, this.nextSize(layer.size(), shrink), random);
            }
            for (BlockPos column : layer) {
                BlockPos pos = column.above(y);
                if (!this.canReplace(level, pos)) {
                    continue;
                }
                boolean isMound = random.nextInt(4) == 0;
                this.setBlock(level, pos, isMound ? mound : sandstone);
                placedMound |= isMound;
                if (anyPlaced == null) {
                    anyPlaced = pos;
                }
            }
        }

        if (anyPlaced == null) {
            return false;
        }
        if (!placedMound) {
            this.setBlock(level, anyPlaced, mound);
        }
        return true;
    }

    private List<BlockPos> baseFootprint(BlockPos origin, RandomSource random) {
        int width = 3 + random.nextInt(3);
        int depth = 3 + random.nextInt(3);
        int minX = origin.getX() - random.nextInt(width);
        int minZ = origin.getZ() - random.nextInt(depth);
        double centreX = minX + (width - 1) / 2.0D;
        double centreZ = minZ + (depth - 1) / 2.0D;
        double span = Math.sqrt((width - 1) * (width - 1) + (depth - 1) * (depth - 1)) / 2.0D;
        List<BlockPos> footprint = new ArrayList<>();
        for (int x = minX; x < minX + width; x++) {
            for (int z = minZ; z < minZ + depth; z++) {
                BlockPos column = new BlockPos(x, origin.getY(), z);
                double distance = Math.sqrt((x - centreX) * (x - centreX) + (z - centreZ) * (z - centreZ));
                if (column.equals(origin) || random.nextDouble() > 0.65D * distance / span) {
                    footprint.add(column);
                }
            }
        }
        return footprint;
    }

    private int nextSize(int size, double shrink) {
        return Math.max(1, Math.min((int) Math.round(size * shrink), size - 1));
    }

    private List<BlockPos> thinTo(List<BlockPos> layer, int size, RandomSource random) {
        List<BlockPos> thinned = new ArrayList<>(layer);
        while (thinned.size() > size) {
            thinned.remove(random.nextInt(thinned.size()));
        }
        return thinned;
    }

    private boolean canStandOn(WorldGenLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) && this.canReplace(level, pos);
    }

    private boolean canReplace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }
}
