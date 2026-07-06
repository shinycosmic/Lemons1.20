package net.lemon.animalia.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Records entity body yaw over time. Each tail bone samples
 * "where was the entity facing N ticks ago" and applies the difference
 * as a trailing rotation.
 */
public class ChainBuffer {
    private final float[] yawHistory;
    private final int length;
    private int head = 0;
    private boolean filled = false;

    public ChainBuffer(int bufferLength) {
        this.length = bufferLength;
        this.yawHistory = new float[bufferLength];
    }

    public void tick(LivingEntity entity) {
        yawHistory[head] = entity.yBodyRot;
        head = (head + 1) % length;
        if (!filled && head == 0) filled = true;
    }

    /**
     * Returns the yaw offset (in radians) for a bone in the chain.
     *
     * @param boneIndex  0-based index in the tail chain
     * @param boneCount  total number of tail bones
     * @param maxAngle   max degrees of rotation per bone
     * @param delay      ticks of delay between each bone (e.g. 3 = each bone is 3 ticks behind the previous)
     */
    public float getChainRotation(int boneIndex, int boneCount, float maxAngle, int delay, LivingEntity entity) {
        if (!filled && head < delay * (boneIndex + 1)) return 0f;

        // How far back in history this bone should look
        int lookback = delay * (boneIndex + 1);
        int histIndex = ((head - 1 - lookback) % length + length) % length;

        // Difference between where the entity is NOW and where it WAS
        float currentYaw = entity.yBodyRot;
        float pastYaw = yawHistory[histIndex];
        float diff = Mth.degreesDifference(currentYaw, pastYaw);

        // Clamp and convert to radians
        diff = Mth.clamp(diff, -maxAngle, maxAngle);
        return diff * ((float) Math.PI / 180F);
    }
}