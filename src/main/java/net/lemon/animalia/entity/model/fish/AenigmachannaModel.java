package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.AenigmachannaEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AenigmachannaModel extends GeoModel<AenigmachannaEntity> {
    public ResourceLocation getModelResource(AenigmachannaEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/aenigmachanna.geo.json");
    }

    public ResourceLocation getTextureResource(AenigmachannaEntity object) {
//        if(object.getType() == ModEntities.AENIGMACHANNA_MAHABALI.get()) {
//            return new ResourceLocation(Animalia.MODID, "textures/entity/aenigmachanna_mahabali.png");
//        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/aenigmachanna_gollum.png");
    }

    public ResourceLocation getAnimationResource(AenigmachannaEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/aenigmachanna.animation.json");
    }

}
