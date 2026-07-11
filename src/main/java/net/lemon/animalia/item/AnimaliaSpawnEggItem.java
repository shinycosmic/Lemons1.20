package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class AnimaliaSpawnEggItem extends ForgeSpawnEggItem {
    public AnimaliaSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = getType(stack.getTag());

        if (level != null) {
            Entity entity = type.create(level);

            if (entity instanceof AnimaliaBreedableWater fish) {
                tooltipComp.add(Component.literal(fish.getScientificName()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }

            //TODO add case for if instance is of AnimaliaBreedableLand
        }
    }
}