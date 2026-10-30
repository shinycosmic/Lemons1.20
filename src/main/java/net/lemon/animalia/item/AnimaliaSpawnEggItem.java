package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.IsGenetic;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AnimaliaSpawnEggItem extends ForgeSpawnEggItem {
    public AnimaliaSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> type = this.getType(stack.getTag());
        return Component.translatable("item.animalia.spawn_egg", type.getDescription());
    }

    @Override
    public Optional<Mob> spawnOffspringFromSpawnEgg(Player pPlayer, Mob pMob, EntityType<? extends Mob> pEntityType, ServerLevel level, Vec3 pPos, ItemStack stack) {
        if(pMob instanceof IsGenetic) {
            return Optional.empty();
        }
        return super.spawnOffspringFromSpawnEgg(pPlayer, pMob, pEntityType, level, pPos, stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() && this.getType(stack.getTag()) == ModEntities.BETTA_SPLENDENS.get()) {
            if (!level.isClientSide) {
                CompoundTag entityTag = stack.getOrCreateTagElement("EntityTag");
                boolean special = !entityTag.getBoolean("SpawnSpecial");
                if (special) {
                    entityTag.putBoolean("SpawnSpecial", true);
                } else {
                    entityTag.remove("SpawnSpecial");
                }
                if (entityTag.isEmpty()) {
                    stack.removeTagKey("EntityTag");
                }
                player.displayClientMessage(Component.translatable(special ? "item.animalia.spawn_egg.special" : "item.animalia.spawn_egg.random"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag entityTag = stack.getTagElement("EntityTag");
        return entityTag != null && entityTag.getBoolean("SpawnSpecial");
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = this.getType(stack.getTag());

        String key = type.getDescriptionId() + ".scientific";
        if (Language.getInstance().has(key)) {
            tooltipComp.add(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}