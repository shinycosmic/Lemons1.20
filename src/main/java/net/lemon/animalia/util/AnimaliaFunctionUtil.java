package net.lemon.animalia.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class AnimaliaFunctionUtil {
    private static final float CM_PER_PIXEL = 6.25f; // 1 block = 16px = 100cm

    /**
     * Calculates the render scale multiplier for a mob given its model's pixel length
     * and the desired real-world size in centimeters.
     *
     * @param modelPixelLength the model's length along its longest axis, in pixels (from Blockbench)
     * @param desiredCm the desired real-world length in centimeters
     * @return the uniform scale multiplier to use in the renderer
     */
    public static float getScaleForSize(float modelPixelLength, float desiredCm) {
        return desiredCm / (modelPixelLength * CM_PER_PIXEL);
    }

    public static void renderEntityFlat(GuiGraphics graphics, int x, int y, int scale, Quaternionf rotation, LivingEntity entity) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 50.0);
        graphics.pose().mulPoseMatrix(new Matrix4f().scaling(scale, scale, -scale));
        graphics.pose().mulPose(rotation);

        Lighting.setupForFlatItems();

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        dispatcher.overrideCameraOrientation(new Quaternionf());

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            dispatcher.render(entity, 0, 0, 0, 0.0F, 1.0F, graphics.pose(), bufferSource, 15728880);
        });
        bufferSource.endBatch();

        dispatcher.setRenderShadow(true);
        graphics.pose().popPose();
        Lighting.setupFor3DItems();
    }
}
