package net.lemon.animalia.util;
import net.minecraft.network.chat.Component;

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

    /**
     * Returns the App this animal will be sorted into
     * @return
     */
    AppName getApp();

    /***
     *  getTrivia, return a translatable component
     */
    Component getTrivia();

    /***
     * getFamily, return a translatable component
     */
    Component getFamily();

    /***
     * getOrder, return a translatable component
     */
    Component getOrder();

    /***
     * the registry method, this is called to make sure Holonet recognizes this creature
     * Every Scannable creature must also have this method
     */
//    static void registerHolonet() {
//        return;
//    }
}
