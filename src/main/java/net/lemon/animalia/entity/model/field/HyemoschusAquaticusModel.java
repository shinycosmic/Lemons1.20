package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.HyemoschusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HyemoschusAquaticusModel extends GeoModel<HyemoschusEntity> {
    public ResourceLocation getModelResource(HyemoschusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymuntjac.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/hyemoschus_aquaticus.geo.json");
    }

    public ResourceLocation getTextureResource(HyemoschusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babychevrotain.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/hyemoschus_aquaticus.png");
    }

    public ResourceLocation getAnimationResource(HyemoschusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymuntjac.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/hyemoschus_aquaticus.animation.json");
    }

    @Override
    public void setCustomAnimations(HyemoschusEntity animatable, long instanceId, AnimationState<HyemoschusEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone tusk = this.getAnimationProcessor().getBone("tusk");
        if (tusk != null) {
            tusk.setHidden(animatable.getGender() == 0);
        }
    }
}
