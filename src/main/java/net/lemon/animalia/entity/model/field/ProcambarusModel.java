package net.lemon.animalia.entity.model.field;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrayfishEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class ProcambarusModel extends GeoModel<CrayfishEntity> {
    public ResourceLocation getModelResource(CrayfishEntity object) {
        return new ResourceLocation(Animalia.MODID, "geo/procambarus.geo.json");
    }

    public ResourceLocation getTextureResource(CrayfishEntity object) {
        return new ResourceLocation(Animalia.MODID, "textures/entity/" + ForgeRegistries.ENTITY_TYPES.getKey(object.getType()).getPath() + ".png");
    }

    public ResourceLocation getAnimationResource(CrayfishEntity animatable) {
        return new ResourceLocation(Animalia.MODID, "animations/procambarus.animation.json");
    }
}
