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

    private static final ResourceLocation FISH_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    public static final ResourceLocation BACK_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_BOTTOM_MARGIN = 12;
    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;

    private final Scannable.AppName app;
    private final Screen parent;

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;

    public CompendiumHomeScreen(Scannable.AppName app, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.splash"));
        this.app = app;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        backBtnX = bgX + (BG_WIDTH - BACK_BUTTON_SIZE) / 2;
        backBtnY = bgY + BG_HEIGHT - BACK_BUTTON_SIZE - BACK_BUTTON_BOTTOM_MARGIN;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation bg = app == Scannable.AppName.FISH ? FISH_BACKGROUND : FIELD_BACKGROUND;
        graphics.blit(bg, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        graphics.blit(BACK_BUTTON_TEXTURE, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        // Hover highlight on back button
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, 0x44FFFFFF);
        }

        // TODO: Render random 3D fish model with mouse-driven rotation
        // TODO: Render progress bar (X / Total discovered)

        String title = app == Scannable.AppName.FISH ? "Fish Compendium" : "Field Guide";
        graphics.drawCenteredString(this.font, title, this.width / 2, this.height / 2 - 40, 0x00FFCC);
        graphics.drawCenteredString(this.font, "Tap anywhere to continue", this.width / 2, this.height / 2 + 40, 0xAAAAAA);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Right-click anywhere → go back to parent screen
        if (button == 1) {
            goBack();
            return true;
        }
        if (button == 0 && isOverBackButton((int) mouseX, (int) mouseY)) {
            goBack();
            return true;
        }
        if (button == 0) {
            // TODO: Minecraft.getInstance().setScreen(new OrderListScreen(app, this));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Esc key closes the entire Holonet GUI, not just this screen.
     */
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    /**
     * Goes back to the parent screen (HolonetHomeScreen).
     */
    private void goBack() {
        Minecraft.getInstance().setScreen(parent);
    }

    private boolean isOverBackButton(int mouseX, int mouseY) {
        return mouseX >= backBtnX && mouseX <= backBtnX + BACK_BUTTON_SIZE
                && mouseY >= backBtnY && mouseY <= backBtnY + BACK_BUTTON_SIZE;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}