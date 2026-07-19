package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Animalia.MODID);

    public static final RegistryObject<CreativeModeTab> ANIMALIA_FOODS = CREATIVE_MODE_TABS.register("animalia_food",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RAW_ICEFISH.get()))
                    .title(Component.translatable("creativetab.animalia_food"))
                    .displayItems((param, output) -> {
                        AnimaliaFoodTab.displayItems(output);
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> ANIMALIA_MOBS = CREATIVE_MODE_TABS.register("animalia_mobs",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CHAENOCEPHALUS_ACERATUS_SPAWN_EGG.get()))
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

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
