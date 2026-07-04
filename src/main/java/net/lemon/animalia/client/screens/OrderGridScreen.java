package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.player.network.ClientDiscoveryCache;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.IsGenetic;
import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OrderGridScreen extends Screen {

    // --- Textures ---
    private static final ResourceLocation FISH_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    private static final ResourceLocation BACK_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    // --- Layout constants ---
    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_BOTTOM_MARGIN = 36;

    // --- Grid constants ---
    private static final int GRID_COLS = 5;
    private static final int GRID_ROWS = 5;
    private static final int CELLS_PER_PAGE = GRID_COLS * GRID_ROWS; // 25
    private static final int CELL_SIZE = 32;
    private static final int CELL_PADDING = 4;
    private static final int GRID_TOP_OFFSET = 55; // from bgY, leaves room for header
    private static final int GRID_BOTTOM_MARGIN = 50; // space for back button + page arrows

    // --- Colors ---
    private static final int LOCKED_CELL_COLOR = 0xBB888888;
    private static final int LOCKED_BORDER_COLOR = 0x55000000;
    private static final int HOVER_HIGHLIGHT = 0x44FFFFFF;
    private static final int TITLE_COLOR = 0x00FFCC;
    private static final int PAGE_TEXT_COLOR = 0xAAAAAA;

    // --- State ---
    private final Scannable.AppName app;
    private final String order;
    private final Screen parent;
    private final List<EntityType<?>> species;
    private int currentPage = 0;

    // --- Cached positions ---
    private int bgX;
    private int bgY;
    private int gridStartX;
    private int gridStartY;
    private int backBtnX;
    private int backBtnY;

    public OrderGridScreen(Scannable.AppName app, String order, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.grid"));
        this.app = app;
        this.order = order;
        this.parent = parent;
        this.species = HolonetEntities.getForOrder(app, order);
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;

        // Center the grid horizontally within the background
        int gridTotalWidth = GRID_COLS * CELL_SIZE + (GRID_COLS - 1) * CELL_PADDING;
        gridStartX = bgX + (BG_WIDTH - gridTotalWidth) / 2;
        gridStartY = bgY + GRID_TOP_OFFSET;

        backBtnX = bgX + (BG_WIDTH - BACK_BUTTON_SIZE) / 2;
        backBtnY = bgY + BG_HEIGHT - BACK_BUTTON_SIZE - BACK_BUTTON_BOTTOM_MARGIN;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // --- Background layers ---
        ResourceLocation bg = app == Scannable.AppName.FISH ? FISH_BACKGROUND : FIELD_BACKGROUND;
        graphics.blit(bg, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));
        graphics.blit(BORDER, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, (int) (BG_HEIGHT * 1.6));

        // --- Header: Order name ---
        graphics.drawCenteredString(this.font, order, bgX + BG_WIDTH / 2, bgY + 35, TITLE_COLOR);

        // --- Grid cells ---
        int pageStart = currentPage * CELLS_PER_PAGE;
        int pageEnd = Math.min(pageStart + CELLS_PER_PAGE, species.size());

        Component hoveredTooltip = null;
        int tooltipX = 0, tooltipY = 0;

        for (int i = pageStart; i < pageEnd; i++) {
            int indexOnPage = i - pageStart;
            int col = indexOnPage % GRID_COLS;
            int row = indexOnPage / GRID_COLS;

            int cellX = gridStartX + col * (CELL_SIZE + CELL_PADDING);
            int cellY = gridStartY + row * (CELL_SIZE + CELL_PADDING);

            EntityType<?> entityType = species.get(i);
            ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            boolean discovered = ClientDiscoveryCache.isDiscovered(entityId);

            if (discovered) {
                // --- Render 3D entity in cell ---
                renderEntityInCell(graphics, entityType, cellX, cellY);

                // Hover highlight
                if (isMouseOverCell(mouseX, mouseY, cellX, cellY)) {
                    graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, HOVER_HIGHLIGHT);

                    // Tooltip: common name
                    LivingEntity dummy = createDummyEntity(entityType);
                    if (dummy != null) {
                        hoveredTooltip = dummy.getDisplayName();
                        tooltipX = mouseX;
                        tooltipY = mouseY;
                    }
                }
            } else {
                // --- Locked cell: gray fill + "?" ---
                graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_CELL_COLOR);

                // Border
                graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + 1, LOCKED_BORDER_COLOR);
                graphics.fill(cellX, cellY, cellX + 1, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);
                graphics.fill(cellX, cellY + CELL_SIZE - 1, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);
                graphics.fill(cellX + CELL_SIZE - 1, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);

                // "?" centered in cell
                String questionMark = "?";
                int textX = cellX + (CELL_SIZE - this.font.width(questionMark)) / 2;
                int textY = cellY + (CELL_SIZE - this.font.lineHeight) / 2;
                graphics.drawString(this.font, questionMark, textX, textY, 0xFFFFFF, true);

                // Hover tooltip for locked
                if (isMouseOverCell(mouseX, mouseY, cellX, cellY)) {
                    graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, HOVER_HIGHLIGHT);
                    hoveredTooltip = Component.literal("???");
                    tooltipX = mouseX;
                    tooltipY = mouseY;
                }
            }
        }

        // --- Pagination arrows ---
        int totalPages = getTotalPages();
        if (totalPages > 1) {
            // Left arrow (if not on first page)
            String leftArrow = "◀";
            int arrowY = bgY + BG_HEIGHT - BACK_BUTTON_BOTTOM_MARGIN - BACK_BUTTON_SIZE;
            int leftArrowX = bgX + 50;
            int rightArrowX = bgX + BG_WIDTH - 65;

            if (currentPage > 0) {
                graphics.drawString(this.font, leftArrow, leftArrowX, arrowY, 0xFFFFFF, true);
                if (mouseX >= leftArrowX && mouseX <= leftArrowX + this.font.width(leftArrow)
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    graphics.fill(leftArrowX - 2, arrowY - 2,
                            leftArrowX + this.font.width(leftArrow) + 2, arrowY + this.font.lineHeight + 2,
                            HOVER_HIGHLIGHT);
                }
            }

            // Right arrow (if not on last page)
            String rightArrow = "▶";
            if (currentPage < totalPages - 1) {
                graphics.drawString(this.font, rightArrow, rightArrowX, arrowY, 0xFFFFFF, true);
                if (mouseX >= rightArrowX && mouseX <= rightArrowX + this.font.width(rightArrow)
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    graphics.fill(rightArrowX - 2, arrowY - 2,
                            rightArrowX + this.font.width(rightArrow) + 2, arrowY + this.font.lineHeight + 2,
                            HOVER_HIGHLIGHT);
                }
            }

            // Page indicator
            String pageText = (currentPage + 1) + " / " + totalPages;
            graphics.drawCenteredString(this.font, pageText, bgX + BG_WIDTH / 2, arrowY, PAGE_TEXT_COLOR);
        }

        // --- Back button ---
        graphics.blit(BACK_BUTTON_TEXTURE, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, HOVER_HIGHLIGHT);
        }

        // --- Tooltip (rendered last, on top of everything) ---
        if (hoveredTooltip != null) {
            graphics.renderTooltip(this.font, hoveredTooltip, tooltipX, tooltipY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // --- Entity rendering in a grid cell ---
    private void renderEntityInCell(GuiGraphics graphics, EntityType<?> entityType, int cellX, int cellY) {
        LivingEntity dummy = createDummyEntity(entityType);
        if (dummy == null) return;

        // Use FishBase GUI helpers if available, otherwise calculate from bounding box
        Quaternionf rotation;
        int scale;
        int yOffset;

        if (dummy instanceof FishBase fish) {
            fish.setForcedInWater(true);
            fish.setOnGround(true);
            rotation = fish.getRotforGUI();
            scale = fish.getScaleforGUI();
            yOffset = fish.getYOffsetForGUI();
        } else {
            // Fallback for non-fish entities (field guide creatures)
            rotation = new Quaternionf().rotateZ((float) Math.PI).rotateY((float) Math.toRadians(140));
            float entityHeight = dummy.getBbHeight();
            scale = (int) (20f / entityHeight);
            yOffset = 0;
        }

        // Randomize genetic entities
        if (dummy instanceof IsGenetic genetic) {
            genetic.buildTraitsRandom();
        }

        // Render position: center of cell, near bottom
        int renderX = cellX + CELL_SIZE / 2;
        int renderY = cellY + CELL_SIZE - 4 + yOffset;

        // Clamp scale to fit within cell
        scale = Math.min(scale, 18);

        InventoryScreen.renderEntityInInventory(graphics, renderX, renderY, scale, rotation, null, dummy);
    }

    // --- Create a dummy entity for rendering ---
    private LivingEntity createDummyEntity(EntityType<?> entityType) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        return (LivingEntity) entityType.create(mc.level);
    }

    // --- Mouse interaction ---
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

            // Pagination arrows
            int totalPages = getTotalPages();
            if (totalPages > 1) {
                int arrowY = bgY + BG_HEIGHT - BACK_BUTTON_BOTTOM_MARGIN - BACK_BUTTON_SIZE;
                int leftArrowX = bgX + 50;
                int rightArrowX = bgX + BG_WIDTH - 65;

                // Left arrow
                if (currentPage > 0 && mouseX >= leftArrowX && mouseX <= leftArrowX + 20
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    currentPage--;
                    return true;
                }

                // Right arrow
                if (currentPage < totalPages - 1 && mouseX >= rightArrowX && mouseX <= rightArrowX + 20
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    currentPage++;
                    return true;
                }
            }

            // Grid cell click
            int pageStart = currentPage * CELLS_PER_PAGE;
            int pageEnd = Math.min(pageStart + CELLS_PER_PAGE, species.size());

            for (int i = pageStart; i < pageEnd; i++) {
                int indexOnPage = i - pageStart;
                int col = indexOnPage % GRID_COLS;
                int row = indexOnPage / GRID_COLS;

                int cellX = gridStartX + col * (CELL_SIZE + CELL_PADDING);
                int cellY = gridStartY + row * (CELL_SIZE + CELL_PADDING);

                if (isMouseOverCell((int) mouseX, (int) mouseY, cellX, cellY)) {
                    EntityType<?> entityType = species.get(i);
                    ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
                    if (ClientDiscoveryCache.isDiscovered(entityId)) {
                        // TODO: Minecraft.getInstance().setScreen(new CreatureDetailScreen(app, entityType, this));
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int totalPages = getTotalPages();
        if (totalPages > 1) {
            if (delta < 0 && currentPage < totalPages - 1) {
                currentPage++;
                return true;
            } else if (delta > 0 && currentPage > 0) {
                currentPage--;
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    // --- Helpers ---
    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) species.size() / CELLS_PER_PAGE));
    }

    private boolean isMouseOverCell(int mouseX, int mouseY, int cellX, int cellY) {
        return mouseX >= cellX && mouseX <= cellX + CELL_SIZE
                && mouseY >= cellY && mouseY <= cellY + CELL_SIZE;
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