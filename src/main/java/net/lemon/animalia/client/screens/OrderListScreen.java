package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OrderListScreen extends Screen {

    private static final ResourceLocation FISH_BACKGROUND =
            new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND =
            new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER =
            new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    public static final ResourceLocation BACK_BUTTON_TEXTURE =
            new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_BOTTOM_MARGIN = 36;

    // Scroll panel dimensions (relative to bgX/bgY)
    private static final int PANEL_LEFT = 40;
    private static final int PANEL_TOP = 20;
    private static final int PANEL_WIDTH = 310;
    private static final int PANEL_HEIGHT = 160;

    // Row dimensions
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_PADDING = 4;

    // Scrollbar
    private static final int SCROLLBAR_WIDTH = 6;

    // Colors
    private static final int FISH_SCROLLBAR_COLOR = 0xFF1A3A5C;  // darker blue
    private static final int FIELD_SCROLLBAR_COLOR = 0xFF5C3A1A; // darker orange
    private static final int SCROLLBAR_TRACK_COLOR = 0x44000000; // semi-transparent black

    private final Scannable.AppName app;
    private final Screen parent;
    private final List<String> orders;

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;

    private float scrollOffset = 0;
    private boolean isDraggingScrollbar = false;

    public OrderListScreen(Scannable.AppName app, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.orders"));
        this.app = app;
        this.parent = parent;
        this.orders = new ArrayList<>(HolonetEntities.getForApp(app).keySet());
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
        // Draw background layers
        ResourceLocation bg = app == Scannable.AppName.FISH ? FISH_BACKGROUND : FIELD_BACKGROUND;
        graphics.blit(bg, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));

        // Scroll panel bounds (absolute screen coordinates)
        int panelX = bgX + PANEL_LEFT;
        int panelY = bgY + PANEL_TOP;
        int panelRight = panelX + PANEL_WIDTH;
        int panelBottom = panelY + PANEL_HEIGHT;

        // Content area (excluding scrollbar)
        int contentWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 4;

        // Total content height
        int totalContentHeight = orders.size() * (ROW_HEIGHT + ROW_PADDING);
        int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT);

        // Clamp scroll
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        // Enable scissor for scroll panel
        graphics.enableScissor(panelX, panelY, panelRight - SCROLLBAR_WIDTH - 4, panelBottom);

        // Render order rows
        for (int i = 0; i < orders.size(); i++) {
            int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;

            // Skip rows outside visible area (optimization)
            if (rowY + ROW_HEIGHT < panelY || rowY > panelBottom) continue;

            int rowX = panelX;
            int rowRight = panelX + contentWidth;

            // Hover highlight
            if (mouseX >= rowX && mouseX <= rowRight && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                    && mouseY >= panelY && mouseY <= panelBottom) {
                graphics.fill(rowX, rowY, rowRight, rowY + ROW_HEIGHT, 0x44FFFFFF);
            }

            // Draw order name with drop shadow
            int textY = rowY + (ROW_HEIGHT - this.font.lineHeight) / 2;
            graphics.drawString(this.font, orders.get(i), rowX + 8, textY, 0xFFFFFF, true);
        }

        graphics.disableScissor();

        // Render scrollbar (if content overflows)
        if (totalContentHeight > PANEL_HEIGHT) {
            int scrollbarX = panelRight - SCROLLBAR_WIDTH;

            // Track
            graphics.fill(scrollbarX, panelY, scrollbarX + SCROLLBAR_WIDTH, panelBottom, SCROLLBAR_TRACK_COLOR);

            // Thumb
            float thumbRatio = (float) PANEL_HEIGHT / totalContentHeight;
            int thumbHeight = Math.max(15, (int) (PANEL_HEIGHT * thumbRatio));
            int thumbY = panelY + (int) ((scrollOffset / maxScroll) * (PANEL_HEIGHT - thumbHeight));

            int thumbColor = app == Scannable.AppName.FISH ? FISH_SCROLLBAR_COLOR : FIELD_SCROLLBAR_COLOR;
            graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
        }

        // Back button (fixed, outside scroll area)
        graphics.blit(BACK_BUTTON_TEXTURE, backBtnX, backBtnY, 0, 0,
                BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1,
                    backBtnY + BACK_BUTTON_SIZE + 1, 0x44FFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Right-click → go back
        if (button == 1) {
            goBack();
            return true;
        }

        if (button == 0) {
            // Back button
            if (isOverBackButton((int) mouseX, (int) mouseY)) {
                goBack();
                return true;
            }

            // Check scrollbar drag
            int panelX = bgX + PANEL_LEFT;
            int panelY = bgY + PANEL_TOP;
            int panelRight = panelX + PANEL_WIDTH;
            int panelBottom = panelY + PANEL_HEIGHT;
            int scrollbarX = panelRight - SCROLLBAR_WIDTH;

            if (mouseX >= scrollbarX && mouseX <= panelRight && mouseY >= panelY && mouseY <= panelBottom) {
                isDraggingScrollbar = true;
                return true;
            }

            // Check order click
            int contentWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 4;
            for (int i = 0; i < orders.size(); i++) {
                int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;
                if (mouseX >= panelX && mouseX <= panelX + contentWidth
                        && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                        && mouseY >= panelY && mouseY <= panelBottom) {
                    // TODO: Minecraft.getInstance().setScreen(new OrderGridScreen(app, orders.get(i), this));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollbar) {
            int panelY = bgY + PANEL_TOP;
            int totalContentHeight = orders.size() * (ROW_HEIGHT + ROW_PADDING);
            int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT);

            float thumbRatio = (float) PANEL_HEIGHT / totalContentHeight;
            int thumbHeight = Math.max(15, (int) (PANEL_HEIGHT * thumbRatio));
            int trackSpace = PANEL_HEIGHT - thumbHeight;

            if (trackSpace > 0) {
                float relativeY = (float) (mouseY - panelY - thumbHeight / 2) / trackSpace;
                scrollOffset = relativeY * maxScroll;
                scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Only scroll if mouse is within panel
        int panelX = bgX + PANEL_LEFT;
        int panelY = bgY + PANEL_TOP;
        int panelRight = panelX + PANEL_WIDTH;
        int panelBottom = panelY + PANEL_HEIGHT;

        if (mouseX >= panelX && mouseX <= panelRight && mouseY >= panelY && mouseY <= panelBottom) {
            int totalContentHeight = orders.size() * (ROW_HEIGHT + ROW_PADDING);
            int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT);
            scrollOffset -= (float) (delta * (ROW_HEIGHT + ROW_PADDING));
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

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