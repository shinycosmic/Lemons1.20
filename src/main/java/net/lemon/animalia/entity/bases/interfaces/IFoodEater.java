package net.lemon.animalia.entity.bases.interfaces;

import net.minecraft.world.item.ItemStack;

public interface IFoodEater {
    boolean isFood(ItemStack stack);

    boolean isEating();

    void startEating();

    int getAge();

    void setAge(int age);

    /***
     * Advances a baby's growth by 10% of its remaining growth time, matching
     * vanilla feeding behavior. Clamps to -1 so the normal age tick handles
     * the baby-to-adult transition. No-op for adults.
     */
    default void ageUpFromFood() {
        int age = this.getAge();
        if (age < 0) {
            int boost = Math.max(1, -age / 10);
            this.setAge(Math.min(-1, age + boost));
        }
    }
}