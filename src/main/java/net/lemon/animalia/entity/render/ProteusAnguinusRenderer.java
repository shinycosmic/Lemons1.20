package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.PangolinEntity;
import net.lemon.animalia.entity.custom.ProteusEntity;
import net.lemon.animalia.entity.model.field.ProteusAnguinusModel;
import net.lemon.animalia.entity.model.field.SmutsiaGiganteaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ProteusAnguinusRenderer extends GeoEntityRenderer<ProteusEntity> {
    private float babyMult = 0.06f;

    public ProteusAnguinusRenderer(EntityRendererProvider.Context context) {
        super(context, new ProteusAnguinusModel());
    }

    @Override
    public void render(ProteusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, ProteusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
