package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.AenigmachannaEntity;
import net.lemon.animalia.entity.custom.SeahorseEntity;
import net.lemon.animalia.entity.model.fish.AenigmachannaModel;
import net.lemon.animalia.entity.model.fish.HippocampusIngensModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HippocampusIngensRenderer extends GeoEntityRenderer<SeahorseEntity> {
    private float babyMult = 0.3f;

    public HippocampusIngensRenderer(EntityRendererProvider.Context context) {
        super(context, new HippocampusIngensModel());
    }

    @Override
    public void render(SeahorseEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(SeahorseEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, SeahorseEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
