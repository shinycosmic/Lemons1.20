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
     * @param modelPixelLength the model's length along its longest axis, in pixels
     * @param desiredCm the desired real length in centimeters
     * @return the scale multiplier to use in the renderer
     */
    public static float getScaleForSize(float modelPixelLength, float desiredCm) {
        return desiredCm / (modelPixelLength * CM_PER_PIXEL);
    }

}
