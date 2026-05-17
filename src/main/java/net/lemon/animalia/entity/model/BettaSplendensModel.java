package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BettaSplendensModel extends GeoModel<BettaEntity> {
    public ResourceLocation getModelResource(BettaEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/bettasplendens.geo.json");
    }

    public ResourceLocation getTextureResource(BettaEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/betta/betta_base.png");
    }

    public ResourceLocation getAnimationResource(BettaEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/betta.animation.json");
    }
}
