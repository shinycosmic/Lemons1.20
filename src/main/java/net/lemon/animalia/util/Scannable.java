package net.lemon.animalia.util;

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
     * Trigger -> Interface
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


}
