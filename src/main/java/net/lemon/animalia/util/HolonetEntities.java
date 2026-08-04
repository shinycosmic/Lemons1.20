package net.lemon.animalia.util;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

/**
 * Static registry mapping AppName → Order → List of EntityTypes.
 * Populated via registerHolonet() calls from each entity class during commonSetup.
 * Zero runtime cost - no entity instantiation needed.
 */
public class HolonetEntities {

    private static final Map<Scannable.AppName, LinkedHashMap<String, List<EntityType<?>>>> registry = new HashMap<>();

    /** Called from each entity class's static registerHolonet() method. */
    public static void register(RegistryObject<? extends EntityType<?>> type, Scannable.AppName app, String order) {
        registry.computeIfAbsent(app, k -> new LinkedHashMap<>())
                .computeIfAbsent(order, k -> new ArrayList<>())
                .add(type.get());
    }

    /** Get all orders and their species for a given app. */
    public static Map<String, List<EntityType<?>>> getForApp(Scannable.AppName app) {
        return registry.getOrDefault(app, new LinkedHashMap<>());
    }

    /** Get all species in a specific order within an app. */
    public static List<EntityType<?>> getForOrder(Scannable.AppName app, String order) {
        return getForApp(app).getOrDefault(order, Collections.emptyList());
    }

    /** Get a flat list of all entity types for a given app. */
    public static List<EntityType<?>> getAllForApp(Scannable.AppName app) {
        List<EntityType<?>> all = new ArrayList<>();
        getForApp(app).values().forEach(all::addAll);
        return all;
    }

    /** Total number of species (entity types) in an app. */
    public static int getTotalCount(Scannable.AppName app) {
        return getAllForApp(app).size();
    }

    /** Get a random entity type from the given app. */
    public static EntityType<?> getRandom(Scannable.AppName app, Random random) {
        List<EntityType<?>> all = getAllForApp(app);
        if (all.isEmpty()) return null;
        return all.get(random.nextInt(all.size()));
    }
}