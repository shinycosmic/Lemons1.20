package net.lemon.animalia.entity.projectiles;

import net.lemon.animalia.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QuillProjectile extends AbstractArrow {
    public QuillProjectile(EntityType<? extends QuillProjectile> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public QuillProjectile(Level level, double x, double y, double z) {
        super(ModEntities.QUILL.get(), x, y, z, level);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}