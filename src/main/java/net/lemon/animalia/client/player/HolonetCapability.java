package net.lemon.animalia.client.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores per-player Holonet discovery data.
 * Key format:
 *   Non-dimorphic: "animalia:dissostichus_eleginoides"
 *   Dimorphic:     "animalia:betta_splendens_1" (appended gender int)
 */
public class HolonetCapability {

    private final Set<String> discovered = new HashSet<>();
    private boolean loadedFirstTime = false;

    /**
     * Discovers a species+gender combo. Returns true if this is a NEW discovery.
     */
    public boolean discover(ResourceLocation entityType, int gender) {
        return discovered.add(entityType.toString() + "_" + gender);
    }

    /**
     * Has this species been discovered in ANY gender?
     */
    public boolean isDiscovered(ResourceLocation entityType) {
        String prefix = entityType.toString();
        return discovered.stream().anyMatch(key -> key.startsWith(prefix));
    }

    /**
     * Which genders have been discovered for this species?
     */
    public Set<Integer> getDiscoveredGenders(ResourceLocation entityType) {
        String prefix = entityType.toString() + "_";
        return discovered.stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> Integer.parseInt(key.substring(prefix.length())))
                .collect(Collectors.toSet());
    }

    public Set<String> getAll() {
        return discovered;
    }

    public void replaceAll(Set<String> entries) {
        discovered.clear();
        discovered.addAll(entries);
    }

    public void copyFrom(HolonetCapability other) {
        replaceAll(other.getAll());
        this.loadedFirstTime = other.isLoadedFirstTime();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String key : discovered) {
            list.add(StringTag.valueOf(key));
        }
        tag.put("discovered", list);
        setLoadedFirstTime(tag.getBoolean("loadedFirstTime"));
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        discovered.clear();
        ListTag list = tag.getList("discovered", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            discovered.add(list.getString(i));
        }
    }

    public boolean isLoadedFirstTime() {
        return loadedFirstTime;
    }

    public void setLoadedFirstTime(boolean loadedFirstTime) {
        this.loadedFirstTime = loadedFirstTime;
    }
}
