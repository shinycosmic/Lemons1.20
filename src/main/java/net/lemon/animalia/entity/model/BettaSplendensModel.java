package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BettaSplendensModel extends GeoModel<BettaEntity> {
    public ResourceLocation getModelResource(BettaEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/bettasplendens.geo.json");
    }

    public ResourceLocation getTextureResource(BettaEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/betta/betta_base.png");
    }

    public ResourceLocation getAnimationResource(BettaEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/betta.animation.json");
    }

    @Override
    public void setCustomAnimations(BettaEntity animatable, long instanceId, AnimationState<BettaEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone whole = this.getAnimationProcessor().getBone("main");
        CoreGeoBone dorsal = this.getAnimationProcessor().getBone("dorsal");
        CoreGeoBone anal = this.getAnimationProcessor().getBone("anal");
        CoreGeoBone pelvicLeft = this.getAnimationProcessor().getBone("leftPelvic");
        CoreGeoBone pelvicRight = this.getAnimationProcessor().getBone("rightPelvic");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

        dorsal.setRotZ(-yaw*0.8f);
        anal.setRotZ(yaw*0.8f);
        pelvicLeft.setRotZ(yaw*0.8f);
        pelvicRight.setRotZ(yaw*0.8f);

    }
}
