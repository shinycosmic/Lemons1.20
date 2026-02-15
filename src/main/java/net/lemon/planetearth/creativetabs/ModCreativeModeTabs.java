package net.lemon.planetearth.creativetabs;

import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PlanetEarth.MODID);

    public static final RegistryObject<CreativeModeTab> PLANETEARTH_FOOD = CREATIVE_MODE_TABS.register("planetearth_food",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RAW_RODENT.get()))
                    .title(Component.translatable("creativetab.planetearth_food"))
                    .displayItems((param, output) -> {
                        PlanetEarthFoodTab.displayItems(output);
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
