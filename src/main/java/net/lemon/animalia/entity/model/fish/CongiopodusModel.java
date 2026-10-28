package net.lemon.animalia.entity.model.fish;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BovichtusEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CongiopodusModel extends AnimaliaCustomModel<BovichtusEntity> {
    public ResourceLocation getModelResource(BovichtusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babynotothen.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/congiopodus.geo.json");
    }

    public ResourceLocation getTextureResource(BovichtusEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babygenericnotothen.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(BovichtusEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babynotothen.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/congiopodus.animation.json");
    }
}
