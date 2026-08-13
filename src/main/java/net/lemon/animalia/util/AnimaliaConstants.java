package net.lemon.animalia.util;

import net.lemon.animalia.Animalia;
import net.minecraft.resources.ResourceLocation;

public class AnimaliaConstants {
    //some constants so I only need to define shared textures once
    public static final ResourceLocation FISH_BG = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_fish_transparent.png");
    public static final ResourceLocation FIELD_BG = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_field_transparent.png");
    public static final ResourceLocation BORDER = new ResourceLocation(Animalia.MODID, "textures/gui/holonet.png");
    public static final ResourceLocation BACK_BUTTON = new ResourceLocation(Animalia.MODID, "textures/gui/back_button.png");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Animalia.MODID, "textures/gui/holonet_transparent.png");

    //Sizing for the Holonet and Back button location. shared among all the screens
    public static final int BG_WIDTH = 390;
    public static final int BG_HEIGHT = 245;
    public static final int BACK_BUTTON_SIZE = 16;
    public static final int BACK_BUTTON_MARGIN_LEFT = 38;
    public static final int BACK_BUTTON_MARGIN_TOP = 35;
}
