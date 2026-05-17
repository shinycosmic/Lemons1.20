package net.lemon.animalia.util;

import net.lemon.animalia.entity.render.BettaSplendensRenderer;
import net.lemon.animalia.entity.render.ChileanSeaBassRenderer;
import net.lemon.animalia.entity.render.EleginopsMaclovinusRenderer;
import net.lemon.animalia.entity.render.PseudaphritisUrvilliiRenderer;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AnimaliaRenderInit {
    public static void init() {
        //put render initializers here
        EntityRenderers.register(ModEntities.CHILEANSEABASS.get(), ChileanSeaBassRenderer::new);
        EntityRenderers.register(ModEntities.ELEGINOPS_MACLOVINUS.get(), EleginopsMaclovinusRenderer::new);
        EntityRenderers.register(ModEntities.PSEUDAPHRITIS_URVILLII.get(), PseudaphritisUrvilliiRenderer::new);
        EntityRenderers.register(ModEntities.BETTA_SPLENDENS.get(), BettaSplendensRenderer::new);
    }
}
