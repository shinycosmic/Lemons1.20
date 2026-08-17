package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.HydrocynusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HydrocynusGoliathModel extends GeoModel<HydrocynusEntity> {
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

    @Override
    public void setCustomAnimations(HydrocynusEntity animatable, long instanceId, AnimationState<HydrocynusEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
//        if(animatable.isBaby() || !animatable.isInWater()) {
//            return;
//        }
//
//        CoreGeoBone whole = this.getAnimationProcessor().getBone("swim");
//
//
//        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
//        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);
//
//        whole.setRotX(pitch);
//        whole.setRotZ(-yaw/2);

    }
}
