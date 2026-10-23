package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.animalia.client.player.capability.StickingProjectileType;
import net.lemon.animalia.client.player.capability.StickingProjectilesCapability;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class StickingProjectileLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final EntityRenderDispatcher dispatcher;
    private final StickingProjectileType type;
    private final Function<LivingEntity, ? extends Entity> projectileFactory;

    public StickingProjectileLayer(RenderLayerParent<T, M> parent, EntityRenderDispatcher dispatcher, StickingProjectileType type, Function<LivingEntity, ? extends Entity> projectileFactory) {
        super(parent);
        this.dispatcher = dispatcher;
        this.type = type;
        this.projectileFactory = projectileFactory;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        int stuck = StickingProjectilesCapability.getCount(entity, this.type);
        if (stuck <= 0 || entity.isInvisible()) {
            return;
        }

        M model = this.getParentModel();
        List<ModelPart> parts = Stream.of(model.head, model.body, model.rightArm, model.leftArm, model.rightLeg, model.leftLeg).filter(part -> !part.isEmpty()).toList();

        if (parts.isEmpty()) {
            return;
        }

        RandomSource random = RandomSource.create(entity.getId() * 31L + this.type.id().hashCode());
        for (int i = 0; i < stuck; i++) {
            poseStack.pushPose();
            ModelPart part = parts.get(random.nextInt(parts.size()));
            ModelPart.Cube cube = part.getRandomCube(random);
            part.translateAndRotate(poseStack);
            float fx = random.nextFloat();
            float fy = random.nextFloat();
            float fz = random.nextFloat();
            poseStack.translate(Mth.lerp(fx, cube.minX, cube.maxX) / 16.0F, Mth.lerp(fy, cube.minY, cube.maxY) / 16.0F, Mth.lerp(fz, cube.minZ, cube.maxZ) / 16.0F);
            this.renderProjectile(poseStack, buffer, packedLight, entity, -(fx * 2.0F - 1.0F), -(fy * 2.0F - 1.0F), -(fz * 2.0F - 1.0F), partialTick);
            poseStack.popPose();
        }
    }

    private void renderProjectile(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity, float x, float y, float z, float partialTick) {
        float horizontal = Mth.sqrt(x * x + z * z);
        Entity projectile = this.projectileFactory.apply(entity);
        projectile.setYRot((float) (Math.atan2(x, z) * (180.0D / Math.PI)));
        projectile.setXRot((float) (Math.atan2(y, horizontal) * (180.0D / Math.PI)));
        projectile.yRotO = projectile.getYRot();
        projectile.xRotO = projectile.getXRot();
        this.dispatcher.render(projectile, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, buffer, packedLight);
    }
}