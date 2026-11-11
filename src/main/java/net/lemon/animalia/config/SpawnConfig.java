package net.lemon.animalia.config;

import net.lemon.animalia.registry.spawning.ModSpawns;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class SpawnConfig {
    public static final String FILE_NAME = "wildlife_explorers_spawns.toml";
    public static final ForgeConfigSpec SPEC;

    private static final Map<String, ForgeConfigSpec.ConfigValue<Integer>> WEIGHTS = new HashMap<>();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Spawn weights for each animal. Larger numbers increases spawn chance. Use weight of 0 to disable spawns for the animal. Valid range: 0-1000").push("weights");
        for (ModSpawns.SpawnEntry entry : ModSpawns.ENTRIES) {
            WEIGHTS.put(entry.name(), builder.define(entry.name(), entry.weight(), value -> value instanceof Integer i && i >= 0 && i <= 1000));
        }
        builder.pop();

        SPEC = builder.build();
    }

    public static int weight(String name, int fallback) {
        ForgeConfigSpec.ConfigValue<Integer> value = WEIGHTS.get(name);
        if (value == null) {
            return fallback;
        }
        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
}