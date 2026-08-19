package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.IndostomusEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IndostomusParadoxusModel extends GeoModel<IndostomusEntity> {
    public ResourceLocation getModelResource(IndostomusEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/indostomusparadoxus.geo.json");
    }

    public ResourceLocation getTextureResource(IndostomusEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/indostomus_paradoxus.png");
    }

    public ResourceLocation getAnimationResource(IndostomusEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/indostomusparadoxus.animation.json");
    }

    @Override
    public void setCustomAnimations(IndostomusEntity animatable, long instanceId, AnimationState<IndostomusEntity> animationState) {
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
