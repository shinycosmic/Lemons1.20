package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Animalia.MODID);

    public static final RegistryObject<CreativeModeTab> ANIMALIA_FOODS = CREATIVE_MODE_TABS.register("animalia_food",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RAW_FISH.get()))
                    .title(Component.translatable("creativetab.animalia_food"))
                    .displayItems((param, output) -> {
                        AnimaliaFoodTab.displayItems(output);
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> ANIMALIA_MOBS = CREATIVE_MODE_TABS.register("animalia_mobs",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Objects.requireNonNull(ForgeSpawnEggItem.fromEntityType(ModEntities.CHAENOCEPHALUS_ACERATUS.get()))))
                    .title(Component.translatable("creativetab.animalia_mobs"))
                    .displayItems((param, output) -> {
                        AnimaliaMobsTab.displayItems(output);
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> ANIMALIA_MISC = CREATIVE_MODE_TABS.register("animalia_misc",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HOLONET.get()))
                    .title(Component.translatable("creativetab.animalia_misc"))
                    .displayItems((param, output) -> {
                        AnimaliaMiscTab.displayItems(output);
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> ANIMALIA_PLANTS = CREATIVE_MODE_TABS.register("animalia_plants",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SAGITTARIA.get()))
                    .title(Component.translatable("creativetab.animalia_plants"))
                    .displayItems((param, output) -> {
                        AnimaliaPlantsTab.displayItems(output);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
