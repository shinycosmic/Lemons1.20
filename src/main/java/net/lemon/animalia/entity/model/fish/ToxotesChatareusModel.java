package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ToxotesEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ToxotesChatareusModel extends GeoModel<ToxotesEntity> {
    public ResourceLocation getModelResource(ToxotesEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/toxoteschatareus.geo.json");
    }

    public ResourceLocation getTextureResource(ToxotesEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/toxotes_chatareus.png");
    }

    public ResourceLocation getAnimationResource(ToxotesEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/toxoteschatareus.animation.json");
    }

    @Override
    public void setCustomAnimations(ToxotesEntity animatable, long instanceId, AnimationState<ToxotesEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(animatable.isBaby() || !animatable.isInWater()) {
            return;
        }

        CoreGeoBone whole = this.getAnimationProcessor().getBone("main");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

    }
}
