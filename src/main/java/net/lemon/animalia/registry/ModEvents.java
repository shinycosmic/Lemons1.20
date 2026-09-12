package net.lemon.animalia.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lemon.animalia.Animalia;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Animalia.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == ModVillagers.AQUARIST.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.COD, 1), new ItemStack(ModItems.FISH_FOOD.get(), 4), 10, 8, 0.02f));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.COD, 1), new ItemStack(ModItems.RAW_FISH.get(), 1), 10, 8, 0.02f));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.WORM.get(), 7), 10, 8, 0.02f));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.COD, 1), new ItemStack(Items.SALMON, 1), 10, 8, 0.02f));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.COD, 2), 10, 8, 0.02f));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.COD, 1), new ItemStack(Items.TROPICAL_FISH, 1), 10, 8, 0.02f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.WHEAT_SEEDS, 6), new ItemStack(ModItems.TADPOLE.get(), 1), 10, 8, 0.02f));
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(ModItems.ARTEMIA.get(), 4), 10, 8, 0.02f));
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.TROPICAL_FISH, 1), new ItemStack(ModItems.AMPHIPOD.get(), 3), 10, 8, 0.02f));
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.WHEAT_SEEDS, 6), new ItemStack(ModItems.AMPHIPOD.get(), 1), 10, 8, 0.02f));
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.COD, 1), new ItemStack(ModBlocks.ALGAE_MAT.get(), 1), 10, 8, 0.02f));
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2), new ItemStack(Items.FISHING_ROD, 1), 2, 24, 0.02f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BRAIN_CORAL, 2), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.TUBE_CORAL, 2), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.FIRE_CORAL, 2), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BUBBLE_CORAL, 2), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.HORN_CORAL, 2), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.SALMON_BUCKET, 1), 4, 8, 0.02f));
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), new ItemStack(Items.PUFFERFISH_BUCKET, 1), 4, 8, 0.02f));

            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 3), new ItemStack(Items.NAUTILUS_SHELL, 1), 4, 8, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(ModItems.RAW_ICEFISH.get(), 12), new ItemStack(Items.HEART_OF_THE_SEA, 1), 4, 50, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8), new ItemStack(ModItems.PUFFERFISH_BUCKET, 1), 1, 24, 0.02f));

            /**
             * L1 Fish Food, Raw Cod, Raw Salmon, Raw Fish, Worm, Tropical Fish
             * L2 Tadpole, Brine Shrimp, Amphipod, Algae
             * L3 Raw Crustacean, Bucket of Salmon, Bucket of Cod
             * L4 Bucket of (random) Betta, Bucket of Pufferfish
             */

        }


    }
}
