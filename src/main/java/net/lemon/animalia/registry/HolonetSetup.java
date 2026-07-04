package net.lemon.animalia.registry;

import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
// import net.lemon.animalia.entity.custom.CongolliEntity;
// import net.lemon.animalia.entity.custom.BettaEntity;

/**
 * Central place to register all entities into the Holonet discovery system.
 * Called once from Animalia.commonSetup().
 * Add new entity classes here as they implement Scannable.
 */
public class HolonetSetup {

    public static void init() {
        ToothfishEntity.registerHolonet();
         CongolliEntity.registerHolonet();
         BettaEntity.registerHolonet();
    }
}