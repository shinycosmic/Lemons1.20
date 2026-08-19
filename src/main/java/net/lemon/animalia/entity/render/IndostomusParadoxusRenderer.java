package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.IndostomusEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.lemon.animalia.entity.model.fish.IndostomusParadoxusModel;
import net.lemon.animalia.entity.model.fish.ScatophagusArgusModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IndostomusParadoxusRenderer extends GeoEntityRenderer<IndostomusEntity> {
    private float babyMult = 0.1f;

    public IndostomusParadoxusRenderer(EntityRendererProvider.Context context) {
        super(context, new IndostomusParadoxusModel());
    }

    @Override
    public void render(IndostomusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, IndostomusEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
