package net.lemon.animalia.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

public class WaterStaticBlock extends BaseCoralFanBlock {

    public WaterStaticBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!context.getLevel().getFluidState(context.getClickedPos()).isSourceOfType(Fluids.WATER)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }
}