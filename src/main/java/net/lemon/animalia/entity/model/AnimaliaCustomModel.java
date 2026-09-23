package net.lemon.animalia.entity.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.BoneAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

public abstract class AnimaliaCustomModel<T extends Entity & GeoAnimatable> extends GeoModel<T> {
    private static final DataTicket<ExitBlends> EXIT_BLENDS = new DataTicket<>("animalia_exit_blends", ExitBlends.class);

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        AnimatableManager<T> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
        ExitBlends blends = manager.getData(EXIT_BLENDS);
        if (blends == null) {
            blends = new ExitBlends();
            manager.setData(EXIT_BLENDS, blends);
        }
        double clock = animatable.tickCount + animationState.getPartialTick();
        for (AnimationController<T> controller : manager.getAnimationControllers().values()) {
            if (controller.getName().equals("controller")) {
                continue;
            }
            Capture capture = blends.byController.get(controller.getName());
            if (controller.getAnimationState() != AnimationController.State.STOPPED && controller.getCurrentAnimation() != null) {
                if (capture == null) {
                    capture = new Capture();
                    blends.byController.put(controller.getName(), capture);
                }
                capture.capture(controller.getCurrentAnimation().animation(), this.getAnimationProcessor());
            } else if (capture != null && controller.getAnimationState() == AnimationController.State.STOPPED) {
                if (capture.blendStart < 0) {
                    capture.blendStart = clock;
                }
                double t = (clock - capture.blendStart) / animatable.getBoneResetTime();
                if (t >= 1) {
                    blends.byController.remove(controller.getName());
                } else {
                    capture.blend(this.getAnimationProcessor(), (float) t);
                }
            }
        }
    }

    private static class ExitBlends {
        final Map<String, Capture> byController = new Object2ObjectOpenHashMap<>(2);
    }

    private static class Capture {
        final Map<String, BoneCapture> bones = new Object2ObjectOpenHashMap<>(8);
        double blendStart = -1;
        int frame;

        void capture(Animation animation, AnimationProcessor<?> processor) {
            this.blendStart = -1;
            this.frame++;
            for (BoneAnimation boneAnimation : animation.boneAnimations()) {
                CoreGeoBone bone = processor.getBone(boneAnimation.boneName());
                if (bone == null) {
                    continue;
                }
                BoneCapture bc = this.bones.computeIfAbsent(boneAnimation.boneName(), name -> new BoneCapture());
                bc.frame = this.frame;
                bc.rot = !boneAnimation.rotationKeyFrames().xKeyframes().isEmpty();
                bc.pos = !boneAnimation.positionKeyFrames().xKeyframes().isEmpty();
                bc.scale = !boneAnimation.scaleKeyFrames().xKeyframes().isEmpty();
                bc.values[0] = bone.getRotX();
                bc.values[1] = bone.getRotY();
                bc.values[2] = bone.getRotZ();
                bc.values[3] = bone.getPosX();
                bc.values[4] = bone.getPosY();
                bc.values[5] = bone.getPosZ();
                bc.values[6] = bone.getScaleX();
                bc.values[7] = bone.getScaleY();
                bc.values[8] = bone.getScaleZ();
            }
            this.bones.values().removeIf(bc -> bc.frame != this.frame);
        }

        void blend(AnimationProcessor<?> processor, float t) {
            for (Map.Entry<String, BoneCapture> entry : this.bones.entrySet()) {
                CoreGeoBone bone = processor.getBone(entry.getKey());
                if (bone == null) {
                    continue;
                }
                BoneCapture bc = entry.getValue();
                if (bc.rot) {
                    bone.setRotX(Mth.lerp(t, bc.values[0], bone.getRotX()));
                    bone.setRotY(Mth.lerp(t, bc.values[1], bone.getRotY()));
                    bone.setRotZ(Mth.lerp(t, bc.values[2], bone.getRotZ()));
                }
                if (bc.pos) {
                    bone.setPosX(Mth.lerp(t, bc.values[3], bone.getPosX()));
                    bone.setPosY(Mth.lerp(t, bc.values[4], bone.getPosY()));
                    bone.setPosZ(Mth.lerp(t, bc.values[5], bone.getPosZ()));
                }
                if (bc.scale) {
                    bone.setScaleX(Mth.lerp(t, bc.values[6], bone.getScaleX()));
                    bone.setScaleY(Mth.lerp(t, bc.values[7], bone.getScaleY()));
                    bone.setScaleZ(Mth.lerp(t, bc.values[8], bone.getScaleZ()));
                }
            }
        }
    }

    private static class BoneCapture {
        final float[] values = new float[9];
        boolean rot;
        boolean pos;
        boolean scale;
        int frame;
    }
}