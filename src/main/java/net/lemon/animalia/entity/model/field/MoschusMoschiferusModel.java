package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.HyemoschusEntity;
import net.lemon.animalia.entity.custom.MoschusEntity;
import net.lemon.animalia.entity.custom.MuntiacusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class MoschusMoschiferusModel extends AnimaliaCustomModel<MoschusEntity> {
    public ResourceLocation getModelResource(MoschusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymuntjac.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/moschus_moschiferus.geo.json");
    }

    public ResourceLocation getTextureResource(MoschusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babymuskdeer.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/moschus_moschiferus.png");
    }

    public ResourceLocation getAnimationResource(MoschusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymuntjac.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/moschus_moschiferus.animation.json");
    }

    @Override
    public void setCustomAnimations(MoschusEntity animatable, long instanceId, AnimationState<MoschusEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone tusk = this.getAnimationProcessor().getBone("tusk");
        if (tusk != null) {
            tusk.setHidden(animatable.getGender() == 0);
        }
    }
}
