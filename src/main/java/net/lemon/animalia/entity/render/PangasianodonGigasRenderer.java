package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.PangasianodonEntity;
import net.lemon.animalia.entity.custom.RoosterfishEntity;
import net.lemon.animalia.entity.model.NematistiusPectoralisModel;
import net.lemon.animalia.entity.model.PangasianodonGigasModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PangasianodonGigasRenderer extends GeoEntityRenderer<PangasianodonEntity> {
    private float babyMult = 0.3f;

    public PangasianodonGigasRenderer(EntityRendererProvider.Context context) {
        super(context, new PangasianodonGigasModel());
    }

    @Override
    public void render(PangasianodonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.tickCount <= 1 && !entity.isRemoved()) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, PangasianodonEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.isBaby() ? babyMult : animatable.getVarSizeMultiplier();
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
