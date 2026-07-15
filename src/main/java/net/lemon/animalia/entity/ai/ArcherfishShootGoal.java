package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.custom.ToxotesEntity;
import net.lemon.animalia.entity.projectile.WaterSpitProjectile;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ArcherfishShootGoal extends Goal {
    private final ToxotesEntity mob;
    private final int minCooldown;
    private final int maxCooldown;

    private int cooldown;
    private State state = State.IDLE;
    private int stateTicks = 0;
    private BlockPos targetBlock = null;

    private static final int SURFACE_TIMEOUT = 60;
    private static final int SCAN_TIMEOUT = 40;
    private static final int AIM_DURATION = 20;
    private static final int SHOOT_DELAY = 5;
    private static final int SCAN_RANGE = 3;

    private enum State {
        IDLE, SURFACING, SCANNING, AIMING, SHOOTING
    }

    public ArcherfishShootGoal(ToxotesEntity mob, int minCooldown, int maxCooldown) {
        this.mob = mob;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.cooldown = minCooldown + mob.getRandom().nextInt(maxCooldown - minCooldown);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isInWater()) return false;
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isInWater() && this.state != State.IDLE;
    }

    @Override
    public void start() {
        this.state = State.SURFACING;
        this.stateTicks = 0;
        this.targetBlock = null;
    }

    @Override
    public void stop() {
        this.state = State.IDLE;
        this.stateTicks = 0;
        this.targetBlock = null;
        this.mob.setShooting(false);
        this.cooldown = this.minCooldown + this.mob.getRandom().nextInt(this.maxCooldown - this.minCooldown);
    }

    @Override
    public void tick() {
        this.stateTicks++;

        switch (this.state) {
            case SURFACING:
                tickSurfacing();
                break;
            case SCANNING:
                tickScanning();
                break;
            case AIMING:
                tickAiming();
                break;
            case SHOOTING:
                tickShooting();
                break;
        }
    }

    private void tickSurfacing() {
        if (this.stateTicks > SURFACE_TIMEOUT) {
            this.stop();
            return;
        }

        // Check if already near the surface
        BlockPos above = this.mob.blockPosition().above();
        if (!this.mob.level().getFluidState(above).is(FluidTags.WATER)) {
            this.state = State.SCANNING;
            this.stateTicks = 0;
            return;
        }

        int surfaceY = findSurfaceY();
        if (surfaceY > this.mob.blockPosition().getY()) {
            this.mob.getNavigation().moveTo(this.mob.getX(), surfaceY - 0.5, this.mob.getZ(), 1.0);
        }
    }

    private void tickScanning() {
        if (this.stateTicks > SCAN_TIMEOUT) {
            this.stop();
            return;
        }

        if (this.stateTicks % 5 != 0) return;

        this.targetBlock = findTarget();
        if (this.targetBlock != null) {
            this.state = State.AIMING;
            this.stateTicks = 0;
            this.mob.setShooting(true);
        }
    }

    private void tickAiming() {
        this.mob.getNavigation().stop();
        Vec3 targetCenter = Vec3.atCenterOf(this.targetBlock);
        this.mob.getLookControl().setLookAt(targetCenter.x, targetCenter.y, targetCenter.z);

        if (this.stateTicks >= AIM_DURATION) {
            this.state = State.SHOOTING;
            this.stateTicks = 0;
        }
    }

    private void tickShooting() {
        this.mob.getNavigation().stop();

        if (this.stateTicks >= SHOOT_DELAY) {
            shoot();
            this.mob.setShooting(false);
            this.stop();
        }
    }

    private void shoot() {
        Level level = this.mob.level();
        if (level.isClientSide) return;

        Vec3 eyePos = new Vec3(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        Vec3 targetCenter = Vec3.atCenterOf(this.targetBlock);
        Vec3 direction = targetCenter.subtract(eyePos).normalize();

        WaterSpitProjectile spit = new WaterSpitProjectile(ModEntities.WATER_SPIT.get(), level);
        spit.setOwner(this.mob);
        spit.setPos(eyePos.x, eyePos.y, eyePos.z);
        spit.setDeltaMovement(direction.scale(1.2));
        level.addFreshEntity(spit);
    }

    /***
     * Finds a random valid foliage target block within range that is above the water surface.
     * @return A valid target BlockPos, or null if none found
     */
    private BlockPos findTarget() {
        Level level = this.mob.level();
        BlockPos fishPos = this.mob.blockPosition();
        List<BlockPos> candidates = new ArrayList<>();

        int surfaceY = findSurfaceY();

        for (int x = -SCAN_RANGE; x <= SCAN_RANGE; x++) {
            for (int y = 0; y <= SCAN_RANGE; y++) {
                for (int z = -SCAN_RANGE; z <= SCAN_RANGE; z++) {
                    BlockPos pos = fishPos.offset(x, y, z);
                    if (pos.getY() < surfaceY) continue;

                    BlockState blockState = level.getBlockState(pos);
                    if (!blockState.is(ModTags.Blocks.FOLIAGE)) continue;
                    if (level.getFluidState(pos).is(FluidTags.WATER)) continue;

                    candidates.add(pos);
                }
            }
        }

        if (candidates.isEmpty()) return null;
        return candidates.get(this.mob.getRandom().nextInt(candidates.size()));
    }

    /***
     * Finds the Y level of the water surface above the fish.
     * @return The Y coordinate of the first non-water block above the fish
     */
    private int findSurfaceY() {
        BlockPos pos = this.mob.blockPosition();
        Level level = this.mob.level();
        while (level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getMaxBuildHeight()) {
            pos = pos.above();
        }
        return pos.getY();
    }
}