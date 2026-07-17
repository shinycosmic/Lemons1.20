package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.client.player.network.ClientWelcomeCache;
import net.lemon.animalia.client.player.network.MarkLoadedFirstTimePacket;
import net.lemon.animalia.client.player.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HolonetWelcomeScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BAR_WIDTH = 140;
    private static final int BAR_HEIGHT = 14;
    private static final int BAR_BG_COLOR = 0x44000000;
    private static final int BAR_FILL_COLOR = 0xFF00FFCC;
    private static final int BUTTON_HOVER_COLOR = 0x44FFFFFF;

    /** Time in ticks for the loading bar to fill (7 seconds = 140 ticks). */
    private static final int LOAD_TICKS = 140;

    private int bgX;
    private int bgY;
    private int ticksOpen;
    private boolean loadComplete;

    public HolonetWelcomeScreen() {
        super(Component.translatable("gui.animalia.holonet.welcome"));
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksOpen < LOAD_TICKS) {
            ticksOpen++;
        }
        if (ticksOpen >= LOAD_TICKS) {
            loadComplete = true;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background
        graphics.blit(BACKGROUND, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));

        // Title
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.welcome.title"),
                bgX + BG_WIDTH / 2, bgY + 40, 0xFFFFFF);

        // Instructional text lines
        int textX = bgX + BG_WIDTH / 2;
        int textY = bgY + 55;
        int lineSpacing = 14;

        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.welcome.line1"),
                textX, textY, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.welcome.line2"),
                textX, textY + lineSpacing, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.welcome.line3"),
                textX, textY + lineSpacing * 2, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.animalia.holonet.welcome.line4"),
                textX, textY + lineSpacing * 3, 0xFFFFFF);

        // Loading bar / Start button area
        int barX = bgX + (BG_WIDTH - BAR_WIDTH) / 2;
        int barY = bgY + BG_HEIGHT - 60;

        if (!loadComplete) {
            // Draw loading bar background
            graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BAR_BG_COLOR);

            // Draw fill
            float progress = (float) ticksOpen / LOAD_TICKS;
            int fillWidth = (int) (progress * BAR_WIDTH);
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, BAR_FILL_COLOR);

            // "Setting Up" text above the bar
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.animalia.holonet.welcome.setting_up"),
                    bgX + BG_WIDTH / 2, barY - 14, 0xAAAAAA);
        } else {
            // Draw "Start" button
            graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BAR_FILL_COLOR);

            // Hover highlight
            if (isOverButton(mouseX, mouseY, barX, barY)) {
                graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BUTTON_HOVER_COLOR);
            }

            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.animalia.holonet.welcome.start"),
                    bgX + BG_WIDTH / 2, barY + 3, 0xFFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (loadComplete && button == 0) {
            int barX = bgX + (BG_WIDTH - BAR_WIDTH) / 2;
            int barY = bgY + BG_HEIGHT - 60;
            if (isOverButton((int) mouseX, (int) mouseY, barX, barY)) {
                ClientWelcomeCache.markSeen();
                ModNetwork.CHANNEL.sendToServer(new MarkLoadedFirstTimePacket());
                Minecraft.getInstance().setScreen(new HolonetHomeScreen());
                return true;
            }
        }
        // During loading, only Esc closes — block all clicks
        return true;
    }

    /**
     * Esc closes the Holonet entirely (even during loading).
     */
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    private boolean isOverButton(int mouseX, int mouseY, int barX, int barY) {
        return mouseX >= barX && mouseX <= barX + BAR_WIDTH
                && mouseY >= barY && mouseY <= barY + BAR_HEIGHT;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}