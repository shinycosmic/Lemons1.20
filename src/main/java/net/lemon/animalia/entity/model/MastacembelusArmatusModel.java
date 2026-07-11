package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MastacembelusArmatusModel extends GeoModel<MastacembelusEntity> {
    public ResourceLocation getModelResource(MastacembelusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyeel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/mastacembelusarmatus.geo.json");
    }

    public ResourceLocation getTextureResource(MastacembelusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babypeacockeel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/mastacembelusarmatus.png");
    }

    public ResourceLocation getAnimationResource(MastacembelusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyeel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/mastacembelusarmatus.animation.json");
    }

}
