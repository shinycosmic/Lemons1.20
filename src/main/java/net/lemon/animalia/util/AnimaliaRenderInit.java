package net.lemon.animalia.util;

import net.lemon.animalia.entity.render.*;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AnimaliaRenderInit {
    public static void init() {
        //put render initializers here
        EntityRenderers.register(ModEntities.CHILEANSEABASS.get(), ChileanSeaBassRenderer::new);
        EntityRenderers.register(ModEntities.ELEGINOPS_MACLOVINUS.get(), EleginopsMaclovinusRenderer::new);
        EntityRenderers.register(ModEntities.PSEUDAPHRITIS_URVILLII.get(), PseudaphritisUrvilliiRenderer::new);
        EntityRenderers.register(ModEntities.BETTA_SPLENDENS.get(), BettaSplendensRenderer::new);
        EntityRenderers.register(ModEntities.PERCOPHIS_BRASILIENSIS.get(), PercophisBrasiliensisRenderer::new);
    }
}
