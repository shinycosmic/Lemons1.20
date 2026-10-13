package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ProteusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;

public class ProteusAnguinusModel extends AnimaliaCustomModel<ProteusEntity> {
    @Override
    public ResourceLocation getModelResource(ProteusEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "geo/proteus_anguinus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ProteusEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/proteus_anguinus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ProteusEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/proteus_anguinus.animation.json");
    }
}
