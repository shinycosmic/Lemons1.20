package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.HyemoschusEntity;
import net.lemon.animalia.entity.custom.PangolinEntity;
import net.lemon.animalia.entity.model.field.HyemoschusAquaticusModel;
import net.lemon.animalia.entity.model.field.SmutsiaGiganteaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HyemoschusAquaticusRenderer extends GeoEntityRenderer<HyemoschusEntity> {
    private float babyMult = 0.2f;

    public HyemoschusAquaticusRenderer(EntityRendererProvider.Context context) {
        super(context, new HyemoschusAquaticusModel());
    }

    @Override
    public void render(HyemoschusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, HyemoschusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
