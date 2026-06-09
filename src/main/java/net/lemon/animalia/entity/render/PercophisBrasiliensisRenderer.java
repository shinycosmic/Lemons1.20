package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.lemon.animalia.entity.model.ChileanSeaBassModel;
import net.lemon.animalia.entity.model.PercophisBrasiliensisModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PercophisBrasiliensisRenderer extends GeoEntityRenderer<ToothfishEntity> {
    private float babyMult = 0.3f;

    public PercophisBrasiliensisRenderer(EntityRendererProvider.Context context) {
        super(context, new PercophisBrasiliensisModel());
    }

    public float scaler(float varSize) {
        return 0.47f * varSize;
    }

    @Override
    public void render(ToothfishEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(babyMult, babyMult, babyMult);
        } else {
            poseStack.scale(scaler(entity.getVarSizeMultiplier()), scaler( entity.getVarSizeMultiplier()), scaler(entity.getVarSizeMultiplier()));
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
