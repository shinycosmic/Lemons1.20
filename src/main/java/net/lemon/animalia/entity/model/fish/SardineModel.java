package net.lemon.animalia.entity.model.fish;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class SardineModel extends AnimaliaCustomModel<RegSchoolingEntity> {
    public ResourceLocation getModelResource(RegSchoolingEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        if(object.getType() == ModEntities.SARDINOPS_MELANOSTICTA.get()
            || object.getType() == ModEntities.SARDINOPS_SAGAX.get()) {
            return new ResourceLocation(Animalia.MODID, "geo/sardinops.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".geo.json");
    }

    public ResourceLocation getTextureResource(RegSchoolingEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(RegSchoolingEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/sardinops.animation.json");
    }

    @Override
    public void setCustomAnimations(RegSchoolingEntity animatable, long instanceId, AnimationState<RegSchoolingEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if(animatable.isBaby() || !animatable.isInWater()) {
            return;
        }

        CoreGeoBone whole = this.getAnimationProcessor().getBone("swim");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = entityData.netHeadYaw() * ((float) Math.PI / 180F);
        float pitch = entityData.headPitch() * ((float) Math.PI / 180F);

        whole.setRotX(pitch);
        whole.setRotZ(-yaw/2);

    }
}
