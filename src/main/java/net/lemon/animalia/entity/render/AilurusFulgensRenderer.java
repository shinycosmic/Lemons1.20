package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.AilurusFulgensEntity;
import net.lemon.animalia.entity.model.field.AilurusFulgensModel;
import net.lemon.animalia.entity.render.layer.HeldItemGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AilurusFulgensRenderer extends GeoEntityRenderer<AilurusFulgensEntity> {
    private float babyMult = 0.2f;

    public AilurusFulgensRenderer(EntityRendererProvider.Context context) {
        super(context, new AilurusFulgensModel());
        addRenderLayer(new HeldItemGeoLayer<>(this, "heldItem", EquipmentSlot.MAINHAND, ItemDisplayContext.GROUND,
                entity -> entity.isEating() && !entity.isBaby() && HeldItemGeoLayer.isPlaying(entity, "eat_controller", "eating"), -90.0F));
    }

    @Override
    public void render(AilurusFulgensEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, AilurusFulgensEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
