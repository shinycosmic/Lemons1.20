package net.lemon.animalia.registry;

import net.lemon.animalia.entity.custom.*;

public class HolonetSetup {

    public static void init() {
        //Fish
        ToothfishEntity.registerHolonet();
        CongolliEntity.registerHolonet();
        BettaEntity.registerHolonet();
        SynbranchusEntity.registerHolonet();
        MastacembelusEntity.registerHolonet();
        RakthamichthysEntity.registerHolonet();
        RoosterfishEntity.registerHolonet();
        ToxotesEntity.registerHolonet();
        PogonophryneEntity.registerHolonet();
        ChaenocephalusEntity.registerHolonet();
        RegSchoolingEntity.registerHolonet();
        GrazeSchoolingEntity.registerHolonet();
        PangasianodonEntity.registerHolonet();
        HydrocynusEntity.registerHolonet();
        IndostomusEntity.registerHolonet();
        CavefishEntity.registerHolonet();

        //Field
        CrayfishEntity.registerHolonet();
        PangolinEntity.registerHolonet();
    }
}