package net.lemon.animalia.util;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class HolonetEntities {

    private static final Map<Scannable.AppName, LinkedHashMap<String, List<EntityType<?>>>> registry = new HashMap<>();

    public static void register(RegistryObject<? extends EntityType<?>> type, Scannable.AppName app, String order) {
        registry.computeIfAbsent(app, k -> new LinkedHashMap<>())
                .computeIfAbsent(order, k -> new ArrayList<>())
                .add(type.get());
    }

    public static Map<String, List<EntityType<?>>> getForApp(Scannable.AppName app) {
        return registry.getOrDefault(app, new LinkedHashMap<>());
    }

    public static List<EntityType<?>> getForOrder(Scannable.AppName app, String order) {
        return getForApp(app).getOrDefault(order, Collections.emptyList());
    }

    public static List<EntityType<?>> getAllForApp(Scannable.AppName app) {
        List<EntityType<?>> all = new ArrayList<>();
        getForApp(app).values().forEach(all::addAll);
        return all;
    }

    public static int getTotalCount(Scannable.AppName app) {
        return getAllForApp(app).size();
    }

    public static EntityType<?> getRandom(Scannable.AppName app, Random random) {
        List<EntityType<?>> all = getAllForApp(app);
        if (all.isEmpty()) return null;
        return all.get(random.nextInt(all.size()));
    }
}