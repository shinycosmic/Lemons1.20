package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.SynbranchusEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SynbranchusMarmoratusModel extends GeoModel<SynbranchusEntity> {
    public ResourceLocation getModelResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyEel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/synbranchusmarmoratus.geo.json");
    }

    public ResourceLocation getTextureResource(SynbranchusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyGenericEel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/synbranchusmarmoratus.png");
    }

    public ResourceLocation getAnimationResource(SynbranchusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyEel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/synbranchusmarmoratus.animation.json");
    }

    public void setCustomAnimations(SynbranchusEntity animatable, long instanceId, AnimationState<SynbranchusEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone main = this.getAnimationProcessor().getBone("main");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        // Don't apply diagonal swimming when in a hiding phase
        if (animatable.getHidePhase() == 0) {
            main.setRotX(main.getRotX() + pitch);
            main.setRotZ(main.getRotZ() + (-yaw / 2));
        }

        if (animatable.getHidePhase() == 0 && animatable.tailBuffer != null) {
            String[] tailBones = {"tail", "tail2", "tail3", "tail4", "tail5", "tail6", "tail7", "tail8", "tail9"};

            for (int i = 0; i < tailBones.length; i++) {
                CoreGeoBone bone = this.getAnimationProcessor().getBone(tailBones[i]);
                if (bone != null) {
                    float trailRot = animatable.tailBuffer.getChainRotation(i, tailBones.length, 15f, 3, animatable);
                    bone.setRotY(bone.getRotY() + trailRot);
                }
            }
        }
    }
}
