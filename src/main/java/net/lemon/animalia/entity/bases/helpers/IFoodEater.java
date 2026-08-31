package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.item.ItemStack;

public interface IFoodEater {
    boolean eats(ItemStack stack);

    boolean isEating();

    void startEating();

    int eatAge();

    void setEatAge(int age);

    default void ageUpFromFood() {
        int age = this.eatAge();
        if (age < 0) {
            int boost = Math.max(1, -age / 10);
            this.setEatAge(Math.min(-1, age + boost));
        }
    }
}