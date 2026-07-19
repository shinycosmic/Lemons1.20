package net.lemon.animalia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MoundNestBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty GROUND_TYPE = IntegerProperty.create("ground_type", 0, 2);

    public static final int GROUND_SAND = 0;
    public static final int GROUND_DIRT = 1;
    public static final int GROUND_GRAVEL = 2;

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 3, 14);

    public MoundNestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, true)
                .setValue(GROUND_TYPE, GROUND_SAND));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, GROUND_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (direction == Direction.DOWN) {
            return state.setValue(GROUND_TYPE, getGroundTypeFor(neighborState));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockState below = level.getBlockState(pos.below());
        level.setBlock(pos, state.setValue(GROUND_TYPE, getGroundTypeFor(below)), 2);
    }

    public static int getGroundTypeFor(BlockState below) {
        if (below.is(Blocks.GRAVEL)) {
            return GROUND_GRAVEL;
        } else if (below.is(Blocks.DIRT) || below.is(Blocks.COARSE_DIRT) || below.is(Blocks.ROOTED_DIRT)) {
            return GROUND_DIRT;
        }
        return GROUND_SAND;
    }

    public static BlockState createForPosition(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        int groundType = getGroundTypeFor(below);
        boolean waterlogged = level.getFluidState(pos).is(Fluids.WATER);

        return net.lemon.animalia.registry.ModBlocks.MOUND_NEST.get().defaultBlockState()
                .setValue(GROUND_TYPE, groundType)
                .setValue(WATERLOGGED, waterlogged);
    }
}