package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.world.entity.MobCategory;

public class ModMobCategories {
    public static final int FISH_CAP = 36;
    public static final int INVERTEBRATE_CAP = 16;
    public static final int LAND_CAP = 28;
    public static final int FLIER_CAP = 8;
    public static final int WATER_STRUCTURE_CAP = 6;
    public static final int LAND_STRUCTURE_CAP = 6;

    public static final MobCategory ANIMALIA_FISH = MobCategory.create(
            "ANIMALIA_FISH", Animalia.MODID + ":fish", FISH_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_INVERTEBRATE = MobCategory.create(
            "ANIMALIA_INVERTEBRATE", Animalia.MODID + ":invertebrate", INVERTEBRATE_CAP, true, false, 64);
    public static final MobCategory ANIMALIA_LAND = MobCategory.create(
            "ANIMALIA_LAND", Animalia.MODID + ":land", LAND_CAP, true, true, 128);
    public static final MobCategory ANIMALIA_FLIER = MobCategory.create(
            "ANIMALIA_FLIER", Animalia.MODID + ":flier", FLIER_CAP, true, true, 128);
    public static final MobCategory ANIMALIA_WATER_STRUCTURE = MobCategory.create(
            "ANIMALIA_WATER_STRUCTURE", Animalia.MODID + ":water_struct", WATER_STRUCTURE_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_LAND_STRUCTURE = MobCategory.create(
            "ANIMALIA_LAND_STRUCTURE", Animalia.MODID + ":land_struct", LAND_STRUCTURE_CAP, true, true, 128);

    public static void bootstrap() {
    }
}