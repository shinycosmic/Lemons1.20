package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.item.ItemStack;

public interface IFoodEater {
    boolean isFood(ItemStack stack);

    boolean isEating();

    void startEating();

    int getAge();

    void setAge(int age);

    default void ageUpFromFood() {
        int age = this.getAge();
        if (age < 0) {
            int boost = Math.max(1, -age / 10);
            this.setAge(Math.min(-1, age + boost));
        }
    }
}