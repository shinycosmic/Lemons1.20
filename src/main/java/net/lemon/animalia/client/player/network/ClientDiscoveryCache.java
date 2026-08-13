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

    public static void replaceAll(Set<String> entries) {
        discovered.clear();
        discovered.addAll(entries);
    }

    public static void addDiscovery(String key) {
        discovered.add(key);
    }

    public static void clear() {
        discovered.clear();
    }

    public static boolean isDiscovered(ResourceLocation entityType) {
        String prefix = entityType.toString();
        return discovered.stream().anyMatch(key -> key.startsWith(prefix));
    }

    public static Set<Integer> getDiscoveredGenders(ResourceLocation entityType) {
        String prefix = entityType.toString() + "_";
        return discovered.stream().filter(key -> key.startsWith(prefix)).map(key -> Integer.parseInt(key.substring(prefix.length()))).collect(Collectors.toSet());
    }

    public static int getDiscoveredCount() {
        return discovered.size();
    }

    public static Set<String> getAll() {
        return discovered;
    }
}