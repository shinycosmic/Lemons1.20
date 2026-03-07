package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class AnimaliaBucketItem extends MobBucketItem {
    public AnimaliaBucketItem(Supplier<? extends EntityType<?>> entitySupplier, Supplier<? extends Fluid> fluidSupplier, Supplier<? extends SoundEvent> soundSupplier, Properties properties) {
        super(entitySupplier, fluidSupplier, soundSupplier, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = getFishType();

        if (type != null && level != null) {
            Entity entity = type.create(level);

            if (entity instanceof FishBase fish) {
                if (stack.hasTag() && stack.getTag().getBoolean("BucketBaby")) {
                    tooltipComp.add(Component.translatable("tooltip.animalia.baby").withStyle(ChatFormatting.GRAY));
                }

                tooltipComp.add(Component.literal(fish.getScientificName()).withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
            }
        }
    }
}
