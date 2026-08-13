package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BottomDwellingGoal extends RandomSwimmingGoal {
    private final PathfinderMob fish;
    //Use sinkBlocks to control likelihood of descent. Higher the number, the more likely it will descend to the sea floor faster.
    private final int sinkBlocks;
    //Use depthCount to control how many blocks above the seafloor it is likely to swim up to at once
    private final int depthCount;
    public BottomDwellingGoal(PathfinderMob fish, double speed, int interval, int sinkBlocks, int depthCount) {
        super(fish, speed, interval);
        this.fish = fish;
        this.sinkBlocks = sinkBlocks;
        this.depthCount = depthCount;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return fish instanceof AnimaliaBreedableWater f && super.canUse() && fish.isInWater() && f.activityChecker(f);
    }

    /***
     * check blocks directly below to find floor. If floor not found, allow continuous swimming, but tend to choose lower value targets
     * @return
     */
    @Override
    protected @Nullable Vec3 getPosition() {
        BlockPos currPos = fish.blockPosition();
        BlockPos seafloor = currPos;

        for(int i = 0; i < sinkBlocks; i++) {
            BlockPos below = seafloor.below();
            if(!fish.level().getFluidState(below).is(FluidTags.WATER)) {
                break;
            }
            seafloor = below;
        }

        int heighAboveFloor = (seafloor.equals(currPos.below(depthCount))) ?
                fish.getRandom().nextInt(depthCount/2 + 1) :
                (fish.getRandom().nextFloat() < 0.8F ? fish.getRandom().nextInt(depthCount) : fish.getRandom().nextInt(depthCount * 2));

        int horizontalRange = 8+depthCount; // how far they can wander
        int xOffset = fish.getRandom().nextInt(horizontalRange * 2 + 1) - horizontalRange;
        int zOffset = fish.getRandom().nextInt(horizontalRange * 2 + 1) - horizontalRange;
        return Vec3.atBottomCenterOf(seafloor.offset(xOffset, heighAboveFloor, zOffset));
    }
}
