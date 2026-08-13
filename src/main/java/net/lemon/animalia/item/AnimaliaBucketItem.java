package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
    public Component getName(ItemStack stack) {
        EntityType<?> type = this.getFishType();
        return Component.translatable("item.animalia.bucket", type.getDescription());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = getFishType();

        if (type != null && level != null) {
            Entity entity = type.create(level);

            if (entity instanceof FishBase fish) {
                tooltipComp.add(Component.literal(fish.getScientificName()).withStyle(ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));

                if (stack.hasTag() && stack.getTag().getBoolean("BucketBaby")) {
                    tooltipComp.add(Component.translatable("tooltip.animalia.baby").withStyle(ChatFormatting.GRAY));
                }
                //Only for Betta fish, plans for koi so may need to consider refractoring this or adding another check
                if (stack.hasTag() && stack.getTag().contains("BucketBettaName") && entity instanceof BettaEntity) {
                    CompoundTag pTag = stack.getTag();
                    ColorUtil primaryColor = ColorUtil.fromId(pTag.getInt("PrimaryColor"));
                    ColorUtil secondaryColor = ColorUtil.fromId(pTag.getInt("SecondaryColor"));
                    BettaTraits.PatternPreset pattern = BettaTraits.PatternPreset.fromId(pTag.getInt("PatternPreset"));
                    BettaTraits.CaudalPreset caudalPreset = BettaTraits.CaudalPreset.fromId(pTag.getInt("CaudalPreset"));
                    Boolean isSpecial = pTag.getBoolean("SpecialVariant");
                    String specialName = pTag.getString("BucketBettaName");

                    tooltipComp.add(BettaEntity.resolveName(pattern,primaryColor, secondaryColor, caudalPreset, specialName, isSpecial));
                }
            }
        }
    }
}
