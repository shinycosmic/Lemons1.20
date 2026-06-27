package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompendiumHomeScreen extends Screen {

    // PLACEHOLDER TEXTURES
    private static final ResourceLocation FISH_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;

    private final Scannable.AppName app;
    private final Screen parent;

    public CompendiumHomeScreen(Scannable.AppName app, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.splash"));
        this.app = app;
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;

        ResourceLocation bg = app == Scannable.AppName.FISH ? FISH_BACKGROUND : FIELD_BACKGROUND;
        graphics.blit(bg, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT*1.5));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT*1.5));

        // TODO: Render random 3D fish model with mouse-driven rotation
        // TODO: Render progress bar (X / Total discovered)

        String title = app == Scannable.AppName.FISH ? "Fish Compendium" : "Field Guide";
        graphics.drawCenteredString(this.font, title, this.width / 2, this.height / 2 - 40, 0x00FFCC);
        graphics.drawCenteredString(this.font, "Tap anywhere to continue", this.width / 2, this.height / 2 + 40, 0xAAAAAA);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Tap anywhere → go to order list
        // TODO: Minecraft.getInstance().setScreen(new OrderListScreen(app, parent));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
