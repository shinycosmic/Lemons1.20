package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.SynbranchusEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SynbranchusMarmoratusModel extends GeoModel<SynbranchusEntity> {
    public ResourceLocation getModelResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyEel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/synbranchusmarmoratus.geo.json");
    }

    public ResourceLocation getTextureResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyGenericEel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/synbranchusmarmoratus.png");
    }

    public ResourceLocation getAnimationResource(SynbranchusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyEel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/synbranchusmarmoratus.animation.json");
    }
}
