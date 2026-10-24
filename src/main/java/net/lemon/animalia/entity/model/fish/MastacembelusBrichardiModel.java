package net.lemon.animalia.entity.model.fish;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.MastacembelusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MastacembelusBrichardiModel extends AnimaliaCustomModel<MastacembelusEntity> {
    public ResourceLocation getModelResource(MastacembelusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyeel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/mastacembelusbrichardi.geo.json");
    }

    public ResourceLocation getTextureResource(MastacembelusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericeel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/mastacembelus_brichardi.png");
    }

    public ResourceLocation getAnimationResource(MastacembelusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyeel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/mastacembelusbrichardi.animation.json");
    }

}
