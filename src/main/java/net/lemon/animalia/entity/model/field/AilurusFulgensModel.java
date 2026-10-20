package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.AilurusFulgensEntity;
import net.lemon.animalia.entity.custom.CrossarchusEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class AilurusFulgensModel extends GeoModel<AilurusFulgensEntity> {
    public ResourceLocation getModelResource(AilurusFulgensEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymongoose.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/ailurus_fulgens.geo.json");
    }

    public ResourceLocation getTextureResource(AilurusFulgensEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyredpanda.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(AilurusFulgensEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymongoose.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/ailurus_fulgens.animation.json");
    }

}
