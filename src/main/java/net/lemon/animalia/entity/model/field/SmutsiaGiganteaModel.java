package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrayfishEntity;
import net.lemon.animalia.entity.custom.PangolinEntity;
import net.lemon.animalia.entity.custom.RoosterfishEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class SmutsiaGiganteaModel extends GeoModel<PangolinEntity> {
    public ResourceLocation getModelResource(PangolinEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babypangolin.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/smutsiagigantea.geo.json");
    }

    public ResourceLocation getTextureResource(PangolinEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babypangolin.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/smutsia_gigantea.png");
    }

    public ResourceLocation getAnimationResource(PangolinEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babypangolin.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/smutsiagigantea.animation.json");
    }
}
