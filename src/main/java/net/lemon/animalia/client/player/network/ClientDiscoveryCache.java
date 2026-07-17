package net.lemon.animalia.client.player.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientDiscoveryCache {

    private static final Set<String> discovered = new HashSet<>();

    /** Replace the entire cache (used on login/dimension change). */
    public static void replaceAll(Set<String> entries) {
        discovered.clear();
        discovered.addAll(entries);
    }

    /** Add a single discovery key (used on incremental discovery). */
    public static void addDiscovery(String key) {
        discovered.add(key);
    }

    /** Clear all discoveries (e.g. on disconnect). */
    public static void clear() {
        discovered.clear();
    }

    /** Has this species been discovered in ANY gender? */
    public static boolean isDiscovered(ResourceLocation entityType) {
        String prefix = entityType.toString();
        return discovered.stream().anyMatch(key -> key.startsWith(prefix));
    }

    /** Which genders have been discovered for this species? */
    public static Set<Integer> getDiscoveredGenders(ResourceLocation entityType) {
        String prefix = entityType.toString() + "_";
        return discovered.stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> Integer.parseInt(key.substring(prefix.length())))
                .collect(Collectors.toSet());
    }

    /** Get the total number of unique discovery keys. */
    public static int getDiscoveredCount() {
        return discovered.size();
    }

    /** Get all raw discovery keys (for debug purposes). */
    public static Set<String> getAll() {
        return discovered;
    }
}