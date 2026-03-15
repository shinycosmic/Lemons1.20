package net.lemon.animalia.entity.aimove;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DartMoveController extends MoveControl {
    private final Mob mob;
    private final boolean isBottomWalker;
    public DartMoveController(Mob mob, boolean flag) {
        super(mob);
        this.mob = mob;
        this.isBottomWalker = flag;
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO) {
            // drag / friction
            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.6));
            return;
        }

        double dx = wantedX - mob.getX();
        double dy = wantedY - mob.getY();
        double dz = wantedZ - mob.getZ();

        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);

        if (dist < 0.25) {
            operation = Operation.WAIT;
            return;
        }

        Vec3 dir = new Vec3(dx, dy, dz).normalize();
        double spurtStrength = 0.45;

        Vec3 proposedMove = dir.scale(spurtStrength);
        BlockPos nextPos = new BlockPos(
                Mth.floor(mob.getX() + proposedMove.x),
                Mth.floor(mob.getY() + proposedMove.y),
                Mth.floor(mob.getZ() + proposedMove.z)
        );

        BlockState blockAtNext = mob.level().getBlockState(nextPos);

        if (isBottomWalker) {
            // Check if the entity can step up 1 block
            float stepHeight = mob.getStepHeight(); // entity-defined step height
            BlockPos aboveNext = nextPos.above();
            BlockState blockAboveNext = mob.level().getBlockState(aboveNext);

            // If the space above is air, allow stepping up
            if (stepHeight >= 1.0F && blockAboveNext.isAir()) {
                mob.setDeltaMovement(mob.getDeltaMovement().add(proposedMove.x, 1.0, proposedMove.z));
            } else if (!blockAtNext.getCollisionShape(mob.level(), nextPos).isEmpty()) {
                // Hit an unwalkable wall, stop movement
                operation = Operation.WAIT;
                mob.setDeltaMovement(Vec3.ZERO);
            } else {
                mob.setDeltaMovement(mob.getDeltaMovement().add(proposedMove));
            }
        } else {
            // Free swimmer: cancel movement if a block is in the way
            if (!blockAtNext.getCollisionShape(mob.level(), nextPos).isEmpty()) {
                operation = Operation.WAIT;
                return;
            } else {
                mob.setDeltaMovement(mob.getDeltaMovement().add(proposedMove));
            }
        }

        // --- Rotation ---
        float yaw = (float) (Mth.atan2(dz, dx) * 180F / Math.PI) - 90F;
        mob.setYRot(rotlerp(mob.getYRot(), yaw, 20));
        mob.yBodyRot = mob.getYRot();
    }

}
