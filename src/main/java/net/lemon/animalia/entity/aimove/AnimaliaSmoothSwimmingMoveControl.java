package net.lemon.animalia.entity.aimove;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

public class AnimaliaSmoothSwimmingMoveControl extends SmoothSwimmingMoveControl {
    public AnimaliaSmoothSwimmingMoveControl(Mob mob, int maxTurnX, int maxTurnY, float inWaterSpeedModifier, float outsideWaterSpeedModifier, boolean applyGravity) {
        super(mob, maxTurnX, maxTurnY, inWaterSpeedModifier, outsideWaterSpeedModifier, applyGravity);
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            double dy = this.wantedY - this.mob.getY();
            if (Math.abs(dy) > 1.0E-5D && !this.mob.level().noCollision(this.mob, this.mob.getBoundingBox().move(0, Math.signum(dy) * 0.1D, 0))) {
                this.wantedY = this.mob.getY();
            }
        }
        super.tick();
    }
}