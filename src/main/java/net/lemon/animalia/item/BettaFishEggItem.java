package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BettaFishEggItem extends FishEggItem {
    public BettaFishEggItem(Properties pProperties) {
        super(pProperties);
    }

    public static void setEntity(ItemStack stack, EntityType<?> type) {
        if(type == null) return;
        stack.getOrCreateTag().putString("Species",
                ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
    }

    @Nullable
    public static EntityType<?> getEntity(ItemStack stack) {
        if(!stack.hasTag() || stack.getTag() == null) {
            return null;
        }
        String id = stack.getTag().getString("Species");
        if(id.isEmpty()) return null;
        return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> type = getEntity(stack);

        if (type != null) {
            return Component.translatable("item.animalia.betta_fish_egg.species");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = getEntity(stack);

        if (type != null && level != null) {
            Entity entity = type.create(level);

            if (entity instanceof BettaEntity fish) {
                if (stack.hasTag() && stack.getTag().contains("PrimaryColor")) {
                    CompoundTag pTag = stack.getTag();
                    ColorUtil primaryColor = ColorUtil.fromId(pTag.getInt("PrimaryColor"));
                    ColorUtil secondaryColor = ColorUtil.fromId(pTag.getInt("SecondaryColor"));
                    BettaTraits.PatternPreset pattern = BettaTraits.PatternPreset.fromId(pTag.getInt("PatternPreset"));
                    BettaTraits.CaudalPreset caudalPreset = BettaTraits.CaudalPreset.fromId(pTag.getInt("CaudalPreset"));
                    Boolean isSpecial = pTag.getBoolean("SpecialVariant");
                    String specialName = pTag.getString("SpecialName");

                    tooltipComp.add(BettaEntity.resolveName(pattern,primaryColor, secondaryColor, caudalPreset, specialName, isSpecial));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag compoundTag = stack.getTag();

        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);

        ServerLevel server = (ServerLevel) level;

        EntityType<?> type = getEntity(stack);
        if (type == null)
            return InteractionResultHolder.fail(stack);

        Entity entity = type.create(server);
        if (!(entity instanceof BettaEntity baby))
            return InteractionResultHolder.fail(stack);

        baby.setAge(-12000); // make baby
        baby.syncTraits(compoundTag);
        baby.setVarSizeMultiplier(baby.genVarSizeMultiplier());
        baby.setGender(baby.getRandom().nextInt(2));
        baby.copyPosition(player);
        baby.setPersistenceRequired();

        server.addFreshEntity(baby);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        ServerLevel server = (ServerLevel) level;
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        CompoundTag compoundTag = stack.getTag();

        EntityType<?> type = getEntity(stack);
        if (type == null)
            return InteractionResult.FAIL;

        Entity entity = type.create(server);
        if (!(entity instanceof BettaEntity baby))
            return InteractionResult.FAIL;

        // Spawn position = block face clicked
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();

        // Spawn slightly offset in the direction of the face
        double x = pos.getX() + 0.5 + face.getStepX() * 0.8;
        double y = pos.getY() + 0.5 + face.getStepY() * 0.8;
        double z = pos.getZ() + 0.5 + face.getStepZ() * 0.8;

        baby.moveTo(x, y, z, 0.0F, 0.0F);

        // Make baby
        baby.setAge(-12000);
        baby.syncTraits(compoundTag);
        baby.setVarSizeMultiplier(baby.genVarSizeMultiplier());
        baby.setGender(baby.getRandom().nextInt(2));
        baby.setPersistenceRequired();

        server.addFreshEntity(baby);

        if (player != null && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

}
