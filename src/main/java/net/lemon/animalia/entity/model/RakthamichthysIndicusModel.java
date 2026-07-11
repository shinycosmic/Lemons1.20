package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.custom.RakthamichthysEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RakthamichthysIndicusModel extends GeoModel<RakthamichthysEntity> {
    public ResourceLocation getModelResource(RakthamichthysEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyeel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/rakthamichthysindicus.geo.json");
    }

    public ResourceLocation getTextureResource(RakthamichthysEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericeel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/rakthamichthysindicus.png");
    }

    public ResourceLocation getAnimationResource(RakthamichthysEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyeel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/rakthamichthysindicus.animation.json");
    }

}
