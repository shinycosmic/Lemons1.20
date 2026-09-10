package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrossarchusEntity;
import net.lemon.animalia.entity.custom.HyemoschusEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CrossarchusModel extends GeoModel<CrossarchusEntity> {
    public ResourceLocation getModelResource(CrossarchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymongoose.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/crossarchus.geo.json");
    }

    public ResourceLocation getTextureResource(CrossarchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babymongoose.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(CrossarchusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymongoose.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/crossarchus.animation.json");
    }

}
