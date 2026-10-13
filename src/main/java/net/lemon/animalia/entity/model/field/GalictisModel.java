package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrossarchusEntity;
import net.lemon.animalia.entity.custom.GalictisEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class GalictisModel extends AnimaliaCustomModel<GalictisEntity> {
    public ResourceLocation getModelResource(GalictisEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymongoose.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/galictis_vittata.geo.json");
    }

    public ResourceLocation getTextureResource(GalictisEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babymongoose.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(GalictisEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymongoose.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/galictis.animation.json");
    }

}
