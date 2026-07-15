package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.custom.RoosterfishEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.lemon.animalia.entity.model.ChileanSeaBassModel;
import net.lemon.animalia.entity.model.NematistiusPectoralisModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NematistiusPectoralisRenderer extends GeoEntityRenderer<RoosterfishEntity> {
    private float babyMult = 0.3f;

    public NematistiusPectoralisRenderer(EntityRendererProvider.Context context) {
        super(context, new NematistiusPectoralisModel());
    }

    @Override
    public void render(RoosterfishEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, RoosterfishEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    protected void applyRotations(RoosterfishEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (!animatable.isInWater()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(0.0F, -animatable.getBbWidth() * 0.5F, 0.0F);
        }
    }
}
