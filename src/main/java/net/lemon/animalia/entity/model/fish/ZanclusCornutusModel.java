package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.GrazeSchoolingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ZanclusCornutusModel extends GeoModel<GrazeSchoolingEntity> {
    public ResourceLocation getModelResource(GrazeSchoolingEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyreeffish.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/zancluscornutus.geo.json");
    }

    public ResourceLocation getTextureResource(GrazeSchoolingEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyzanclus.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/zanclus_cornutus.png");
    }

    public ResourceLocation getAnimationResource(GrazeSchoolingEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyreeffish.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/zancluscornutus.animation.json");
    }

    @Override
    public void setCustomAnimations(GrazeSchoolingEntity animatable, long instanceId, AnimationState<GrazeSchoolingEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(!animatable.isInWater()) return;

        CoreGeoBone whole = this.getAnimationProcessor().getBone("swim");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

    }
}
