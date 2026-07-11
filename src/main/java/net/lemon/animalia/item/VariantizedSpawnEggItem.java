package net.lemon.animalia.item;

import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.Optional;
import java.util.function.Supplier;

public class VariantizedSpawnEggItem extends AnimaliaSpawnEggItem {
    public VariantizedSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public Optional<Mob> spawnOffspringFromSpawnEgg(Player pPlayer, Mob pMob, EntityType<? extends Mob> pEntityType, ServerLevel level, Vec3 pPos, ItemStack stack) {
        return Optional.empty();
    }
}
