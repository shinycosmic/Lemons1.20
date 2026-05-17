package net.lemon.animalia.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.BettaEntity;
import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.awt.*;

public class BettaSplendensColorLayer extends GeoRenderLayer<BettaEntity> {

    public BettaSplendensColorLayer(GeoRenderer<BettaEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, BettaEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        //Entity will auto-assign to true if trait combination found in dict
        if(animatable.isSpecialVariant()) {
            return;
        }

        BettaTraits traits = animatable.getTraitsClient();
        BettaTraits.PatternPreset pattern = traits.patternPreset;
        Boolean isButterfly = traits.isButterfly;

        ColorUtil primary = traits.primaryColor;
        ColorUtil secondary = traits.secondaryColor;
        ColorUtil third = traits.thirdColor;

        switch (pattern) {
            case SOLID:
                renderMaskBody("solid", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 0, false, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                break;
            case BICOLOR:
                renderMaskBody("solid", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                break;
            case DRAGON:
                renderMaskBody("dragon", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                if(isButterfly) {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                } else {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                }
                break;
            case PIEBALD:
                renderMaskBody("piebald", "", secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskBody("piebald", "_face", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                if(isButterfly) {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                } else {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                }
                break;
            case MULTICOLOR:
                renderMaskBody("marble", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskBody("marble", "_spots", secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                break;
            case CAMBODIAN:
                renderMaskBody("solid", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                break;
            case BUTTERFLY:
                renderMaskBody("solid", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("dorsal", traits.dorsalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("caudal", traits.caudalPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 1, true, primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("anal", traits.analPreset.name(), 2, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                break;
            case MARBLE:
                renderMaskBody("marble", "", primary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                renderMaskBody("marble", "_spots", secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                if(isButterfly) {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 1, true, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 2, true, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, secondary, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                } else {
                    renderMaskFin("dorsal", traits.dorsalPreset.name(), 0, false, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("caudal", traits.caudalPreset.name(), 0, false, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("anal", traits.analPreset.name(), 0, false, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                    renderMaskFin("pelvic", traits.pelvicPreset.name(), 0, false, third, bakedModel, animatable, bufferSource, partialTick, packedLight, packedOverlay, poseStack);
                }
        }

    }


    private void renderMaskFin(String fin, String presetName, int index, Boolean isButterfly, ColorUtil color,
                               BakedGeoModel model, BettaEntity entity, MultiBufferSource bufferSource, float partialTick, int light, int packedOverlay, PoseStack poseStack) {
        String num = index == 0 ? "" : "_" + index;
        String basePath = "textures/entity/betta/betta_";
        String butterfly = isButterfly ? "butterfly" : "solid";
        String fullPath = basePath + fin + "_" + butterfly + "_" + presetName + num;
        renderMask(fullPath, model, entity, bufferSource, partialTick, light, color, packedOverlay, poseStack);
    }

    private void renderMaskBody(String presetName, String additionalPath, ColorUtil color,
                                BakedGeoModel model, BettaEntity entity, MultiBufferSource bufferSource, float partialTick, int light, int packedOverlay, PoseStack poseStack) {
        String basePath = "textures/entity/betta/betta_body_";
        String fullPath = basePath + presetName + additionalPath;
        renderMask(fullPath, model, entity, bufferSource, partialTick, light, color, packedOverlay, poseStack);
    }

    private void renderMask(String path, BakedGeoModel model, BettaEntity entity, MultiBufferSource bufferSource,
                            float partialTick, int light, ColorUtil color, int packedOverlay, PoseStack poseStack){
        RenderType layer = RenderType.entityCutoutNoCull(new ResourceLocation(Animalia.MODID, (path + ".png").toLowerCase()));
        VertexConsumer vc = bufferSource.getBuffer(layer);

        this.getRenderer().reRender(model,poseStack, bufferSource, entity, layer, vc, partialTick,
                light, packedOverlay, color.r(), color.g(), color.b(), 1.0f);
    }
}
