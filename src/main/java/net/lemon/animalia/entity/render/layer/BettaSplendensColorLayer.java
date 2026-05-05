package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BettaSplendensColorLayer extends GeoRenderLayer<BettaEntity> {

    public BettaSplendensColorLayer(GeoRenderer<BettaEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, BettaEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        //Entity will auto-assign to true if trait combination found in dict
        if(animatable.isSpecialVariant()) {
            return;
        }



    }

    private void renderColorPass() {

    }
}
