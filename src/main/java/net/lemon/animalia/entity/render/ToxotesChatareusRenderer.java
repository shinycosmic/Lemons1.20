package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.custom.RoosterfishEntity;
import net.lemon.animalia.entity.custom.ToxotesEntity;
import net.lemon.animalia.entity.model.NematistiusPectoralisModel;
import net.lemon.animalia.entity.model.ToxotesChatareusModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToxotesChatareusRenderer extends GeoEntityRenderer<ToxotesEntity> {
    private float babyMult = 0.3f;

    public ToxotesChatareusRenderer(EntityRendererProvider.Context context) {
        super(context, new ToxotesChatareusModel());
    }

    @Override
    public void render(ToxotesEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, ToxotesEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
