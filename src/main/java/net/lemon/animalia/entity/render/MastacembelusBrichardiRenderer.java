package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.model.MastacembelusArmatusModel;
import net.lemon.animalia.entity.model.MastacembelusBrichardiModel;
import net.lemon.animalia.entity.render.layer.MastacembelusBrichardiFinLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MastacembelusBrichardiRenderer extends GeoEntityRenderer<MastacembelusEntity> {
    private float babyMult = 0.3f;

    public MastacembelusBrichardiRenderer(EntityRendererProvider.Context context) {
        super(context, new MastacembelusBrichardiModel());
        this.addRenderLayer(new MastacembelusBrichardiFinLayer(this));
    }

    public float scaler() {
        return 0.2f;
    }

    @Override
    public void render(MastacembelusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MastacembelusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : this.scaler();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
