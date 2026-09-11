package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface ICanClimb {

    Direction[] CLIMB_FACES = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};

    static boolean onSolid(Mob mob) {
        return mob.onGround() || (mob instanceof ICanClimb climber && climber.isAttached());
    }

    default boolean babyClimbEnabled() { return true; }

    default boolean climbEnabled() {
        return this.canClimb() && (this.babyClimbEnabled() || !((Mob) this).isBaby());
    }

    byte getClimbData();

    void setClimbData(byte data);

    boolean wantsToClimb();

    void setWantsToClimb(boolean wantsToClimb);

    default boolean canClimb() {
        return false;
    }

    default boolean canClimbUp() {
        return true;
    }

    default boolean canClimbDown() {
        return true;
    }

    default boolean canClimbCeilings() {
        return false;
    }

    default float climbUpMalus() {
        return 0.0F;
    }

    default float climbDownMalus() {
        return 0.0F;
    }

    default boolean isClimbableBlock(BlockState state) {
        return true;
    }

    default double getClimbSpeed() {
        return 1.0D;
    }

    /**
     * basically, this method controls if during pathfinding, the mob should treat walls as valid passes
     */
    default boolean canPathfindWithWalls() {
        return this.climbEnabled() && (this.wantsToClimb() || this.isAttached());
    }

    default Direction getClimbFace() {
        return Direction.from3DDataValue(this.getClimbData() & 7);
    }

    default void setClimbFace(Direction face) {
        this.setClimbData((byte) ((this.getClimbData() & ~7) | face.get3DDataValue()));
    }

    default boolean isAttached() {
        return this.getClimbFace() != Direction.DOWN;
    }

    default void detach() {
        this.setClimbFace(Direction.DOWN);
        ((Mob) this).setYya(0.0F);
    }

    default boolean faceAllowed(Direction face) {
        return face != Direction.UP || this.canClimbCeilings();
    }

    default boolean canAttachTo(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.getCollisionShape(level, pos).isEmpty() && this.isClimbableBlock(state);
    }

    default boolean hasClimbableFace(BlockGetter level, BlockPos pos) {
        for (Direction face : CLIMB_FACES) {
            if (this.faceAllowed(face) && this.canAttachTo(level, pos.relative(face))) {
                return true;
            }
        }
        return false;
    }

    default boolean isValidAttachment(Direction face) {
        PathfinderMob mob = (PathfinderMob) this;
        BlockPos support = mob.blockPosition().relative(face);
        return mob.level().isLoaded(support) && this.canAttachTo(mob.level(), support);
    }

    default boolean attach() {
        if (!this.climbEnabled()) {
            return false;
        }
        this.selectAttachmentFace();
        return this.isAttached();
    }

    default void selectAttachmentFace() {
        PathfinderMob mob = (PathfinderMob) this;
        Direction current = this.getClimbFace();
        Direction best = null;
        double bestDist = Double.MAX_VALUE;
        if (current != Direction.DOWN && this.faceAllowed(current) && this.isValidAttachment(current)) {
            best = current;
            bestDist = this.faceDistSqr(mob, current);
        }
        for (Direction face : CLIMB_FACES) {
            if (face == current || !this.faceAllowed(face) || !this.isValidAttachment(face)) {
                continue;
            }
            double dist = this.faceDistSqr(mob, face);
            if (dist < bestDist) {
                best = face;
                bestDist = dist;
            }
        }
        if (best == null) {
            this.detach();
            return;
        }
        this.setClimbFace(best);
    }

    default double faceDistSqr(PathfinderMob mob, Direction face) {
        BlockPos support = mob.blockPosition().relative(face);
        return mob.distanceToSqr(support.getX() + 0.5D, support.getY() + 0.5D, support.getZ() + 0.5D);
    }

    default void tickClimb() {
        if (!this.isAttached()) {
            return;
        }
        PathfinderMob mob = (PathfinderMob) this;
        if (!this.climbEnabled() || mob.isPassenger() || mob.isLeashed()
                || (!this.wantsToClimb() && (mob.onGround() || !mob.getNavigation().isDone()))) {
            this.detach();
            return;
        }
        if (!this.faceAllowed(this.getClimbFace()) || !this.isValidAttachment(this.getClimbFace())) {
            this.selectAttachmentFace();
        }
    }

    default void climbTravel(Vec3 travelVector) {
        PathfinderMob mob = (PathfinderMob) this;
        Direction face = this.getClimbFace();
        Vec3 drive = travelVector.multiply(1 - Math.abs(face.getStepX()), 1 - Math.abs(face.getStepY()), 1 - Math.abs(face.getStepZ()));
        mob.addDeltaMovement(drive.scale(this.getClimbSpeed() * 0.005D));
        if (!mob.horizontalCollision && !mob.verticalCollision) {
            mob.addDeltaMovement(new Vec3(face.getStepX() * 0.01D, face.getStepY() * 0.01D, face.getStepZ() * 0.01D));
        }
        mob.move(MoverType.SELF, mob.getDeltaMovement());
        mob.setDeltaMovement(mob.getDeltaMovement().scale(0.9D));
        mob.resetFallDistance();
    }
}