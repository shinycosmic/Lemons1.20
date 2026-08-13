package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.client.player.network.ClientDiscoveryCache;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.lemon.animalia.util.AnimaliaConstants.*;

@OnlyIn(Dist.CLIENT)
public class OrderListScreen extends Screen {

    private static final int PANEL_LEFT = 40;
    private static final int PANEL_TOP = 80;
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 140;
    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 6;
    private static final int ROW_HEIGHT = 14;
    private static final int ROW_PADDING = 2;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int FISH_SCROLLBAR_COLOR = 0xFF1A3A5C;  // darker blue
    private static final int FIELD_SCROLLBAR_COLOR = 0xFF5C3A1A; // darker orange
    private static final int SCROLLBAR_TRACK_COLOR = 0x44000000; // semi-transparent black
    private static final int FISH_BAR_COLOR = 0xFF1A8A5C;
    private static final int FIELD_BAR_COLOR = 0xFF1A8A5C;
    private static final int BAR_BG_COLOR = 0x44000000;

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
        Collections.sort(this.orders);
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        backBtnX = bgX + BACK_BUTTON_MARGIN_LEFT;
        backBtnY = bgY + BACK_BUTTON_MARGIN_TOP;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.animalia.holonet.view_all"), b -> {
            Minecraft.getInstance().setScreen(new OrderGridScreen(app, null, this));
        }).pos(bgX + BG_WIDTH - 90, bgY + 35).size(55, 16).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CompendiumHomeScreen.renderHelper(graphics, app, FISH_BG, FIELD_BG, bgX, bgY, BG_WIDTH, BG_HEIGHT, BORDER);
        int color = app == Scannable.AppName.FISH ? 0x00FFCC : 0x65e900;
        graphics.drawCenteredString(this.font, Component.translatable("gui.animalia.holonet.order_list"), bgX + BG_WIDTH / 2, bgY + 40, color);

        int panelX = bgX + PANEL_LEFT;
        int panelY = bgY + PANEL_TOP;
        int panelRight = panelX + PANEL_WIDTH;
        int panelBottom = panelY + PANEL_HEIGHT;
        int contentWidth = PANEL_WIDTH - SCROLLBAR_WIDTH - 4;
        int totalContentHeight = orders.size() * (ROW_HEIGHT + ROW_PADDING);
        int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        graphics.enableScissor(panelX, panelY, panelRight - SCROLLBAR_WIDTH - 4, panelBottom);

        for (int i = 0; i < orders.size(); i++) {
            int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;
            if (rowY + ROW_HEIGHT < panelY || rowY > panelBottom) continue;
            int rowX = panelX;
            int rowRight = panelX + contentWidth;
            if (mouseX >= rowX && mouseX <= rowRight && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                    && mouseY >= panelY && mouseY <= panelBottom) {
                graphics.fill(rowX, rowY, rowRight, rowY + ROW_HEIGHT, 0x44FFFFFF);
            }

            int textY = rowY + (ROW_HEIGHT - this.font.lineHeight) / 2;
            graphics.drawString(this.font, orders.get(i), rowX + 8, textY, 0xFFFFFF, true);

            int discovered = getDiscoveredCountForOrder(orders.get(i));
            int total = HolonetEntities.getForOrder(app, orders.get(i)).size();
            int barX = rowRight - BAR_WIDTH - 8;
            int barY = rowY + (ROW_HEIGHT - BAR_HEIGHT) / 2;
            graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BAR_BG_COLOR);
            if (total > 0) {
                int fillWidth = (int) ((float) discovered / total * BAR_WIDTH);
                int barColor = app == Scannable.AppName.FISH ? FISH_BAR_COLOR : FIELD_BAR_COLOR;
                graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, barColor);
            }
            String progress = discovered + "/" + total;
            graphics.drawString(this.font, progress, barX - this.font.width(progress) - 4, textY, 0xFFFFFF, true);
        }

        graphics.disableScissor();

        if (totalContentHeight > PANEL_HEIGHT) {
            int scrollbarX = panelRight - SCROLLBAR_WIDTH;
            graphics.fill(scrollbarX, panelY, scrollbarX + SCROLLBAR_WIDTH, panelBottom, SCROLLBAR_TRACK_COLOR);
            float thumbRatio = (float) PANEL_HEIGHT / totalContentHeight;
            int thumbHeight = Math.max(15, (int) (PANEL_HEIGHT * thumbRatio));
            int thumbY = panelY + (int) ((scrollOffset / maxScroll) * (PANEL_HEIGHT - thumbHeight));

            int thumbColor = app == Scannable.AppName.FISH ? FISH_SCROLLBAR_COLOR : FIELD_SCROLLBAR_COLOR;
            graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
        }

        graphics.blit(BACK_BUTTON, backBtnX, backBtnY, 0, 0,
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
            for (int i = 0; i < orders.size(); i++) {
                int rowY = panelY + i * (ROW_HEIGHT + ROW_PADDING) - (int) scrollOffset;
                if (mouseX >= panelX && mouseX <= panelX + contentWidth
                        && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT
                        && mouseY >= panelY && mouseY <= panelBottom) {
                    Minecraft.getInstance().setScreen(new OrderGridScreen(app, orders.get(i), this));
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

    private int getDiscoveredCountForOrder(String order) {
        int count = 0;
        for (EntityType<?> type : HolonetEntities.getForOrder(app, order)) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (ClientDiscoveryCache.isDiscovered(id)) {
                count++;
            }
        }
        return count;
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