package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ElaphodusEntity;
import net.lemon.animalia.entity.custom.MoschusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class ElaphodusCephalophusModel extends AnimaliaCustomModel<ElaphodusEntity> {
    public ResourceLocation getModelResource(ElaphodusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymuntjac.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/elaphodus_cephalophus.geo.json");
    }

    public ResourceLocation getTextureResource(ElaphodusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babytufteddeer.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/elaphodus_cephalophus.png");
    }

    public ResourceLocation getAnimationResource(ElaphodusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymuntjac.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/elaphodus_cephalophus.animation.json");
    }

    @Override
    public void setCustomAnimations(ElaphodusEntity animatable, long instanceId, AnimationState<ElaphodusEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone tusk = this.getAnimationProcessor().getBone("tusk");
        if (tusk != null) {
            tusk.setHidden(animatable.getGender() == 0);
        }
    }
}
