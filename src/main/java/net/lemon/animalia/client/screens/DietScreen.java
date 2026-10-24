package net.lemon.animalia.client.screens;

import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.lemon.animalia.util.AnimaliaConstants.*;

@OnlyIn(Dist.CLIENT)
public class DietScreen extends Screen {
    private static final int COLS = 6;
    private static final int ROWS = 5;
    private static final int CELL = 18;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int BOX_WIDTH = COLS * CELL;
    private static final int BOX_HEIGHT = ROWS * CELL;

    private final Scannable.AppName app;
    private final Screen detail;
    private final Screen parent;
    private final List<ItemStack> items = new ArrayList<>();

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;
    private int boxX;
    private int boxY;
    private int arrowX;
    private int arrowY;

    private float scrollOffset = 0;
    private boolean isDraggingScrollbar = false;

    public DietScreen(Scannable.AppName app, TagKey<Item> foodTag, Screen detail, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.diet"));
        this.app = app;
        this.detail = detail;
        this.parent = parent;
        BuiltInRegistries.ITEM.getTagOrEmpty(foodTag).forEach(holder -> this.items.add(new ItemStack(holder.value())));
        this.items.sort(Comparator.comparing(stack -> stack.getHoverName().getString()));
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        backBtnX = bgX + BACK_BUTTON_MARGIN_LEFT;
        backBtnY = bgY + BACK_BUTTON_MARGIN_TOP;
        boxX = bgX + (BG_WIDTH - BOX_WIDTH - SCROLLBAR_WIDTH - 4) / 2;
        boxY = bgY + 60;
        arrowX = bgX + 50;
        arrowY = bgY + BG_HEIGHT - 38 - BACK_BUTTON_SIZE;
    }

    private int contentHeight() {
        return (items.size() + COLS - 1) / COLS * CELL;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - BOX_HEIGHT);
    }

    private boolean isOverBox(double mouseX, double mouseY) {
        return mouseX >= boxX && mouseX <= boxX + BOX_WIDTH + 4 + SCROLLBAR_WIDTH && mouseY >= boxY && mouseY <= boxY + BOX_HEIGHT;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CompendiumHomeScreen.renderHelper(graphics, app, FISH_BG, FIELD_BG, bgX, bgY, BG_WIDTH, BG_HEIGHT, BORDER);
        int color = app == Scannable.AppName.FISH ? 0x00FFCC : 0x65e900;
        graphics.drawCenteredString(this.font, this.title, bgX + BG_WIDTH / 2, bgY + 40, color);

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
        ItemStack hovered = ItemStack.EMPTY;

        graphics.enableScissor(boxX, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT);
        for (int i = 0; i < items.size(); i++) {
            int cellX = boxX + (i % COLS) * CELL;
            int cellY = boxY + (i / COLS) * CELL - (int) scrollOffset;
            if (cellY + CELL < boxY || cellY > boxY + BOX_HEIGHT) continue;
            graphics.renderItem(items.get(i), cellX + 1, cellY + 1);
            if (mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL
                    && mouseY >= boxY && mouseY <= boxY + BOX_HEIGHT) {
                graphics.fill(cellX, cellY, cellX + CELL, cellY + CELL, 0x44FFFFFF);
                hovered = items.get(i);
            }
        }
        graphics.disableScissor();

        if (maxScroll() > 0) {
            int scrollbarX = boxX + BOX_WIDTH + 4;
            graphics.fill(scrollbarX, boxY, scrollbarX + SCROLLBAR_WIDTH, boxY + BOX_HEIGHT, 0x44000000);
            int thumbHeight = Math.max(15, (int) (BOX_HEIGHT * ((float) BOX_HEIGHT / contentHeight())));
            int thumbY = boxY + (int) ((scrollOffset / maxScroll()) * (BOX_HEIGHT - thumbHeight));
            int thumbColor = app == Scannable.AppName.FISH ? 0xFF1A3A5C : 0xFF5C3A1A;
            graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
        }

        graphics.drawString(this.font, "◀", arrowX, arrowY, 0xFFFFFF, true);
        if (isOverArrow(mouseX, mouseY)) {
            graphics.fill(arrowX - 2, arrowY - 2, arrowX + this.font.width("◀") + 2, arrowY + this.font.lineHeight + 2, 0x44FFFFFF);
        }

        graphics.blit(BACK_BUTTON, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, 0x44FFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        if (!hovered.isEmpty()) {
            graphics.renderTooltip(this.font, hovered, mouseX, mouseY);
        }
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
            if (isOverArrow((int) mouseX, (int) mouseY)) {
                Minecraft.getInstance().setScreen(detail);
                return true;
            }
            int scrollbarX = boxX + BOX_WIDTH + 4;
            if (maxScroll() > 0 && mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH && mouseY >= boxY && mouseY <= boxY + BOX_HEIGHT) {
                isDraggingScrollbar = true;
                return true;
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
            int thumbHeight = Math.max(15, (int) (BOX_HEIGHT * ((float) BOX_HEIGHT / contentHeight())));
            int trackSpace = BOX_HEIGHT - thumbHeight;
            if (trackSpace > 0) {
                float relativeY = (float) (mouseY - boxY - thumbHeight / 2) / trackSpace;
                scrollOffset = Math.max(0, Math.min(relativeY * maxScroll(), maxScroll()));
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isOverBox(mouseX, mouseY) && maxScroll() > 0) {
            scrollOffset = Math.max(0, Math.min(scrollOffset - (float) (delta * CELL), maxScroll()));
            return true;
        }
        if (delta > 0) {
            Minecraft.getInstance().setScreen(detail);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private boolean isOverArrow(int mouseX, int mouseY) {
        return mouseX >= arrowX && mouseX <= arrowX + this.font.width("◀")
                && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight;
    }

    private boolean isOverBackButton(int mouseX, int mouseY) {
        return mouseX >= backBtnX && mouseX <= backBtnX + BACK_BUTTON_SIZE
                && mouseY >= backBtnY && mouseY <= backBtnY + BACK_BUTTON_SIZE;
    }

    private void goBack() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}