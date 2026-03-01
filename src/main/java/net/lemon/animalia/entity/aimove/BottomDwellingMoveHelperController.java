package net.lemon.animalia.entity.aimove;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class BottomDwellingMoveHelperController extends MoveControl {
    private final Mob fish;

    public BottomDwellingMoveHelperController(Mob mob) {
        super(mob);
        this.fish = mob;
    }

    @Override
    public void tick() {
        if(this.operation == Operation.MOVE_TO && !this.fish.getNavigation().isDone()) {
            if(!fish.isBaby() && (this.wantedY - this.fish.getY()) > 0) {
                this.wantedY -= 0.6 * (this.wantedY - this.fish.getY());
            }

            float f = (float)(this.speedModifier * this.fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.fish.setSpeed(Mth.lerp(0.125F, this.fish.getSpeed(), f));

            double dx = this.wantedX - this.fish.getX();
            double dy = this.wantedY - this.fish.getY();
            double dz = this.wantedZ - this.fish.getZ();

            double horizontalDist = Math.sqrt(dx*dx + dz*dz);
            double totalDist = Math.sqrt(dx*dx + dy*dy + dz*dz);

            Vec3 currentVelocity = fish.getDeltaMovement();

            double desiredX = 0, desiredZ = 0;
            if (horizontalDist > 1.0E-6) {
                double normX = dx / horizontalDist;
                double normZ = dz / horizontalDist;
                desiredX = normX * fish.getSpeed() * 0.1;
                desiredZ = normZ * fish.getSpeed() * 0.1;
            }

            // Lerp horizontal velocity for smooth acceleration
            double lerpFactor = 0.1; // smaller = smoother
            double finalX = Mth.lerp(lerpFactor, currentVelocity.x, desiredX);
            double finalZ = Mth.lerp(lerpFactor, currentVelocity.z, desiredZ);
            double finalY = currentVelocity.y;

            if ( totalDist > 0.0) {
                double swimSpeed = fish.getSpeed() * 0.06; // scale factor similar to horizontal
                finalY = Mth.clamp(dy, -swimSpeed, swimSpeed);
            }

            fish.setDeltaMovement(new Vec3(finalX, finalY, finalZ));



            /***
             * Handle Rotation speed
             * Lower Maximum change to make rotation speed slower
             */
            if (dx != 0.0D || dz != 0.0D) {
                float f1 = (float)(Mth.atan2(dz, dx) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.fish.setYRot(this.rotlerp(this.fish.getYRot(), f1, 15.0F));
                this.fish.yBodyRot = this.fish.getYRot();
            }
        } else {
            this.fish.setSpeed(0.0F);
        }
    }

}
