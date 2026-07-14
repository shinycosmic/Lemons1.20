package net.lemon.animalia.client.screens;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class AnimaliaSlot extends SlotItemHandler {
    private final Predicate<ItemStack> validator;

    public static final Predicate<ItemStack> DENY_ALL = stack -> false;

    public AnimaliaSlot(IItemHandler handler, int index, int x, int y, Predicate<ItemStack> validator) {
        super(handler, index, x, y);
        this.validator = validator;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return validator.test(stack);
    }
}