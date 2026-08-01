package net.lemon.animalia.registry.spawning;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public enum SpawnBand {
    SHALLOW {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 8 && pos.getY() < seaLevel
                    && isWater(level, pos)
                    && openSkyAboveWater(level, pos);
        }
    },
    OPEN_WATER {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 32 && pos.getY() < seaLevel
                    && isWater(level, pos)
                    && openSkyAboveWater(level, pos);
        }
    },
    DEEP {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 128 && pos.getY() <= seaLevel - 32
                    && isWater(level, pos);
        }
    },
    CAVE_WATER {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return isWater(level, pos)
                    && !openSkyAboveWater(level, pos);
        }
    },
    FLOOR {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 28 && pos.getY() <= seaLevel
                    && isWater(level, pos)
                    && solidBelow(level, pos);
        }
    },
    FLOOR_DEEP {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 128 && pos.getY() < seaLevel - 28
                    && isWater(level, pos)
                    && solidBelow(level, pos);
        }
    },
    LOWLAND {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel && pos.getY() < seaLevel + 40
                    && isWater(level, pos)
                    && openSkyAboveWater(level, pos);
        }
    },
    HIGHLAND {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return pos.getY() >= level.getSeaLevel() + 40
                    && isWater(level, pos)
                    && openSkyAboveWater(level, pos);
        }
    },
    GROUND {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return isDryGround(level, pos);
        }
    },
    GROUND_CAVE {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return isDryGround(level, pos)
                    && !openSkyAbove(level, pos);
        }
    },
    GROUND_HIGHLAND {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return pos.getY() >= level.getSeaLevel() + 40
                    && isDryGround(level, pos)
                    && openSkyAbove(level, pos);
        }
    },
    GROUND_PEAKS {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return pos.getY() >= level.getSeaLevel() + 80
                    && isDryGround(level, pos)
                    && openSkyAbove(level, pos);
        }
    },
    ANY_WATER {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos) {
            return isWater(level, pos);
        }
    };

    public abstract boolean test(LevelAccessor level, BlockPos pos);

    private static boolean isWater(LevelAccessor level, BlockPos pos) {
        return level.isWaterAt(pos);
    }

    private static boolean isDryGround(LevelAccessor level, BlockPos pos) {
        return level.getFluidState(pos).isEmpty()
                && solidBelow(level, pos);
    }

    private static boolean solidBelow(LevelAccessor level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    private static boolean openSkyAbove(LevelAccessor level, BlockPos pos) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) <= pos.getY();
    }

    private static boolean openSkyAboveWater(LevelAccessor level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() < level.getMaxBuildHeight() && isWater(level, cursor)) {
            cursor.move(Direction.UP);
        }
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) <= cursor.getY();
    }
}