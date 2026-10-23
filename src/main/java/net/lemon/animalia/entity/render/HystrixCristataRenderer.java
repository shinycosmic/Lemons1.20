package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.AilurusFulgensEntity;
import net.lemon.animalia.entity.custom.HystrixCristataEntity;
import net.lemon.animalia.entity.model.field.AilurusFulgensModel;
import net.lemon.animalia.entity.model.field.HystrixCristataModel;
import net.lemon.animalia.entity.render.layer.HeldItemGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HystrixCristataRenderer extends GeoEntityRenderer<HystrixCristataEntity> {
    private float babyMult = 0.2f;

    public HystrixCristataRenderer(EntityRendererProvider.Context context) {
        super(context, new HystrixCristataModel());
    }

    @Override
    public void render(HystrixCristataEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, HystrixCristataEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
