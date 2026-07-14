package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.SynbranchusEntity;
import net.lemon.animalia.entity.model.ChaudhuriaCaudataModel;
import net.lemon.animalia.entity.model.SynbranchusMarmoratusModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChaudhuriaCaudataRenderer extends GeoEntityRenderer<SynbranchusEntity> {
    private float babyMult = 0.1f;

    public ChaudhuriaCaudataRenderer(EntityRendererProvider.Context context) {
        super(context, new ChaudhuriaCaudataModel());
    }

    @Override
    public void render(SynbranchusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, SynbranchusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    protected void applyRotations(SynbranchusEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (!animatable.isInWater() && animatable.onGround()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(0.0F, -animatable.getBbWidth() * 0.5F, 0.0F);
        }
    }
}
