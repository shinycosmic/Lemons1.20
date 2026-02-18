package net.lemon.planetearth.entity.model;

import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.entity.SnakeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

/// IMPORTANT NOTE, DO NOT FORGET TO CHANGE THE SNAKEENTITY INSIDE THE <>
public class TemplateModel extends GeoModel<SnakeEntity> {
    /***
     *  Set the location of the geckolib model
     * @param object
     *  This parameter refers to the entity this model will be tied to
     * @return
     */
    public ResourceLocation getModelResource(SnakeEntity object) {
        return new ResourceLocation(PlanetEarth.MODID, "geo/name.geo.json");
    }

    /***
     *  Set the location of the texture
     * @param object
     * @return
     */
    public ResourceLocation getTextureResource(SnakeEntity object) {
        return new ResourceLocation(PlanetEarth.MODID, "textures/entities/scientific_name.png");
    }

    /***
     *  Set the location of the animations for this geckolib mob
     * @param animatable
     * @return
     */
    public ResourceLocation getAnimationResource(SnakeEntity animatable) {
        return new ResourceLocation(PlanetEarth.MODID, "animations/scientific_name.animation.json");
    }

    /***
     * This is the method that gets the mob to look at you
     * @param animatable The {@code GeoAnimatable} instance currently being rendered
     * @param instanceId The instance id of the {@code GeoAnimatable}
     * @param animationState An {@link AnimationState} instance created to hold animation data for the {@code animatable} for this method call
     */
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
