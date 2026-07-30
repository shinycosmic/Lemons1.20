package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ScatophagusArgusModel extends GeoModel<RegSchoolingEntity> {
    public ResourceLocation getModelResource(RegSchoolingEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/scatophagusargus.geo.json");
    }

    public ResourceLocation getTextureResource(RegSchoolingEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/scatophagus_argus.png");
    }

    public ResourceLocation getAnimationResource(RegSchoolingEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/scatophagusargus.animation.json");
    }

    @Override
    public void setCustomAnimations(RegSchoolingEntity animatable, long instanceId, AnimationState<RegSchoolingEntity> animationState) {
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
