package net.lemon.planetearth.entity.model;

import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.entity.SnakeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OcellatedPampasSnakeModel extends GeoModel<SnakeEntity> {
    public ResourceLocation getModelResource(SnakeEntity object) {
        return new ResourceLocation(PlanetEarth.MODID, "geo/ocellated_pampas_snake.geo.json");
    }

    public ResourceLocation getTextureResource(SnakeEntity object) {
        return new ResourceLocation(PlanetEarth.MODID, "textures/entities/tachymenis_ocellata.png");
    }

    public ResourceLocation getAnimationResource(SnakeEntity animatable) {
        return new ResourceLocation(PlanetEarth.MODID, "animations/tachymenis_ocellata.animation.json");
    }

    @Override
    public void setCustomAnimations(SnakeEntity animatable, long instanceId, AnimationState<SnakeEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
