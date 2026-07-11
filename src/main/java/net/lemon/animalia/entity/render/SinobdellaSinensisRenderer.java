package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.model.MacrognathusSiamensisModel;
import net.lemon.animalia.entity.model.SinobdellaSinensisModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SinobdellaSinensisRenderer extends GeoEntityRenderer<MastacembelusEntity> {
    private float babyMult = 0.3f;

    public SinobdellaSinensisRenderer(EntityRendererProvider.Context context) {
        super(context, new SinobdellaSinensisModel());
    }

    public float scaler() {
        return 0.23f;
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
