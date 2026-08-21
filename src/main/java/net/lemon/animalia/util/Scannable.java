package net.lemon.animalia.util;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

public interface Scannable {
    /***
     * Scannable. Animals implementing this are scannable and their pages can be pulled up by the Holonet
     * This interface serves to expose animal information to the Holonet item.
     * Some information is already exposed by the animal class
     * Breakdown:
     * Common Name -> Entity
     * Scientific Name -> Entity
     * Family Name -> Interface
     * Breeding Item -> Entity
     * Ingame Trivia -> Interface
     * App -> Interface
     * Trigger -> Entity
     * Order Name -> Interface
     */

    enum AppName {
        FISH,
        FIELD,
    }
    //Might use this in the future but doesn't do anything right now
    AppName getApp();

    Component getTrivia();

    Component getFamily();

    Component getOrder();

    String getScientificName();

    static String getScientificName(EntityType<?> type) {
        String key = type.getDescriptionId() + ".scientific";
        return Language.getInstance().has(key) ? Component.translatable(key).getString() : "";
    }

    /***
     * the registry method, this is called to make sure Holonet recognizes this creature
     * Every Scannable creature must also have this method
     */
//    static void registerHolonet() {
//        return;
//    }

    default Quaternionf getRotforGUI() {
        return new Quaternionf().rotateZ((float) Math.PI).rotateY((float) Math.toRadians(130));
    }
    default int getScaleforGUI() {
        return 50;
    }

    default int getScaleforDetailGUI() {
        if(this instanceof LivingEntity living) {
            float maxDim = Math.max(living.getBbHeight(), living.getBbWidth());
            return (int) (50f / maxDim);
        }
        return 50;
    }

    default int getYOffsetForGUI() {
        return 0;
    }

    default boolean hasDimorphism() { return false; }

    default int getXOffsetForGUI() { return 0; }
}
