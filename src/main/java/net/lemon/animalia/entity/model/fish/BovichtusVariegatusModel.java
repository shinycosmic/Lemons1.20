package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BovichtusEntity;
import net.lemon.animalia.entity.custom.ChaenocephalusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BovichtusVariegatusModel extends GeoModel<BovichtusEntity> {
    public ResourceLocation getModelResource(BovichtusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/bovichtus_variegatus.geo.json");
    }

    public ResourceLocation getTextureResource(BovichtusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/bovichtus_variegatus.png");
    }

    public ResourceLocation getAnimationResource(BovichtusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/bovichtus_variegatus.animation.json");
    }
}
