package net.lemon.animalia.item;

import net.lemon.animalia.client.screens.HolonetScreenOpener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HolonetItem extends Item {

    public HolonetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            HolonetScreenOpener.open();
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        tooltipComp.add(Component.empty()
                .append(Component.translatable("tooltip.animalia.holonet.action").withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE))
                .append(Component.translatable("tooltip.animalia.holonet.desc").withStyle(ChatFormatting.GRAY)));    }
}