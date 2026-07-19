package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.model.MastacembelusErythrotaeniaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MastacembelusErythrotaeniaRenderer extends GeoEntityRenderer<MastacembelusEntity> {
    private float babyMult = 0.3f;

    public MastacembelusErythrotaeniaRenderer(EntityRendererProvider.Context context) {
        super(context, new MastacembelusErythrotaeniaModel());
    }

    public float scaler() {
        return 0.37f;
    }

    @Override
    public void render(MastacembelusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1) return;
        if(entity.tickCount <= 1) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MastacembelusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : this.scaler();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
