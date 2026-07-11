package net.lemon.animalia.client.toast;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DiscoveryToast implements Toast {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/toasts.png");
    private static final Component TITLE = Component.translatable("toast.animalia.discovered");
    private static final ItemStack ICON = new ItemStack(ModItems.HOLONET.get());
    private final Component entityName;
    private long lastChanged;
    private boolean changed;

    public DiscoveryToast(Component entityName) {
        this.entityName = entityName;
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        if (this.changed) {
            this.lastChanged = timeSinceLastVisible;
            this.changed = false;
        }
        guiGraphics.blit(BACKGROUND, 0, 0, 0, 0, this.width(), this.height());
        guiGraphics.renderItem(ICON, 8, 8);
        guiGraphics.drawString(toastComponent.getMinecraft().font, this.entityName, 30, 7, 0xFFFFFF, false);
        guiGraphics.drawString(toastComponent.getMinecraft().font, TITLE, 30, 18, 0xFFFF00, false);

        return (double)(timeSinceLastVisible - this.lastChanged) >= 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier()
                ? Visibility.HIDE : Visibility.SHOW;
    }
}