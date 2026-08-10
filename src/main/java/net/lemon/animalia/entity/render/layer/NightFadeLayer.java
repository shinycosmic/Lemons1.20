package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lemon.animalia.util.NightBlend;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class NightFadeLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    private final ResourceLocation nightTexture;

    public NightFadeLayer(GeoRenderer<T> renderer, ResourceLocation nightTexture) {
        super(renderer);
        this.nightTexture = nightTexture;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isBaby()) {
            return;
        }
        float blend = NightBlend.blend(animatable);
        if (blend <= 0.0F || blend >= 1.0F) {
            return;
        }
        RenderType overlayType = RenderType.entityTranslucent(this.nightTexture);
        VertexConsumer vc = bufferSource.getBuffer(overlayType);
        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, overlayType, vc, partialTick, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, blend);
    }
}