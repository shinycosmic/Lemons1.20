package net.lemon.animalia.entity.ai;

import net.lemon.animalia.entity.bases.helpers.IFoodEater;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class EatDroppedItemsGoal<T extends PathfinderMob & IFoodEater> extends Goal {
    private final T mob;
    private final double speedMultiplier;
    private final float searchRange;

    private ItemEntity targetItem;
    private int eatCooldown;
    private int nextSearchTime;

    public EatDroppedItemsGoal(T mob, double speedMultiplier, float searchRange) {
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
        this.searchRange = searchRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.eatCooldown > this.mob.tickCount) {
            return false;
        }
        if (this.mob.isEating()) {
            return false;
        }
        if (this.mob.tickCount < this.nextSearchTime) {
            return false;
        }
        this.nextSearchTime = this.mob.tickCount + 20 + this.mob.getRandom().nextInt(10);
        this.targetItem = this.findNearestFoodItem();
        return this.targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetItem != null && this.targetItem.isAlive()
                && this.mob.distanceTo(this.targetItem) < this.searchRange * 2.0F;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetItem, this.speedMultiplier);
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.targetItem, 10.0F, 10.0F);

        if (this.mob.distanceToSqr(this.targetItem) < 2.0D) {
            this.eatItem();
        } else {
            this.mob.getNavigation().moveTo(this.targetItem, this.speedMultiplier);
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.mob.getNavigation().stop();
    }

    private void eatItem() {
        ItemStack stack = this.targetItem.getItem();
        float healAmount = stack.getFoodProperties(this.mob) != null ? Objects.requireNonNull(stack.getFoodProperties(this.mob)).getNutrition() : 2;
        this.mob.heal(healAmount);
        this.mob.ageUpFromFood();
        this.mob.startEating();
        this.mob.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F + (this.mob.getRandom().nextFloat() - this.mob.getRandom().nextFloat()) * 0.4F);

        stack.shrink(1);
        if (stack.isEmpty()) {
            this.targetItem.discard();
        }

        this.eatCooldown = this.mob.tickCount + 40;
        this.targetItem = null;
    }

    private ItemEntity findNearestFoodItem() {
        List<ItemEntity> items = this.mob.level().getEntitiesOfClass(ItemEntity.class,
                this.mob.getBoundingBox().inflate(this.searchRange), item -> (item.isInWater() || item.onGround()) && this.mob.isFood(item.getItem()));

        ItemEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double dist = this.mob.distanceToSqr(item);
            if (dist < nearestDist && this.mob.hasLineOfSight(item)) {
                nearestDist = dist;
                nearest = item;
            }
        }

        if (nearest != null) {
            Path path = this.mob.getNavigation().createPath(nearest, 0);
            if (path == null || !path.canReach()) {
                return null;
            }
        }
        return nearest;
    }
}