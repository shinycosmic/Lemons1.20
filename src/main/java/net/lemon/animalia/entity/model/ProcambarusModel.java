package net.lemon.animalia.entity.model;//package net.lemon.animalia.entity.model;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.CrayfishEntity;
import net.lemon.animalia.entity.custom.RegSchoolingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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
