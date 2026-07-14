package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.lemon.animalia.entity.model.BettaSplendensModel;
import net.lemon.animalia.entity.render.layer.BettaSplendensColorLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BettaSplendensRenderer extends GeoEntityRenderer<BettaEntity> {
    private float babyMult = 0.3f;
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/entity/betta/betta_base.png");

    public BettaSplendensRenderer(EntityRendererProvider.Context context) {
        super(context, new BettaSplendensModel());
        this.addRenderLayer(new BettaSplendensColorLayer(this));
    }

    public float scaler() {
        return 0.35f;
    }

    @Override
    public void render(BettaEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, BettaEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : this.scaler();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public ResourceLocation getTextureLocation(BettaEntity animatable) {
        if (animatable.isSpecialVariant()) {
            String specialName = animatable.getTraitsClient().specialTexture;
            return new ResourceLocation(Animalia.MODID,"textures/entity/betta/betta_" + specialName + ".png");
        }
        return BASE_TEXTURE;
    }

    @Override
    protected void applyRotations(BettaEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (!animatable.isInWater() && animatable.onGround()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(0.0F, -animatable.getBbWidth() * 0.5F, 0.0F);
        }
    }
}
