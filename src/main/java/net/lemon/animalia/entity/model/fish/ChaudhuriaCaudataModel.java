package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.SynbranchusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChaudhuriaCaudataModel extends GeoModel<SynbranchusEntity> {
    public ResourceLocation getModelResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyeel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/chaudhuriacaudata.geo.json");
    }

    public ResourceLocation getTextureResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericeel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/chaudhuria_caudata.png");
    }

    public ResourceLocation getAnimationResource(SynbranchusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyeel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/chaudhuriacaudata.animation.json");
    }

}
