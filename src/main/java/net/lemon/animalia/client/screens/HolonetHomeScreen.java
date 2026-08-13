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

import static net.lemon.animalia.util.AnimaliaConstants.*;

@OnlyIn(Dist.CLIENT)
public class HolonetHomeScreen extends Screen {

    private static final ResourceLocation FISH_ICON = new ResourceLocation(Animalia.MODID, "textures/gui/fish_compendium_icon.png");
    private static final ResourceLocation FIELD_ICON = new ResourceLocation(Animalia.MODID, "textures/gui/field_guide_icon.png");
    private static final ResourceLocation TUTORIAL_ICON = new ResourceLocation(Animalia.MODID, "textures/gui/tutorial_icon.png");

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

        graphics.blit(BACKGROUND, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT*1.6));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT*1.6));

        int totalWidth = (ICON_SIZE * 3) + (ICON_SPACING * 2);
        int startX = bgX + (BG_WIDTH - totalWidth) / 2;
        int iconY = bgY + (BG_HEIGHT - ICON_SIZE) / 2;

        graphics.blit(FISH_ICON, startX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.fish_compendium"),
                startX + ICON_SIZE / 2, iconY + ICON_SIZE + 4, 0xFFFFFF);

        int fieldX = startX + ICON_SIZE + ICON_SPACING;
        graphics.blit(FIELD_ICON, fieldX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.field_guide"),
                fieldX + ICON_SIZE / 2, iconY + ICON_SIZE + 4, 0xFFFFFF);

        int tutorialsX = fieldX + ICON_SIZE + ICON_SPACING;
        graphics.blit(TUTORIAL_ICON, tutorialsX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.tutorials"),
                tutorialsX + ICON_SIZE / 2, iconY + ICON_SIZE + 4, 0xFFFFFF);

        if (isOver(mouseX, mouseY, tutorialsX, iconY)) {
            graphics.fill(tutorialsX - 2, iconY - 2, tutorialsX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, 0x44FFFFFF);
        }

        if (isOver(mouseX, mouseY, startX, iconY)) {
            graphics.fill(startX - 2, iconY - 2, startX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, 0x44FFFFFF);
        }
        if (isOver(mouseX, mouseY, fieldX, iconY)) {
            graphics.fill(fieldX - 2, iconY - 2, fieldX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, 0x44FFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 1) {
            this.onClose();
            return true;
        }
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;
        int totalWidth = (ICON_SIZE * 3) + (ICON_SPACING * 2);
        int startX = bgX + (BG_WIDTH - totalWidth) / 2;
        int iconY = bgY + (BG_HEIGHT - ICON_SIZE) / 2;

        int fieldX = startX + ICON_SIZE + ICON_SPACING;
        int tutorialsX = fieldX + ICON_SIZE + ICON_SPACING;

        if (isOver((int) mouseX, (int) mouseY, startX, iconY)) {
            Minecraft.getInstance().setScreen(new CompendiumHomeScreen(Scannable.AppName.FISH, this));
            return true;
        }
        if (isOver((int) mouseX, (int) mouseY, fieldX, iconY)) {
            Minecraft.getInstance().setScreen(new CompendiumHomeScreen(Scannable.AppName.FIELD, this));
            return true;
        }
        if (isOver((int) mouseX, (int) mouseY, tutorialsX, iconY)) {
            Minecraft.getInstance().setScreen(new TutorialListScreen(this));
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