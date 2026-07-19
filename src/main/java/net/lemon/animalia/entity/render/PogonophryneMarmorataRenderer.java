package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.lemon.animalia.entity.custom.PogonophryneEntity;
import net.lemon.animalia.entity.model.PogonophryneMarmorataModel;
import net.lemon.animalia.entity.model.PseudaphritisUrvilliiModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PogonophryneMarmorataRenderer extends GeoEntityRenderer<PogonophryneEntity> {
    private float babyMult = 0.3f;

    public PogonophryneMarmorataRenderer(EntityRendererProvider.Context context) {
        super(context, new PogonophryneMarmorataModel());
    }

    @Override
    public void render(PogonophryneEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, PogonophryneEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    protected void applyRotations(PogonophryneEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (!animatable.isInWater()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(0.0F, -animatable.getBbWidth() * 0.5F, 0.0F);
        }
    }
}
