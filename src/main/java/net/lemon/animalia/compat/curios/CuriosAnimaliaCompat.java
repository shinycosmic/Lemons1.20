package net.lemon.animalia.compat.curios;

import net.lemon.animalia.compat.curios.holonet.HolonetRenderer;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosAnimaliaCompat {
    public static void init() {
        CuriosRendererRegistry.register(ModItems.HOLONET.get(), HolonetRenderer::new);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, CuriosAnimaliaCompat::onRenderArm);
    }
    private static void onRenderArm(RenderArmEvent event) {
        if (event.isCanceled()) {
            return;
        }
        int slot = event.getArm() == event.getPlayer().getMainArm() ? 0 : 1;
        CuriosApi.getCuriosInventory(event.getPlayer()).resolve()
                .flatMap(inv -> inv.getStacksHandler("bracelet"))
                .ifPresent(handler -> {
                    if (slot >= handler.getSlots() || !handler.getRenders().get(slot)) {
                        return;
                    }
                    ItemStack stack = handler.getCosmeticStacks().getStackInSlot(slot);
                    if (stack.isEmpty()) {
                        stack = handler.getStacks().getStackInSlot(slot);
                    }
                    if (stack.is(ModItems.HOLONET.get())) {
                        HolonetRenderer.renderFirstPerson(stack, event.getPlayer(), event.getArm(), event.getPoseStack(),
                                event.getMultiBufferSource(), event.getPackedLight());
                    }
                });
    }
}
