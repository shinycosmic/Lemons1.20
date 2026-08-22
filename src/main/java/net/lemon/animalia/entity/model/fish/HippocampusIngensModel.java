package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.lemon.animalia.entity.custom.SeahorseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HippocampusIngensModel extends GeoModel<SeahorseEntity> {
    public ResourceLocation getModelResource(SeahorseEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyseahorse.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/hippocampus.geo.json");
    }

    public ResourceLocation getTextureResource(SeahorseEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babyseahorse.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(SeahorseEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyseahorse.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/hippocampus.animation.json");
    }

    @Override
    public void setCustomAnimations(SeahorseEntity animatable, long instanceId, AnimationState<SeahorseEntity> animationState) {
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
