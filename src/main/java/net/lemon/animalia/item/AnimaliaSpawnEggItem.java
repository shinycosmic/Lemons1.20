package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = this.getType(stack.getTag());

        String key = type.getDescriptionId() + ".scientific";
        if (Language.getInstance().has(key)) {
            tooltipComp.add(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}