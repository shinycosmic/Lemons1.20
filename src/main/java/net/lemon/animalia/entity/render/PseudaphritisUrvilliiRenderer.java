package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.lemon.animalia.entity.model.fish.PseudaphritisUrvilliiModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PseudaphritisUrvilliiRenderer extends GeoEntityRenderer<CongolliEntity> {
    private float babyMult = 0.3f;

    public PseudaphritisUrvilliiRenderer(EntityRendererProvider.Context context) {
        super(context, new PseudaphritisUrvilliiModel());
    }

    public float scaler() {
        return 0.473f;
    }

    @Override
    public void render(CongolliEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, CongolliEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : this.scaler();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
