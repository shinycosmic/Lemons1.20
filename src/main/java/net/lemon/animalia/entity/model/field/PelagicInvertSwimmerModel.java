package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrayfishEntity;
import net.lemon.animalia.entity.custom.PelagicInvertSwimmerEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PelagicInvertSwimmerModel extends GeoModel<PelagicInvertSwimmerEntity> {
    public ResourceLocation getModelResource(PelagicInvertSwimmerEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".geo.json");
    }

    public ResourceLocation getTextureResource(PelagicInvertSwimmerEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(PelagicInvertSwimmerEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/" + ForgeRegistries.ENTITY_TYPES.getKey(animatable.getType()).getPath() + ".animation.json");
    }

    @Override
    public void setCustomAnimations(PelagicInvertSwimmerEntity animatable, long instanceId, AnimationState<PelagicInvertSwimmerEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(!animatable.isInWater() || animatable.isBaby()) return;

        CoreGeoBone whole = this.getAnimationProcessor().getBone("swim");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

    }
}
