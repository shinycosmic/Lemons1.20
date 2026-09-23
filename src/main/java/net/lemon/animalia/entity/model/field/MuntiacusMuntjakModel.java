package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.MuntiacusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MuntiacusMuntjakModel extends AnimaliaCustomModel<MuntiacusEntity> {
    public ResourceLocation getModelResource(MuntiacusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymuntjac.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/muntiacus_muntjak.geo.json");
    }

    public ResourceLocation getTextureResource(MuntiacusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babymuntjac.png");
        }
        if(object.getGender() == 0) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/muntiacus_muntjak_female.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/muntiacus_muntjak_male.png");
    }

    public ResourceLocation getAnimationResource(MuntiacusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymuntjac.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/muntiacus_muntjak.animation.json");
    }
}
