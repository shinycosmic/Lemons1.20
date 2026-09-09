package net.lemon.animalia.entity.aimove;

import net.lemon.animalia.entity.bases.helpers.ICanClimb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClimberMoveControl extends MoveControl {

    private final ICanClimb climber;

    public ClimberMoveControl(Mob mob) {
        super(mob);
        this.climber = (ICanClimb) mob;
    }

    @Override
    public void tick() {
        if (this.climber.isAttached()) {
            this.tickAttached();
            return;
        }
        boolean wantsClimbNode = this.operation == Operation.MOVE_TO && this.climber.wantsToClimb()
                && this.wantedY - this.mob.getY() > 0.5D && this.isClimbNode();
        super.tick();
        if (wantsClimbNode && this.climber.attach()) {
            this.operation = Operation.WAIT;
            this.drive(Vec3.ZERO);
        }
    }

    private boolean isClimbNode() {
        Level level = this.mob.level();
        BlockPos pos = BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ);
        BlockPos below = pos.below();
        return level.getBlockState(below).getCollisionShape(level, below).isEmpty()
                && this.climber.hasClimbableFace(level, pos);
    }

    private void tickAttached() {
        if (this.operation != Operation.MOVE_TO) {
            this.drive(Vec3.ZERO);
            return;
        }
        this.operation = Operation.WAIT;
        Vec3 wanted = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
        this.drive(wanted.lengthSqr() < 2.5000003E-7D ? Vec3.ZERO : wanted.normalize().scale(this.speedModifier));

        Direction face = this.climber.getClimbFace();
        if (face.getAxis().isHorizontal()) {
            float yaw = (float) (Mth.atan2(face.getStepZ(), face.getStepX()) * (180.0F / (float) Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, 10.0F));
        }
    }

    private void drive(Vec3 drive) {
        this.mob.setXxa((float) drive.x);
        this.mob.setYya((float) drive.y);
        this.mob.setZza((float) drive.z);
    }
}