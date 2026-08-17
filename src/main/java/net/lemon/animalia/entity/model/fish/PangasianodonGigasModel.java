package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.PangasianodonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PangasianodonGigasModel extends GeoModel<PangasianodonEntity> {
    public ResourceLocation getModelResource(PangasianodonEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babycatfish.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/pangasianodongigas.geo.json");
    }

    public ResourceLocation getTextureResource(PangasianodonEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericcatfish.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/pangasianodon_gigas.png");
    }

    public ResourceLocation getAnimationResource(PangasianodonEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babycatfish.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/pangasianodongigas.animation.json");
    }

//    @Override
//    public void setCustomAnimations(PangasianodonEntity animatable, long instanceId, AnimationState<PangasianodonEntity> animationState) {
//        super.setCustomAnimations(animatable, instanceId, animationState);
//        if(animatable.isBaby() || !animatable.isInWater()) {
//            return;
//        }
//
//        CoreGeoBone whole = this.getAnimationProcessor().getBone("main");
//
//
//        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
//        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);
//
//        whole.setRotX(pitch);
//        whole.setRotZ(-yaw/2);
//
//    }
}
