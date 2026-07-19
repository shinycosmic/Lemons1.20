package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.lemon.animalia.entity.custom.PogonophryneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PogonophryneMarmorataModel extends GeoModel<PogonophryneEntity> {
    public ResourceLocation getModelResource(PogonophryneEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/pogonophrynemarmorata.geo.json");
    }

    public ResourceLocation getTextureResource(PogonophryneEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/pogonophrynemarmorata.png");
    }

    public ResourceLocation getAnimationResource(PogonophryneEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/pogonophrynemarmorata.animation.json");
    }
}
