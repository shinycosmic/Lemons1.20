package net.lemon.animalia.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lemon.animalia.entity.projectiles.WaterSpitProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WaterSpitRenderer extends EntityRenderer<WaterSpitProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/particle/bubble_pop_0.png");

    public WaterSpitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WaterSpitProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // Billboard — always face the camera
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        float halfSize = 0.125F; // small quad, particle-sized
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        vertex(consumer, matrix, normal, packedLight, -halfSize, -halfSize, 0, 1);
        vertex(consumer, matrix, normal, packedLight, halfSize, -halfSize, 1, 1);
        vertex(consumer, matrix, normal, packedLight, halfSize, halfSize, 1, 0);
        vertex(consumer, matrix, normal, packedLight, -halfSize, halfSize, 0, 0);

        poseStack.popPose();
        if(entity.tickCount <= 1) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, int packedLight, float x, float y, float u, float v) {
        consumer.vertex(matrix, x, y, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(WaterSpitProjectile entity) {
        return TEXTURE;
    }
}