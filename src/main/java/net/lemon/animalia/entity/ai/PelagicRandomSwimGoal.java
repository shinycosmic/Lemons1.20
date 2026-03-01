package net.lemon.animalia.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PelagicRandomSwimGoal extends Goal {

    private final Mob fish;
    private final double speed;
    private int interval;
    public PelagicRandomSwimGoal(Mob fish, double speed) {
        this.fish = fish;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return fish.isInWater() && fish.getTarget() == null;
    }

    @Override
    public void tick() {
        if (--interval <= 0) {
            interval = 80 + fish.getRandom().nextInt(80);

            BlockPos pos = fish.blockPosition();

            double x = pos.getX() + fish.getRandom().nextInt(20) - 10;
            double z = pos.getZ() + fish.getRandom().nextInt(20) - 10;

            // Moderate vertical drift
            double y = pos.getY() + fish.getRandom().nextInt(6) - 3;

            fish.getMoveControl().setWantedPosition(x, y, z, speed);
        }
    }
}
