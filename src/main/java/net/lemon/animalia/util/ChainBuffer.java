package net.lemon.animalia.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Stores a ring buffer of yaw deltas, then applies delayed rotation
 * to successive bones in a chain — creating a "trailing" effect.
 */
public class ChainBuffer {
    private final float[] yawHistory;
    private int index = 0;
    private float prevYaw = 0;

    /**
     * @param bufferLength how many ticks of history to store.
     *                     Higher = smoother/more delay between segments.
     */
    public ChainBuffer(int bufferLength) {
        this.yawHistory = new float[bufferLength];
    }

    /**
     * Call once per tick (client-side only) to record the entity's yaw delta.
     */
    public void tick(LivingEntity entity) {
        float currentYaw = entity.yBodyRot;
        float delta = Mth.degreesDifference(prevYaw, currentYaw);
        prevYaw = currentYaw;
        yawHistory[index] = delta;
        index = (index + 1) % yawHistory.length;
    }

    /**
     * Gets the accumulated yaw offset for a given bone in the chain.
     *
     * @param boneIndex     which bone in the chain (0 = first tail segment, 1 = second, etc.)
     * @param boneCount     total bones in the chain
     * @param maxAngle      clamp: max degrees any single bone can rotate
     * @param intensity     multiplier for the effect strength (1.0 = normal)
     * @return rotation in radians to apply to this bone's Y axis
     */
    public float getRotationForBone(int boneIndex, int boneCount, float maxAngle, float intensity) {
        int samplesPerBone = Math.max(1, yawHistory.length / boneCount);
        int lookbackStart = (boneIndex + 1) * samplesPerBone;

        float accumulated = 0;
        for (int i = 0; i < samplesPerBone; i++) {
            int histIdx = (index - lookbackStart - i + yawHistory.length * 2) % yawHistory.length;
            accumulated += yawHistory[histIdx];
        }

        accumulated *= intensity / samplesPerBone;
        accumulated = Mth.clamp(accumulated, -maxAngle, maxAngle);
        return accumulated * ((float) Math.PI / 180F); // degrees to radians
    }
}