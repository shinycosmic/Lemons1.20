package net.lemon.animalia.util;

import net.minecraft.network.chat.Component;

import java.util.List;

public class HolonetTutorials {

    //register new tutorial pages below
    private static final List<String> TUTORIALS = List.of(
            "scanning",
            "gene_inheritance",
            "filter_trap"
    );

    public static List<String> getAll() {
        return TUTORIALS;
    }

    public static Component getTitle(String id) {
        return Component.translatable("gui.animalia.holonet.tutorial." + id);
    }

    public static Component getBody(String id) {
        return Component.translatable("gui.animalia.holonet.tutorial." + id + ".body");
    }
}