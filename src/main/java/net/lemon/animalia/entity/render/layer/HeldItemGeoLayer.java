package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.function.Predicate;

public class HeldItemGeoLayer<T extends LivingEntity & GeoAnimatable> extends BlockAndItemGeoLayer<T> {
    private final String boneName;
    private final EquipmentSlot slot;
    private final ItemDisplayContext displayContext;
    private final Predicate<T> visible;
    private final float rollDegrees;

    public HeldItemGeoLayer(GeoRenderer<T> renderer, String boneName, EquipmentSlot slot) {
        this(renderer, boneName, slot, ItemDisplayContext.GROUND, entity -> true);
    }

    public HeldItemGeoLayer(GeoRenderer<T> renderer, String boneName, EquipmentSlot slot, ItemDisplayContext displayContext, Predicate<T> visible) {
        this(renderer, boneName, slot, displayContext, visible, 0.0F);
    }

    public HeldItemGeoLayer(GeoRenderer<T> renderer, String boneName, EquipmentSlot slot, ItemDisplayContext displayContext, Predicate<T> visible, float rollDegrees) {
        super(renderer);
        this.boneName = boneName;
        this.slot = slot;
        this.displayContext = displayContext;
        this.visible = visible;
        this.rollDegrees = rollDegrees;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource,
                                      float partialTick, int packedLight, int packedOverlay) {
        if (this.rollDegrees == 0.0F) {
            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(this.rollDegrees));
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Nullable
    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {
        if (!bone.getName().equals(this.boneName) || !this.visible.test(animatable)) {
            return null;
        }
        ItemStack stack = animatable.getItemBySlot(this.slot);
        return stack.isEmpty() ? null : stack;
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
        return this.displayContext;
    }

    public static boolean isPlaying(LivingEntity entity, String controllerName, String clipName) {
        if (!(entity instanceof GeoAnimatable animatable)) {
            return false;
        }
        AnimationController<?> controller = animatable.getAnimatableInstanceCache().getManagerForId(entity.getId()).getAnimationControllers().get(controllerName);
        if (controller == null) {
            return false;
        }
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        return current != null && current.animation().name().equals(clipName);
    }
}