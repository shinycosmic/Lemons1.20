package net.lemon.animalia.client.screens;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.IsGenetic;
import net.lemon.animalia.util.Scannable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.Random;

import static net.lemon.animalia.player.network.ClientDiscoveryCache.getDiscoveredCount;

@OnlyIn(Dist.CLIENT)
public class CompendiumHomeScreen extends Screen {

    private static final ResourceLocation FISH_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    public static final ResourceLocation BACK_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_BOTTOM_MARGIN = 36;
    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BAR_WIDTH = 120;
    private static final int BAR_HEIGHT = 8;
    private static final int BAR_BG_COLOR = 0x44000000;
    private static final int FISH_BAR_COLOR = 0xFF1A8A5C;
    private static final int FIELD_BAR_COLOR = 0xFF8A5C1A;

    private final Scannable.AppName app;
    private final Screen parent;

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;
    private LivingEntity featuredEntity;
    private EntityType<?> featuredType;

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
        buildFeaturedEntity();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderHelper(graphics, app, FISH_BACKGROUND, FIELD_BACKGROUND, bgX, bgY, BG_WIDTH, BG_HEIGHT, BORDER);
        graphics.blit(BACK_BUTTON_TEXTURE, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        // Hover highlight on back button
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, 0x44FFFFFF);
        }

        String key = app == Scannable.AppName.FISH ? "gui.animalia.holonet.fish_compendium" : "gui.animalia.holonet.field_guide";
        graphics.drawCenteredString(this.font, Component.translatable(key), bgX + BG_WIDTH / 2, bgY + 30, 0x00FFCC);
        if (featuredEntity != null) {
            if (featuredEntity instanceof GeoEntity geo) {
                geo.getAnimatableInstanceCache().getManagerForId(featuredEntity.getId())
                        .getAnimationControllers().values()
                        .forEach(AnimationController::forceAnimationReset);
            }

            float rotY = (float) Mth.lerp((float) mouseX / this.width, 0, Math.PI);
            float rotZ = (float) Mth.lerp((float) mouseY / this.width, Math.PI, Math.PI + 0.2);
            Quaternionf rotation = new Quaternionf().rotateY(rotY).rotateZ(rotZ);
            int entityX = bgX + BG_WIDTH / 2;
            int entityY = bgY + BG_HEIGHT / 2 + 30;
            int scale = 40;

            if (featuredEntity instanceof Scannable scannable) {
                scale = Math.min(scannable.getScaleforGUI() * 3, 60);
            }

            InventoryScreen.renderEntityInInventory(graphics, entityX, entityY, scale, rotation, null, featuredEntity);
        }

        int totalSpecies = HolonetEntities.getTotalCount(app);
        int discovered = getDiscoveredCount();

        int barX = bgX + (BG_WIDTH - BAR_WIDTH) / 2;
        int barY = bgY + BG_HEIGHT - 70;

        graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BAR_BG_COLOR);

        if (totalSpecies > 0) {
            int fillWidth = (int) ((float) discovered / totalSpecies * BAR_WIDTH);
            int barColor = app == Scannable.AppName.FISH ? FISH_BAR_COLOR : FIELD_BAR_COLOR;
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, barColor);
        }

        String progress = discovered + " / " + totalSpecies;
        graphics.drawCenteredString(this.font, progress, bgX + BG_WIDTH / 2, barY - 12, 0xFFFFFF);

        // --- "Tap anywhere" text ---
        graphics.drawCenteredString(this.font, Component.translatable("gui.animalia.tap"), bgX + BG_WIDTH / 2, barY + BAR_HEIGHT + 8, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    static void renderHelper(GuiGraphics graphics, Scannable.AppName app, ResourceLocation fishBackground, ResourceLocation fieldBackground, int bgX, int bgY, int bgWidth, int bgHeight, ResourceLocation border) {
        ResourceLocation bg = app == Scannable.AppName.FISH ? fishBackground : fieldBackground;
        graphics.blit(bg, bgX, bgY, 0, 0, bgWidth, bgHeight, bgWidth, (int) (bgHeight *1.6));
        graphics.blit(border, bgX, bgY, 0, 0, bgWidth, bgHeight, bgWidth, (int) (bgHeight *1.6));
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
            Minecraft.getInstance().setScreen(new OrderListScreen(app, this));
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

    private void buildFeaturedEntity() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        featuredType = HolonetEntities.getRandom(app, new Random());
        if (featuredType == null) return;

        featuredEntity = (LivingEntity) featuredType.create(mc.level);
        if (featuredEntity == null) return;

        if (featuredEntity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setNoAi(true);
        }

        if (featuredEntity instanceof FishBase fish) {
            fish.setForcedInWater(true);
        }
        featuredEntity.setOnGround(true);

        if (featuredEntity instanceof IsGenetic genetic) {
            genetic.buildTraitsRandom();
        }

        featuredEntity.discard();
    }
}