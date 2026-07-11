package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class MastacembelusBrichardiFinLayer extends GeoRenderLayer<MastacembelusEntity> {
    private static final ResourceLocation FIN = new ResourceLocation(Animalia.MODID, "textures/entity/mastacembelusbrichardi_fins.png");

    public MastacembelusBrichardiFinLayer(GeoRenderer<MastacembelusEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MastacembelusEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType transparentType = RenderType.entityTranslucent(FIN);
        VertexConsumer vc = bufferSource.getBuffer(transparentType);

        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, transparentType, vc, partialTick, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 0.5f);
    }

}
