package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.util.HolonetTutorials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TutorialListScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Animalia.MODID, "textures/gui/holonet_transparent.png");
    private static final ResourceLocation BORDER =
            new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    public static final ResourceLocation BACK_BUTTON_TEXTURE =
            new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_MARGIN_LEFT = 38;
    private static final int BACK_BUTTON_MARGIN_TOP = 35;
    private static final int PANEL_LEFT = 40;
    private static final int PANEL_TOP = 80;
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 140;
    private static final int ROW_HEIGHT = 14;
    private static final int ROW_PADDING = 2;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_COLOR = 0xFF5C5C1A;
    private static final int SCROLLBAR_TRACK_COLOR = 0x44000000;
    private static final int TITLE_COLOR = 0xFFCC33;

    private final Screen parent;
    private final List<String> tutorials;

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;

    private float scrollOffset = 0;
    private boolean isDraggingScrollbar = false;

    public TutorialListScreen(Screen parent) {
        super(Component.translatable("gui.animalia.holonet.tutorials"));
        this.parent = parent;
        this.tutorials = HolonetTutorials.getAll();
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        backBtnX = bgX + BACK_BUTTON_MARGIN_LEFT;
        backBtnY = bgY + BACK_BUTTON_MARGIN_TOP;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(BACKGROUND, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));
        graphics.drawCenteredString(this.font, Component.translatable("gui.animalia.holonet.tutorials"), bgX + BG_WIDTH / 2, bgY + 40, TITLE_COLOR);

        int panelX = bgX + PANEL_LEFT;
        int panelY = bgY + PANEL_TOP;
        int panelRight = panelX + PANEL_WIDTH;
        int panelBottom = panelY + PANEL_HEIGHT;
        int contentWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 4;
        int totalContentHeight = tutorials.size() * (ROW_HEIGHT + ROW_PADDING);
        int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        graphics.enableScissor(panelX, panelY, panelRight - SCROLLBAR_WIDTH - 4, panelBottom);

        for (int i = 0; i < tutorials.size(); i++) {
            int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;
            if (rowY + ROW_HEIGHT < panelY || rowY > panelBottom) continue;
            int rowX = panelX;
            int rowRight = panelX + contentWidth;
            if (mouseX >= rowX && mouseX <= rowRight && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                    && mouseY >= panelY && mouseY <= panelBottom) {
                graphics.fill(rowX, rowY, rowRight, rowY + ROW_HEIGHT, 0x44FFFFFF);
            }

            int textY = rowY + (ROW_HEIGHT - this.font.lineHeight) / 2;
            graphics.drawString(this.font, HolonetTutorials.getTitle(tutorials.get(i)), rowX + 8, textY, 0xFFFFFF, true);
        }

        graphics.disableScissor();

        if (totalContentHeight > PANEL_HEIGHT) {
            int scrollbarX = panelRight - SCROLLBAR_WIDTH;
            graphics.fill(scrollbarX, panelY, scrollbarX + SCROLLBAR_WIDTH, panelBottom, SCROLLBAR_TRACK_COLOR);
            float thumbRatio = (float) PANEL_HEIGHT / totalContentHeight;
            int thumbHeight = Math.max(15, (int) (PANEL_HEIGHT * thumbRatio));
            int thumbY = panelY + (int) ((scrollOffset / maxScroll) * (PANEL_HEIGHT - thumbHeight));
            graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, SCROLLBAR_COLOR);
        }

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
        if (button == 1) {
            goBack();
            return true;
        }

        if (button == 0) {
            if (isOverBackButton((int) mouseX, (int) mouseY)) {
                goBack();
                return true;
            }

            int panelX = bgX + PANEL_LEFT;
            int panelY = bgY + PANEL_TOP;
            int panelRight = panelX + PANEL_WIDTH;
            int panelBottom = panelY + PANEL_HEIGHT;
            int scrollbarX = panelRight - SCROLLBAR_WIDTH;

            if (mouseX >= scrollbarX && mouseX <= panelRight && mouseY >= panelY && mouseY <= panelBottom) {
                isDraggingScrollbar = true;
                return true;
            }

            int contentWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 4;
            for (int i = 0; i < tutorials.size(); i++) {
                int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;
                if (mouseX >= panelX && mouseX <= panelX + contentWidth
                        && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                        && mouseY >= panelY && mouseY <= panelBottom) {
                    Minecraft.getInstance().setScreen(new TutorialPageScreen(tutorials.get(i), this));
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
            int totalContentHeight = tutorials.size() * (ROW_HEIGHT + ROW_PADDING);
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
        int panelX = bgX + PANEL_LEFT;
        int panelY = bgY + PANEL_TOP;
        int panelRight = panelX + PANEL_WIDTH;
        int panelBottom = panelY + PANEL_HEIGHT;

        if (mouseX >= panelX && mouseX <= panelRight && mouseY >= panelY && mouseY <= panelBottom) {
            int totalContentHeight = tutorials.size() * (ROW_HEIGHT + ROW_PADDING);
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