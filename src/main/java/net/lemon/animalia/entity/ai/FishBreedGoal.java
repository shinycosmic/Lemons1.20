package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.AnimaliaEggTypes;
import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class FishBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected FishBase fish;
    private AnimaliaEggTypes eggType;
    private final Class<? extends FishBase> partnerClass;
    protected Level level;
    @Nullable
    protected FishBase partner;
    private int loveTime;
    private final double speedModifier;

    public FishBreedGoal(FishBase fish, double speedMod) {
        this(fish, speedMod, fish.getClass());
    }
    public FishBreedGoal(FishBase fish, double speedMod, Class<? extends FishBase> partnerClass) {
        this.fish = fish;
        this.partnerClass = partnerClass;
        this.level = fish.level();
        this.speedModifier = speedMod;
        this.eggType = fish.getEggType();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

    }
    @Override
    public boolean canUse() {
        if (!this.fish.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        this.fish.getLookControl().setLookAt(this.partner, 10.0F, (float)this.fish.getMaxHeadXRot());
        this.fish.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.fish.distanceToSqr(this.partner) < 9.0D) {
            this.breed();
        }
    }

    @Nullable
    private FishBase getFreePartner() {
        List<? extends FishBase> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.fish, this.fish.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        FishBase fish0 = null;

        for(FishBase fish1 : list) {
            if (this.fish.canMate(fish1) && this.fish.distanceToSqr(fish1) < d0) {
                fish0 = fish1;
                d0 = this.fish.distanceToSqr(fish1);
            }
        }

        return fish0;
    }

    protected void breed() {
        this.fish.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
    }
}
