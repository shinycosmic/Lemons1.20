package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CongolliEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PseudaphritisUrvilliiModel extends GeoModel<CongolliEntity> {
    public ResourceLocation getModelResource(CongolliEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/notothenbaby1.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/pseudaphritis.geo.json");
    }

    public ResourceLocation getTextureResource(CongolliEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/notothenbaby1.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/pseudaphritisurvillii.png");
    }

    public ResourceLocation getAnimationResource(CongolliEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/notothenbaby1.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/pseudaphritis.animation.json");
    }
}
