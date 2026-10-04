package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.PangasianodonEntity;
import net.lemon.animalia.entity.custom.SpinnerSharkEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CarcharhinusBrevipinnaModel extends GeoModel<SpinnerSharkEntity> {
    public ResourceLocation getModelResource(SpinnerSharkEntity object) {
//        if(object.isBaby()) {
//            return new ResourceLocation(Animalia.MODID, "geo/babyshark.geo.json");
//        }
        return new ResourceLocation(Animalia.MODID, "geo/carcharhinus_brevipinna.geo.json");
    }

    public ResourceLocation getTextureResource(SpinnerSharkEntity object) {
//        if(object.isBaby()) {
//            return new ResourceLocation(Animalia.MODID, "textures/entity/babyshark.png");
//        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/carcharhinus_brevipinna.png");
    }

    public ResourceLocation getAnimationResource(SpinnerSharkEntity animatable) {
//        if(animatable.isBaby()) {
//            return new ResourceLocation(Animalia.MODID, "animations/babyshark.animation.json");
//        }
        return new ResourceLocation(Animalia.MODID, "animations/carcharhinus_brevipinna.animation.json");
    }

}
