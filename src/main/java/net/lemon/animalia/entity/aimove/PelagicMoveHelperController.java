package net.lemon.animalia.entity.aimove;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class PelagicMoveHelperController extends MoveControl {
    private final Mob fish;
    public PelagicMoveHelperController(Mob mob) {
        super(mob);
        this.fish = mob;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO && !fish.getNavigation().isDone()) {

            Vec3 dir = new Vec3(
                    this.wantedX - fish.getX(),
                    this.wantedY - fish.getY(),
                    this.wantedZ - fish.getZ()
            );

            double distance = dir.length();
            if (distance < 0.1) {
                fish.setSpeed(0);
                return;
            }

            dir = dir.normalize();

            float speed = (float)(this.speedModifier *
                    fish.getAttributeValue(Attributes.MOVEMENT_SPEED));

            fish.setSpeed(Mth.lerp(0.1F, fish.getSpeed(), speed));

            Vec3 current = fish.getDeltaMovement();
            Vec3 desired = dir.scale(fish.getSpeed() * 0.1);

            fish.setDeltaMovement(current.lerp(desired, 0.08));

            float yaw = (float)(Mth.atan2(dir.z, dir.x) * (180F / Math.PI)) - 90F;
            fish.setYRot(this.rotlerp(fish.getYRot(), yaw, 10F));
            fish.yBodyRot = fish.getYRot();
        } else {
            fish.setSpeed(0.0F);
        }
    }
}
