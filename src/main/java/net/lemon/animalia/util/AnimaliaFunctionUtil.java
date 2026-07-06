package net.lemon.animalia.util;

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
}
