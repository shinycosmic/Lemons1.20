package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PercophisBrasiliensisModel extends GeoModel<ToothfishEntity> {
    public ResourceLocation getModelResource(ToothfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/notothenbaby1.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/percophisbrasiliensis.geo.json");
    }

    public ResourceLocation getTextureResource(ToothfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/notothenbaby1.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/percophisbrasiliensis.png");
    }

    public ResourceLocation getAnimationResource(ToothfishEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/notothenbaby1.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/percophis.animation.json");
    }
}
