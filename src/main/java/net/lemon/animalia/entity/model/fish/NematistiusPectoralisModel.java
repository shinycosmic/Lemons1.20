package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.RoosterfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class NematistiusPectoralisModel extends GeoModel<RoosterfishEntity> {
    public ResourceLocation getModelResource(RoosterfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyroosterfish.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/nematistiuspectoralis.geo.json");
    }

    public ResourceLocation getTextureResource(RoosterfishEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyroosterfish.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/nematistius_pectoralis.png");
    }

    public ResourceLocation getAnimationResource(RoosterfishEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyroosterfish.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/nematistiuspectoralis.animation.json");
    }

    @Override
    public void setCustomAnimations(RoosterfishEntity animatable, long instanceId, AnimationState<RoosterfishEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(animatable.isBaby() || !animatable.isInWater()) {
            return;
        }

        CoreGeoBone whole = this.getAnimationProcessor().getBone("main");
        CoreGeoBone dorsal = this.getAnimationProcessor().getBone("dorsalFin");
        CoreGeoBone pelvicLeft = this.getAnimationProcessor().getBone("backLeftFin");
        CoreGeoBone pelvicRight = this.getAnimationProcessor().getBone("backRightFin");
        CoreGeoBone tail = this.getAnimationProcessor().getBone("tail");
        CoreGeoBone tail2 = this.getAnimationProcessor().getBone("tail2");
        CoreGeoBone tail3 = this.getAnimationProcessor().getBone("tail3");
        CoreGeoBone tail4 = this.getAnimationProcessor().getBone("tail4");


        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

    }
}
