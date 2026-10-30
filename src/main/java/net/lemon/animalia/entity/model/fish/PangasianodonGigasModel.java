package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.PangasianodonEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PangasianodonGigasModel extends AnimaliaCustomModel<PangasianodonEntity> {
    public ResourceLocation getModelResource(PangasianodonEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babycatfish.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/pangasianodongigas.geo.json");
    }

    public ResourceLocation getTextureResource(PangasianodonEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericcatfish.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/pangasianodon_gigas.png");
    }

    public ResourceLocation getAnimationResource(PangasianodonEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babycatfish.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/pangasianodongigas.animation.json");
    }
}
