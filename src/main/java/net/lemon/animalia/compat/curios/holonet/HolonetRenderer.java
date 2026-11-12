package net.lemon.animalia.compat.curios.holonet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lemon.animalia.Animalia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HolonetRenderer implements ICurioRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Animalia.MODID, "textures/entity/holonet_render.png");
    private static ModelPart wide;
    private static ModelPart slim;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> parent, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (parent.getModel() instanceof HumanoidModel<?> model) {
            HumanoidArm main = slotContext.entity().getMainArm();
            HumanoidArm side = slotContext.index() % 2 == 0 ? main : main.getOpposite();
            ModelPart arm = side == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
            draw(stack, slotContext.entity(), side, arm, poseStack, buffer, light);
        }
    }

    public static void renderFirstPerson(ItemStack stack, AbstractClientPlayer player, HumanoidArm side, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof PlayerRenderer renderer)) {
            return;
        }
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        ModelPart arm = side == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        arm.xRot = 0.0F;
        draw(stack, player, side, arm, poseStack, buffer, light);
    }

    private static void draw(ItemStack stack, LivingEntity entity, HumanoidArm side, ModelPart arm, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (wide == null) {
            wide = bake(HolonetModel.WIDE);
            slim = bake(HolonetModel.SLIM);
        }
        boolean isSlim = entity instanceof AbstractClientPlayer player && player.getModelName().equals("slim");
        ModelPart bracelet = isSlim ? slim : wide;
        VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(TEXTURE), false, stack.hasFoil());

        poseStack.pushPose();
        arm.translateAndRotate(poseStack);
        if (side == HumanoidArm.RIGHT) {
            poseStack.scale(-1.0F, 1.0F, 1.0F);
        }
        bracelet.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static ModelPart bake(ModelLayerLocation layer) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layer).getChild("bracelet");
    }
}
