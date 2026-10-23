package net.lemon.animalia.entity.render;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.projectiles.QuillProjectile;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class QuillRenderer extends ArrowRenderer<QuillProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Animalia.MODID, "textures/entity/projectiles/quill.png");

    public QuillRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(QuillProjectile entity) {
        return TEXTURE;
    }
}