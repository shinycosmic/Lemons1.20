package net.lemon.animalia.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.client.player.network.ClientDiscoveryCache;
import net.lemon.animalia.util.IsGenetic;
import net.lemon.animalia.util.Scannable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class CreatureDetailScreen extends Screen {

    private static final ResourceLocation FISH_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    private static final ResourceLocation FIELD_BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    private static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    private static final ResourceLocation BACK_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");

    private static final ResourceLocation MALE_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/male_button.png");
    private static final ResourceLocation FEMALE_BUTTON_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/female_button.png");
    private static final ResourceLocation MALE_PRESSED_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/male_button_pressed.png");
    private static final ResourceLocation FEMALE_PRESSED_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/female_button_pressed.png");
    private static final ResourceLocation GENERIC_LOCKED_TEXTURE = new ResourceLocation(Animalia.MODID, "textures/gui/generic_locked.png");

    private static final int BG_WIDTH = 390;
    private static final int BG_HEIGHT = 245;
    private static final int BACK_BUTTON_SIZE = 16;
    private static final int BACK_BUTTON_BOTTOM_MARGIN = 38;
    private static final int BACK_BUTTON_MARGIN_LEFT = 35;
    private static final int BACK_BUTTON_MARGIN_TOP = 35;
    private static final int MODEL_AREA_WIDTH = 160;
    private static final int INFO_LEFT_OFFSET = 175;
    private static final int INFO_TOP_OFFSET = 40;
    private static final int INFO_WIDTH = 180;
    private static final int LINE_SPACING = 12;
    private static final int GENDER_BTN_SIZE = 16;
    private static final int GENDER_BTN_SPACING = 4;
    private static final int GENDER_BTN_TOP = 35;
    private static final int GENDER_BTN_LEFT = 15;

    private final Scannable.AppName app;
    private final EntityType<?> entityType;
    private final Screen parent;

    private int bgX;
    private int bgY;
    private int backBtnX;
    private int backBtnY;
    private float baseRotation = 0;
    private float baseRotationZ = 0;
    private boolean isDragging = false;
    private double lastDragX = 0;

    private LivingEntity displayEntity;
    private int currentGender = 0;
    private boolean hasDimorphism = false;
    private Set<Integer> discoveredGenders;

    public CreatureDetailScreen(Scannable.AppName app, EntityType<?> entityType, Screen parent) {
        super(Component.translatable("gui.animalia.holonet.detail"));
        this.app = app;
        this.entityType = entityType;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        bgX = (this.width - BG_WIDTH) / 2;
        bgY = (this.height - BG_HEIGHT) / 2;
        backBtnX = bgX + BACK_BUTTON_MARGIN_LEFT;
        backBtnY = bgY + BACK_BUTTON_MARGIN_TOP;

        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        discoveredGenders = ClientDiscoveryCache.getDiscoveredGenders(entityId);

        if (discoveredGenders.contains(1)) {
            currentGender = 1;
        } else if (discoveredGenders.contains(0)) {
            currentGender = 0;
        }

        buildDisplayEntity();
    }

    private void buildDisplayEntity() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        displayEntity = (LivingEntity) entityType.create(mc.level);
        if (displayEntity == null) return;

        if (displayEntity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setNoAi(true);
        }
        if (displayEntity instanceof FishBase fish) {
            fish.setForcedInWater(true);
        }
        displayEntity.setOnGround(true);

        if (displayEntity instanceof AnimaliaBreedableWater abw) {
            abw.setGender(currentGender);
        }
        if (displayEntity instanceof Scannable scannable) {
            hasDimorphism = scannable.hasDimorphism();
        }
        if (displayEntity instanceof IsGenetic genetic) {
            genetic.buildTraitsRandom();
        }

        displayEntity.discard();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CompendiumHomeScreen.renderHelper(graphics, app, FISH_BACKGROUND, FIELD_BACKGROUND, bgX, bgY, BG_WIDTH, BG_HEIGHT, BORDER);

        // 3D model
        if (displayEntity != null) {
            if (displayEntity instanceof GeoEntity geo) {
                geo.getAnimatableInstanceCache().getManagerForId(displayEntity.getId())
                        .getAnimationControllers().values()
                        .forEach(AnimationController::forceAnimationReset);
            }

            int entityX = bgX + MODEL_AREA_WIDTH / 2 + 40;
            int entityY = bgY + BG_HEIGHT / 2 + 40;
            int scale = ((Scannable) displayEntity).getScaleforDetailGUI();

            float rotY = (float) Mth.lerp((float) mouseX / this.width, 0, Math.PI *2f);
            float rotZ = (float) Mth.lerp((float) mouseY / this.height, Math.PI, Math.PI) + baseRotation;
            Quaternionf rotation = new Quaternionf().rotateY(rotY).rotateZ(rotZ);

            Lighting.setupForEntityInInventory();
            InventoryScreen.renderEntityInInventory(graphics, entityX, entityY, scale, rotation, null, displayEntity);
        }
        if (hasDimorphism) {
            int btnX = bgX + GENDER_BTN_LEFT;
            int maleBtnY = bgY + GENDER_BTN_TOP;
            int femaleBtnY = maleBtnY + GENDER_BTN_SIZE + GENDER_BTN_SPACING;

            boolean maleDiscovered = discoveredGenders.contains(1);
            boolean femaleDiscovered = discoveredGenders.contains(0);

            ResourceLocation maleTexture = !maleDiscovered ? GENERIC_LOCKED_TEXTURE
                    : currentGender == 1 ? MALE_PRESSED_TEXTURE : MALE_BUTTON_TEXTURE;
            graphics.blit(maleTexture, btnX, maleBtnY, 0, 0, GENDER_BTN_SIZE, GENDER_BTN_SIZE, GENDER_BTN_SIZE, GENDER_BTN_SIZE);
            if (maleDiscovered && isOverGenderButton(mouseX, mouseY, btnX, maleBtnY)) {
                graphics.fill(btnX, maleBtnY, btnX + GENDER_BTN_SIZE, maleBtnY + GENDER_BTN_SIZE, 0x44FFFFFF);
            }

            ResourceLocation femaleTexture = !femaleDiscovered ? GENERIC_LOCKED_TEXTURE
                    : currentGender == 0 ? FEMALE_PRESSED_TEXTURE : FEMALE_BUTTON_TEXTURE;
            graphics.blit(femaleTexture, btnX, femaleBtnY, 0, 0, GENDER_BTN_SIZE, GENDER_BTN_SIZE, GENDER_BTN_SIZE, GENDER_BTN_SIZE);
            if (femaleDiscovered && isOverGenderButton(mouseX, mouseY, btnX, femaleBtnY)) {
                graphics.fill(btnX, femaleBtnY, btnX + GENDER_BTN_SIZE, femaleBtnY + GENDER_BTN_SIZE, 0x44FFFFFF);
            }
        }

        // Common name (top center)
        if (displayEntity != null) {
            graphics.drawCenteredString(this.font, entityType.getDescription().copy().withStyle(ChatFormatting.BOLD),
                    bgX + BG_WIDTH / 2, bgY + 35, 0xFFFFFF);
        }

        // Info panel (right side)
        int infoX = bgX + INFO_LEFT_OFFSET;
        int infoY = bgY + INFO_TOP_OFFSET + 10;

        if (displayEntity instanceof AnimaliaBreedableWater abw && displayEntity instanceof Scannable scannable) {
            graphics.drawString(this.font, Component.literal("> ").append(scannable.getOrder()), infoX, infoY, 0xFFFFFF, true);
            infoY += LINE_SPACING;
            graphics.drawString(this.font, Component.literal("> ").append(scannable.getFamily()), infoX, infoY, 0xFFFFFF, true);
            infoY += LINE_SPACING;
            graphics.drawString(this.font, Component.literal("> ").append(
                            Component.literal(abw.getScientificName()).withStyle(ChatFormatting.ITALIC)),
                    infoX, infoY, 0xFFFFFF, true);
            infoY += LINE_SPACING + 8;

            // Breeding item
            ItemStack foodItem = new ItemStack(abw.getBreedingItem());
            if (!foodItem.isEmpty()) {
                graphics.renderItem(foodItem, infoX, infoY - 2);
                graphics.drawString(this.font, foodItem.getHoverName(), infoX + 18, infoY + 2, 0xFFFFFF, true);
                infoY += LINE_SPACING + 10;
            }

            // Description
            List<FormattedCharSequence> wrappedLines = this.font.split(scannable.getTrivia(), INFO_WIDTH);
            for (FormattedCharSequence line : wrappedLines) {
                graphics.drawString(this.font, line, infoX, infoY, 0xFFFFFF, true);
                infoY += LINE_SPACING;
            }
        }

        graphics.blit(BACK_BUTTON_TEXTURE, backBtnX, backBtnY, 0, 0, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        if (isOverBackButton(mouseX, mouseY)) {
            graphics.fill(backBtnX - 1, backBtnY - 1, backBtnX + BACK_BUTTON_SIZE + 1, backBtnY + BACK_BUTTON_SIZE + 1, 0x44FFFFFF);
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

            if (hasDimorphism) {
                int btnX = bgX + GENDER_BTN_LEFT;
                int maleBtnY = bgY + GENDER_BTN_TOP;
                int femaleBtnY = maleBtnY + GENDER_BTN_SIZE + GENDER_BTN_SPACING;

                if (discoveredGenders.contains(1) && isOverGenderButton((int) mouseX, (int) mouseY, btnX, maleBtnY)) {
                    if (currentGender != 1) {
                        currentGender = 1;
                        buildDisplayEntity();
                    }
                    return true;
                }

                if (discoveredGenders.contains(0) && isOverGenderButton((int) mouseX, (int) mouseY, btnX, femaleBtnY)) {
                    if (currentGender != 0) {
                        currentGender = 0;
                        buildDisplayEntity();
                    }
                    return true;
                }
            }
        }

        isDragging = true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isDragging && button == 0) {
            baseRotation += (float) (dragX * 0.02f);
            baseRotation += (float) (dragY * 0.02f);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0) { isDragging = false; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isOverBackButton(int mouseX, int mouseY) {
        return mouseX >= backBtnX && mouseX <= backBtnX + BACK_BUTTON_SIZE
                && mouseY >= backBtnY && mouseY <= backBtnY + BACK_BUTTON_SIZE;
    }

    private boolean isOverGenderButton(int mouseX, int mouseY, int btnX, int btnY) {
        return mouseX >= btnX && mouseX <= btnX + GENDER_BTN_SIZE
                && mouseY >= btnY && mouseY <= btnY + GENDER_BTN_SIZE;
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