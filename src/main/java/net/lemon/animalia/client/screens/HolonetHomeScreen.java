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
public class HolonetHomeScreen extends Screen {

    // PLACEHOLDER TEXTURE — replace with final hologram phone background
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet/holonet_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet/holonet.png");
    private static final ResourceLocation FISH_ICON = new ResourceLocation(Animalia.MODID, "textures/gui/holonet/icon_fish_compendium.png");
    private static final ResourceLocation FIELD_ICON = new ResourceLocation(Animalia.MODID, "textures/gui/holonet/icon_field_guide.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;

    // Icon display size (scaled up from 16x16 source)
    private static final int ICON_SIZE = 32;
    private static final int ICON_SPACING = 64;

    public HolonetHomeScreen() {
        super(Component.translatable("gui.animalia.holonet"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;

        // Draw phone background
        graphics.blit(BACKGROUND, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);

        // Calculate icon positions (centered within background)
        int totalWidth = (ICON_SIZE * 2) + ICON_SPACING;
        int startX = bgX + (BG_WIDTH - totalWidth) / 2;
        int iconY = bgY + (BG_HEIGHT - ICON_SIZE) / 2;

        // Fish Compendium icon
        int fishX = startX;
        graphics.blit(FISH_ICON, fishX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.fish_compendium"),
                fishX + ICON_SIZE / 2, iconY + ICON_SIZE + 4, 0xFFFFFF);

        // Field Guide icon
        int fieldX = startX + ICON_SIZE + ICON_SPACING;
        graphics.blit(FIELD_ICON, fieldX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.field_guide"),
                fieldX + ICON_SIZE / 2, iconY + ICON_SIZE + 4, 0xFFFFFF);

        // Hover highlight
        if (isOver(mouseX, mouseY, fishX, iconY)) {
            graphics.fill(fishX - 2, iconY - 2, fishX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, 0x44FFFFFF);
        }
        if (isOver(mouseX, mouseY, fieldX, iconY)) {
            graphics.fill(fieldX - 2, iconY - 2, fieldX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, 0x44FFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;
        int totalWidth = (ICON_SIZE * 2) + ICON_SPACING;
        int startX = bgX + (BG_WIDTH - totalWidth) / 2;
        int iconY = bgY + (BG_HEIGHT - ICON_SIZE) / 2;

        int fishX = startX;
        int fieldX = startX + ICON_SIZE + ICON_SPACING;

        if (isOver((int) mouseX, (int) mouseY, fishX, iconY)) {
            Minecraft.getInstance().setScreen(new CompendiumHomeScreen(Scannable.AppName.FISH, this));
            return true;
        }
        if (isOver((int) mouseX, (int) mouseY, fieldX, iconY)) {
            Minecraft.getInstance().setScreen(new CompendiumHomeScreen(Scannable.AppName.FIELD, this));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isOver(int mouseX, int mouseY, int iconX, int iconY) {
        return mouseX >= iconX && mouseX <= iconX + ICON_SIZE
                && mouseY >= iconY && mouseY <= iconY + ICON_SIZE;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}