package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChileanSeaBassModel extends GeoModel<ToothfishEntity> {
    public ResourceLocation getModelResource(ToothfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/chileanseabass.geo.json");
    }

    public ResourceLocation getTextureResource(ToothfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/chileanseabass.png");
    }

    public ResourceLocation getAnimationResource(ToothfishEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/notothen.animation.json");
    }
}
