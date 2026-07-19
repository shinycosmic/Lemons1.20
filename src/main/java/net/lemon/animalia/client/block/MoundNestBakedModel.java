package net.lemon.animalia.client.block;

import net.lemon.animalia.block.MoundNestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoundNestBakedModel extends BakedModelWrapper<BakedModel> {

    public static final ModelProperty<BlockState> GROUND_STATE = new ModelProperty<>();

    public MoundNestBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        BlockState below = level.getBlockState(pos.below());
        return ModelData.builder()
                .with(GROUND_STATE, below)
                .build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> originalQuads = super.getQuads(state, side, rand, extraData, renderType);

        BlockState groundState = extraData.get(GROUND_STATE);
        if (groundState == null) {
            return originalQuads;
        }

        BakedModel groundModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(groundState);
        TextureAtlasSprite groundSprite = groundModel.getParticleIcon(ModelData.EMPTY);

        List<BakedQuad> retexturedQuads = new ArrayList<>();
        for (BakedQuad quad : originalQuads) {
            retexturedQuads.add(retexture(quad, groundSprite));
        }
        return retexturedQuads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState groundState = data.get(GROUND_STATE);
        if (groundState != null) {
            BakedModel groundModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(groundState);
            return groundModel.getParticleIcon(ModelData.EMPTY);
        }
        return super.getParticleIcon(data);
    }

    private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite newSprite) {
        int[] vertexData = quad.getVertices().clone();
        TextureAtlasSprite oldSprite = quad.getSprite();

        for (int i = 0; i < 4; i++) {
            int offset = i * 8;
            float u = Float.intBitsToFloat(vertexData[offset + 4]);
            float v = Float.intBitsToFloat(vertexData[offset + 5]);

            // Convert from old sprite UV space back to 0-1, then to new sprite UV space
            float normalizedU = (u - oldSprite.getU0()) / (oldSprite.getU1() - oldSprite.getU0());
            float normalizedV = (v - oldSprite.getV0()) / (oldSprite.getV1() - oldSprite.getV0());

            float newU = newSprite.getU0() + normalizedU * (newSprite.getU1() - newSprite.getU0());
            float newV = newSprite.getV0() + normalizedV * (newSprite.getV1() - newSprite.getV0());

            vertexData[offset + 4] = Float.floatToRawIntBits(newU);
            vertexData[offset + 5] = Float.floatToRawIntBits(newV);
        }

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), newSprite, quad.isShade());
    }
}