package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;

public class FishFrySwimmingGoal extends RandomSwimmingGoal {
    private final PathfinderMob fish;
    public FishFrySwimmingGoal(PathfinderMob fish, double speed, int interval) {
        super(fish, speed, interval);
        this.fish = fish;
    }

    @Override
    public boolean canUse() {
        return fish instanceof FishBase baby && baby.isBaby() && super.canUse();
    }
}
