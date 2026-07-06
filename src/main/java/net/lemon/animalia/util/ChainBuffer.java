package net.lemon.animalia.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Tracks turning speed and propagates it down a bone chain with decay.
 * Works alongside SmoothSwimmingMoveControl without conflicting.
 */
public class ChainBuffer {
    private float prevYaw = Float.NaN;
    private float turnSpeed = 0f;      // current smoothed turn speed in degrees/tick
    private final float smoothing;      // how quickly turnSpeed responds (0-1, lower = smoother)

    public ChainBuffer(float smoothing) {
        this.smoothing = smoothing;
    }

    public void tick(LivingEntity entity) {
        float currentYaw = entity.yBodyRot;
        if (Float.isNaN(prevYaw)) {
            prevYaw = currentYaw;
            return;
        }
        float delta = Mth.degreesDifference(prevYaw, currentYaw);
        // Smooth the turn speed so it doesn't jitter
        turnSpeed = turnSpeed + (delta - turnSpeed) * smoothing;
        prevYaw = currentYaw;
    }

    /**
     * Returns rotation in radians for a bone in the chain.
     * Later bones get more rotation (trailing behind the head).
     *
     * @param boneIndex  0-based index (0 = closest to head)
     * @param boneCount  total tail bones
     * @param maxAngle   max degrees per bone
     * @param falloff    how much more each successive bone rotates (e.g. 1.0 = linear, 1.5 = accelerating)
     */
    public float getRotation(int boneIndex, int boneCount, float maxAngle, float falloff) {
        // Each bone gets progressively more of the turn speed
        float progress = (float)(boneIndex + 1) / boneCount;
        float scaled = turnSpeed * progress * falloff;
        scaled = Mth.clamp(scaled, -maxAngle, maxAngle);
        return scaled * ((float) Math.PI / 180F);
    }
}