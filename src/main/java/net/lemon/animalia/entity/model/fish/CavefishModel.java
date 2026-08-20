package net.lemon.animalia.entity.model.fish;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CavefishEntity;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CavefishModel extends GeoModel<CavefishEntity> {
    public ResourceLocation getModelResource(CavefishEntity object) {
        if(object.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get() || object.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get() || object.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return new ResourceLocation(Animalia.MODID, "geo/sinocyclocheilus.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".geo.json");
    }

    public ResourceLocation getTextureResource(CavefishEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(CavefishEntity animatable) {
        if(animatable.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get() || animatable.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get() || animatable.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return new ResourceLocation(Animalia.MODID, "animations/sinocyclocheilus.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/" + ForgeRegistries.ENTITY_TYPES.getKey(animatable.getType()).getPath() + ".animation.json");
    }

    @Override
    public void setCustomAnimations(CavefishEntity animatable, long instanceId, AnimationState<CavefishEntity> animationState) {
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
