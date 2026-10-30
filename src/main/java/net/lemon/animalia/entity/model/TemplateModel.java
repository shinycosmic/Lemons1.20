//package net.lemon.animalia.entity.model;
//
//import net.lemon.animalia.PlanetEarth;
//import net.lemon.animalia.entity.SnakeEntity;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.Entity;
//import software.bernie.geckolib.constant.DataTickets;
//import software.bernie.geckolib.core.animatable.GeoAnimatable;
//import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
//import software.bernie.geckolib.core.animation.AnimationState;
//import software.bernie.geckolib.model.GeoModel;
//import software.bernie.geckolib.model.data.EntityModelData;
//

//public class TemplateModel extends GeoModel<Entity> {
//    public ResourceLocation getModelResource(Entity object) {
//        return new ResourceLocation(PlanetEarth.MODID, "geo/name.geo.json");
//    }
//
//    public ResourceLocation getTextureResource(Entity object) {
//        return new ResourceLocation(PlanetEarth.MODID, "textures/entity/scientific_name.png");
//    }
//
//    public ResourceLocation getAnimationResource(Entity animatable) {
//        return new ResourceLocation(PlanetEarth.MODID, "animations/scientific_name.animation.json");
//    }
//
//    /***
//     * This is the method that gets the mob to look at you, also used for smooth turning.
//     */
//    @Override
//    public void setCustomAnimations(Entity animatable, long instanceId, AnimationState<Entity> animationState) {
//        CoreGeoBone head = getAnimationProcessor().getBone("head");
//        if (head != null) {
//            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//
//            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
//            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
//        }
//    }
//}
