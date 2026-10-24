package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.WalkingCrustaceanEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class MunidopsisModel extends AnimaliaCustomModel<WalkingCrustaceanEntity> {
    public ResourceLocation getModelResource(WalkingCrustaceanEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/munidopsis.geo.json");
    }

    public ResourceLocation getTextureResource(WalkingCrustaceanEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(WalkingCrustaceanEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/munidopsis.animation.json");
    }
}
