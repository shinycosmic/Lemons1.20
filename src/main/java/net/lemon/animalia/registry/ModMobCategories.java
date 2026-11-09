package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.world.entity.MobCategory;

public class ModMobCategories {
    public static final int FISH_CAP = 28;
    public static final int FRESHWATER_FISH_CAP = 12;
    public static final int JELLY_CAP = 6;
    public static final int SHARK_CAP = 4;
    public static final int BOTTOM_DWELLER_FISH_CAP = 12;
    public static final int AQUATIC_CRITTER = 6;
    public static final int INVERTEBRATE_CAP = 8;
    public static final int CEPHALOPOD_CAP = 6;
    public static final int WATER_INVERTEBRATE_CAP = 8;
    public static final int LAND_CAP = 24;
    public static final int SEMIAQUATIC_CAP = 10;
    public static final int FLIER_CAP = 8;
    public static final int WATER_STRUCTURE_CAP = 6;
    public static final int LAND_STRUCTURE_CAP = 6;

    public static final MobCategory ANIMALIA_FISH = MobCategory.create(
            "ANIMALIA_FISH", Animalia.MODID + ":fish", FISH_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_FRESHWATER_FISH = MobCategory.create(
            "ANIMALIA_FRESHWATER_FISH", Animalia.MODID + ":freshwater_fish", FRESHWATER_FISH_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_JELLY = MobCategory.create(
            "ANIMALIA_JELLY", Animalia.MODID + ":jelly", JELLY_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_SHARK = MobCategory.create(
            "ANIMALIA_SHARK", Animalia.MODID + ":shark", SHARK_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_BOTTOM_DWELLER_FISH = MobCategory.create(
            "ANIMALIA_BOTTOM_DWELLER_FISH", Animalia.MODID + ":bottom_dweller", BOTTOM_DWELLER_FISH_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_AQUATIC_CRITTER = MobCategory.create(
            "ANIMALIA_AQUATIC_CRITTER", Animalia.MODID + ":aquatic_critter", AQUATIC_CRITTER, true, false, 84);
    public static final MobCategory ANIMALIA_INVERTEBRATE = MobCategory.create(
            "ANIMALIA_INVERTEBRATE", Animalia.MODID + ":invertebrate", INVERTEBRATE_CAP, true, false, 64);
    public static final MobCategory ANIMALIA_CEPHALOPOD = MobCategory.create(
            "ANIMALIA_CEPHALOPOD", Animalia.MODID + ":cephalopod", CEPHALOPOD_CAP, true, false, 64);
    public static final MobCategory ANIMALIA_WATER_INVERTEBRATE = MobCategory.create(
            "ANIMALIA_WATER_INVERTEBRATE", Animalia.MODID + ":water_invertebrate", WATER_INVERTEBRATE_CAP, true, false, 64);
    public static final MobCategory ANIMALIA_LAND = MobCategory.create(
            "ANIMALIA_LAND", Animalia.MODID + ":land", LAND_CAP, true, true, 128);
    public static final MobCategory ANIMALIA_SEMIAQUATIC = MobCategory.create(
            "ANIMALIA_SEMIAQUATIC", Animalia.MODID + ":land", SEMIAQUATIC_CAP, true, true, 128);
    public static final MobCategory ANIMALIA_FLIER = MobCategory.create(
            "ANIMALIA_FLIER", Animalia.MODID + ":flier", FLIER_CAP, true, true, 128);
    public static final MobCategory ANIMALIA_WATER_STRUCTURE = MobCategory.create(
            "ANIMALIA_WATER_STRUCTURE", Animalia.MODID + ":water_struct", WATER_STRUCTURE_CAP, true, false, 84);
    public static final MobCategory ANIMALIA_LAND_STRUCTURE = MobCategory.create(
            "ANIMALIA_LAND_STRUCTURE", Animalia.MODID + ":land_struct", LAND_STRUCTURE_CAP, true, true, 128);

    public static void bootstrap() {
    }
}