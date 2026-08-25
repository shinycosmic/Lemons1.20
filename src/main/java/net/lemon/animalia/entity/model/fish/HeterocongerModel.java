package net.lemon.animalia.entity.model.fish;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.HeterocongerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class HeterocongerModel extends GeoModel<HeterocongerEntity> {
    public ResourceLocation getModelResource(HeterocongerEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babyeel.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/heteroconger.geo.json");
    }

    public ResourceLocation getTextureResource(HeterocongerEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babypeacockeel.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(HeterocongerEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babyeel.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/heteroconger.animation.json");
    }
}
