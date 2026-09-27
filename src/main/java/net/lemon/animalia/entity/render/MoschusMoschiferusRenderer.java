package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.HyemoschusEntity;
import net.lemon.animalia.entity.custom.MoschusEntity;
import net.lemon.animalia.entity.model.field.HyemoschusAquaticusModel;
import net.lemon.animalia.entity.model.field.MoschusMoschiferusModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoschusMoschiferusRenderer extends GeoEntityRenderer<MoschusEntity> {
    private float babyMult = 0.52f;

    public MoschusMoschiferusRenderer(EntityRendererProvider.Context context) {
        super(context, new MoschusMoschiferusModel());
    }

    @Override
    public void render(MoschusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MoschusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
