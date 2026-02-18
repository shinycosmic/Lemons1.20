package net.lemon.planetearth.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.entity.SnakeEntity;
import net.lemon.planetearth.entity.model.OcellatedPampasSnakeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SnakeRenderer extends GeoEntityRenderer<SnakeEntity> {

    public SnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new OcellatedPampasSnakeModel());
        this.shadowRadius = 0.3f;
    }

    public float getScaler() {
        return 0.2f;
    }

    @Override
    public ResourceLocation getTextureLocation(SnakeEntity animatable) {
        return new ResourceLocation(PlanetEarth.MODID, "textures/entities/tachymenis_ocellata.png");
    }

    @Override
    public void render(SnakeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.1f, 0.1f, 0.1f);
        } else {
            poseStack.scale(getScaler(),getScaler(),getScaler());
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(SnakeEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
