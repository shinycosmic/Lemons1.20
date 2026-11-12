package net.lemon.animalia.compat.curios.holonet;

import net.lemon.animalia.Animalia;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class HolonetModel {
    public static final ModelLayerLocation WIDE = new ModelLayerLocation(new ResourceLocation(Animalia.MODID, "holonet_wide"), "main");
    public static final ModelLayerLocation SLIM = new ModelLayerLocation(new ResourceLocation(Animalia.MODID, "holonet_slim"), "main");

    public static LayerDefinition create(boolean slim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        float armWidth = slim ? 3.0F : 4.0F;
        float center = armWidth / 2.0F - 1.0F;

        PartDefinition bracelet = part.addOrReplaceChild("bracelet", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, 7.25F, -2.0F, armWidth, 1.0F, 4.0F, new CubeDeformation(0.25F))
                        .texOffs(5, 6).addBox(center - 0.5F, 7.25F, -2.725F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        bracelet.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(0, 6).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(center, 7.75F, -2.475F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(mesh, 16, 16);
    }
}
