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
 * Stores Holonet data. unique for each player
 * Key format:
 *   Non-dimorphic: "animalia:dissostichus_eleginoides"
 *   Dimorphic:     "animalia:betta_splendens_1"
 */
public class HolonetCapability {

    private final Set<String> discovered = new HashSet<>();
    private boolean loadedFirstTime = false;

    public boolean discover(ResourceLocation entityType, int gender) {
        return this.discovered.add(entityType.toString() + "_" + gender);
    }

    public boolean isDiscovered(ResourceLocation entityType) {
        String prefix = entityType.toString();
        return this.discovered.stream().anyMatch(key -> key.startsWith(prefix));
    }

    public Set<Integer> getDiscoveredGenders(ResourceLocation entityType) {
        String prefix = entityType.toString() + "_";
        return this.discovered.stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> Integer.parseInt(key.substring(prefix.length())))
                .collect(Collectors.toSet());
    }

    public Set<String> getAll() {
        return this.discovered;
    }

    public void replaceAll(Set<String> entries) {
        this.discovered.clear();
        this.discovered.addAll(entries);
    }

    public void copyFrom(HolonetCapability other) {
        replaceAll(other.getAll());
        this.loadedFirstTime = other.isLoadedFirstTime();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String key : this.discovered) {
            list.add(StringTag.valueOf(key));
        }
        tag.put("discovered", list);
        tag.putBoolean("loadedFirstTime", this.loadedFirstTime);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.discovered.clear();
        ListTag list = tag.getList("discovered", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            this.discovered.add(list.getString(i));
        }
        this.loadedFirstTime = tag.getBoolean("loadedFirstTime");
    }

    public boolean isLoadedFirstTime() {
        return this.loadedFirstTime;
    }

    public void setLoadedFirstTime(boolean loadedFirstTime) {
        this.loadedFirstTime = loadedFirstTime;
    }
}
