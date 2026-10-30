package net.lemon.animalia.entity.model.fish;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.HydrocynusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HydrocynusGoliathModel extends AnimaliaCustomModel<HydrocynusEntity> {
    public ResourceLocation getModelResource(HydrocynusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babysmallfish.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/hydrocynusgoliath.geo.json");
    }

    public ResourceLocation getTextureResource(HydrocynusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericsmallfish.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/hydrocynus_goliath.png");
    }

    public ResourceLocation getAnimationResource(HydrocynusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babysmallfish.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/hydrocynusgoliath.animation.json");
    }
}
