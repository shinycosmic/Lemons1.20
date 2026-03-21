package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.lemon.animalia.entity.model.EleginopsMaclovinusModel;
import net.lemon.animalia.entity.model.PseudaphritisUrvilliiModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PseudaphritisUrvilliiRenderer extends GeoEntityRenderer<CongolliEntity> {
    private float babyMult = 0.3f;

    public PseudaphritisUrvilliiRenderer(EntityRendererProvider.Context context) {
        super(context, new PseudaphritisUrvilliiModel());
    }

    public float scaler(float varSize) {
        return 0.473f;
    }

    @Override
    public void render(CongolliEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(babyMult, babyMult, babyMult);
        } else {
            poseStack.scale(scaler(entity.getVarSizeMultiplier()), scaler( entity.getVarSizeMultiplier()), scaler(entity.getVarSizeMultiplier()));
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(CongolliEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }
}
