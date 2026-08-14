package net.lemon.animalia.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.client.player.network.ClientDiscoveryCache;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.entity.bases.helpers.IsGenetic;
import net.lemon.animalia.util.Scannable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.lemon.animalia.util.AnimaliaConstants.*;

@OnlyIn(Dist.CLIENT)
public class OrderGridScreen extends Screen {

    private static final int BACK_BUTTON_BOTTOM_MARGIN = 38;
    private static final int GRID_COLS = 7;
    private static final int GRID_ROWS = 3;
    private static final int CELLS_PER_PAGE = GRID_COLS * GRID_ROWS; // 21
    private static final int CELL_SIZE = 42;
    private static final int CELL_PADDING = 4;
    private static final int GRID_TOP_OFFSET = 55;
    private static final int LOCKED_CELL_COLOR = 0xBB888888;
    private static final int LOCKED_BORDER_COLOR = 0x55000000;
    private static final int HOVER_HIGHLIGHT = 0x44FFFFFF;
    private static final int PAGE_TEXT_COLOR = 0xAAAAAA;

    private final Scannable.AppName app;
    private final String order;
    private final Screen parent;
    private final List<EntityType<?>> species;
    private int currentPage = 0;

    private final Map<EntityType<?>, LivingEntity> cachedDummies = new HashMap<>();
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
        this.species = order != null ? HolonetEntities.getForOrder(app, order) : HolonetEntities.getAllForApp(app);
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        int gridTotalWidth = GRID_COLS * CELL_SIZE + (GRID_COLS - 1) * CELL_PADDING;
        gridStartX = bgX + (BG_WIDTH - gridTotalWidth) / 2;
        gridStartY = bgY + GRID_TOP_OFFSET;
        backBtnX = bgX + BACK_BUTTON_MARGIN_LEFT;
        backBtnY = bgY + BACK_BUTTON_MARGIN_TOP;
        if (order == null) {
            this.addRenderableWidget(Button.builder(Component.translatable("gui.animalia.holonet.by_order"), b -> {
                Minecraft.getInstance().setScreen(new OrderListScreen(app, this));
            }).pos(bgX + BG_WIDTH - 90, bgY + 35).size(55, 16).build());
        }
        buildDummyCache();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CompendiumHomeScreen.renderHelper(graphics, app, FISH_BG, FIELD_BG, bgX, bgY, BG_WIDTH, BG_HEIGHT, BORDER);
        int color = app == Scannable.AppName.FISH ? 0x00FFCC : 0x65e900;
        String title = order != null ? order : "All Species";
        graphics.drawCenteredString(this.font, title, bgX + BG_WIDTH / 2, bgY + 35, color);

        int pageStart = currentPage * CELLS_PER_PAGE;
        int pageEnd = Math.min(pageStart + CELLS_PER_PAGE, species.size());
        List<Component> hoveredTooltip = null;
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
                renderEntityInCell(graphics, entityType, cellX, cellY);
                if (isMouseOverCell(mouseX, mouseY, cellX, cellY)) {
                    graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, HOVER_HIGHLIGHT);

                    LivingEntity dummy = cachedDummies.get(entityType);
                    if(dummy instanceof AnimaliaBreedableWater water) {
                        hoveredTooltip = List.of(entityType.getDescription(), Component.literal(water.getScientificName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                    } else {
                        hoveredTooltip = List.of(entityType.getDescription());
                    }
                    tooltipX = mouseX;
                    tooltipY = mouseY;
                }
            } else {
                graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_CELL_COLOR);
                graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + 1, LOCKED_BORDER_COLOR);
                graphics.fill(cellX, cellY, cellX + 1, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);
                graphics.fill(cellX, cellY + CELL_SIZE - 1, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);
                graphics.fill(cellX + CELL_SIZE - 1, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, LOCKED_BORDER_COLOR);
                String questionMark = "?";
                int textX = cellX + (CELL_SIZE - this.font.width(questionMark)) / 2;
                int textY = cellY + (CELL_SIZE - this.font.lineHeight) / 2;
                graphics.drawString(this.font, questionMark, textX, textY, 0xFFFFFF, true);
                if (isMouseOverCell(mouseX, mouseY, cellX, cellY)) {
                    graphics.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, HOVER_HIGHLIGHT);
                    hoveredTooltip = List.of(Component.literal("???"));
                    tooltipX = mouseX;
                    tooltipY = mouseY;
                }
            }
        }

        int totalPages = getTotalPages();
        if (totalPages > 1) {
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

            String pageText = (currentPage + 1) + " / " + totalPages;
            graphics.drawCenteredString(this.font, pageText, bgX + BG_WIDTH / 2, arrowY, PAGE_TEXT_COLOR);
        }

        graphics.blit(BACK_BUTTON, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, HOVER_HIGHLIGHT);
        }

        if (hoveredTooltip != null) {
            graphics.renderComponentTooltip(this.font, hoveredTooltip, tooltipX, tooltipY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderEntityInCell(GuiGraphics graphics, EntityType<?> entityType, int cellX, int cellY) {
        LivingEntity dummy = cachedDummies.get(entityType);
        if (dummy == null) return;

        if(dummy instanceof GeoEntity geo) {
            geo.getAnimatableInstanceCache().getManagerForId(dummy.getId())
                    .getAnimationControllers().values().forEach(AnimationController::forceAnimationReset);
        }

        Scannable scannable = (Scannable) dummy;
        Quaternionf rotation = scannable.getRotforGUI();
        int yOffset = scannable.getYOffsetForGUI();
        int scale = Math.min(scannable.getScaleforGUI(), 128);

        int renderX = cellX + CELL_SIZE * 3 / 7 + scannable.getXOffsetForGUI();
        int renderY = cellY + CELL_SIZE - 4 + yOffset;

        Lighting.setupForEntityInInventory();
        InventoryScreen.renderEntityInInventory(graphics, renderX, renderY, scale, rotation, null, dummy);
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

            int totalPages = getTotalPages();
            if (totalPages > 1) {
                int arrowY = bgY + BG_HEIGHT - BACK_BUTTON_BOTTOM_MARGIN - BACK_BUTTON_SIZE;
                int leftArrowX = bgX + 50;
                int rightArrowX = bgX + BG_WIDTH - 65;
                if (currentPage > 0 && mouseX >= leftArrowX && mouseX <= leftArrowX + 20
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    currentPage--;
                    return true;
                }
                if (currentPage < totalPages - 1 && mouseX >= rightArrowX && mouseX <= rightArrowX + 20
                        && mouseY >= arrowY && mouseY <= arrowY + this.font.lineHeight) {
                    currentPage++;
                    return true;
                }
            }

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
                        Minecraft.getInstance().setScreen(new CreatureDetailScreen(app, entityType, this));
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

    private void buildDummyCache() {
        cachedDummies.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        for (EntityType<?> entityType : species) {
            LivingEntity dummy = (LivingEntity) entityType.create(mc.level);
            if (dummy == null) continue;

            if (dummy instanceof FishBase fish) {
                fish.setForcedInWater(true);
            }

            dummy.setOnGround(true);

            if (dummy instanceof IsGenetic genetic) {
                genetic.buildTraitsRandom();
            }
            dummy.discard();
            cachedDummies.put(entityType, dummy);
        }
    }
}