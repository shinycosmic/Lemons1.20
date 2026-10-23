package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.AilurusFulgensEntity;
import net.lemon.animalia.entity.custom.HystrixCristataEntity;
import net.lemon.animalia.entity.model.AnimaliaCustomModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class HystrixCristataModel extends AnimaliaCustomModel<HystrixCristataEntity> {
    public ResourceLocation getModelResource(HystrixCristataEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "geo/babymongoose.geo.json");
        }
        return new ResourceLocation(Animalia.MODID, "geo/hystrix_cristata.geo.json");
    }

    public ResourceLocation getTextureResource(HystrixCristataEntity object) {
        if(object.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "textures/entity/babymongoose.png");
        }
        return new ResourceLocation(Animalia.MODID, "textures/entity/hystrix_cristata.png");
    }

    public ResourceLocation getAnimationResource(HystrixCristataEntity animatable) {
        if(animatable.isBaby()) {
            return new ResourceLocation(Animalia.MODID, "animations/babymongoose.animation.json");
        }
        return new ResourceLocation(Animalia.MODID, "animations/hystrix_cristata.animation.json");
    }

}
