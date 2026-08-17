package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ChaenocephalusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChaenocephalusAceratusModel extends GeoModel<ChaenocephalusEntity> {
    public ResourceLocation getModelResource(ChaenocephalusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/chaenocephalusaceratus.geo.json");
    }

    public ResourceLocation getTextureResource(ChaenocephalusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/chaenocephalus_aceratus.png");
    }

    public ResourceLocation getAnimationResource(ChaenocephalusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/chaenocephalusaceratus.animation.json");
    }
}
