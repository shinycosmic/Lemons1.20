package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.FishBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class FishEggItem extends Item {
    public FishEggItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> type = getEntity(stack);

        if (type != null) {
            return Component.translatable(
                    "item.animalia.fish_egg.species",
                    type.getDescription()
            );
        }
        return super.getName(stack);
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);

        ServerLevel server = (ServerLevel) level;

        EntityType<?> type = getEntity(stack);
        if (type == null)
            return InteractionResultHolder.fail(stack);

        Entity entity = type.create(server);
        if (!(entity instanceof FishBase baby))
            return InteractionResultHolder.fail(stack);

        baby.setAge(-24000); // make baby
        baby.setVarSizeMultiplier(baby.genVarSizeMultiplier());
        baby.setGender(baby.getRandom().nextInt(2));
        baby.copyPosition(player);

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

        EntityType<?> type = getEntity(stack);
        if (type == null)
            return InteractionResult.FAIL;

        Entity entity = type.create(server);
        if (!(entity instanceof FishBase baby))
            return InteractionResult.FAIL;

        // Spawn position = block face clicked
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();

        // Spawn slightly offset in the direction of the face
        double x = pos.getX() + 0.5 + face.getStepX() * 0.6;
        double y = pos.getY() + 0.5 + face.getStepY() * 0.6;
        double z = pos.getZ() + 0.5 + face.getStepZ() * 0.6;

        baby.moveTo(x, y, z, 0.0F, 0.0F);

        // Make baby
        baby.setAge(-24000);
        baby.setVarSizeMultiplier(baby.genVarSizeMultiplier());
        baby.setGender(baby.getRandom().nextInt(2));

        server.addFreshEntity(baby);

        if (player != null && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

}
