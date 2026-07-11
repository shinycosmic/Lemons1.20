package net.lemon.animalia.registry;

import net.lemon.animalia.entity.render.*;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AnimaliaRenderInit {
    public static void init() {
        //put render initializers here
        EntityRenderers.register(ModEntities.CHILEANSEABASS.get(), ChileanSeaBassRenderer::new);
        EntityRenderers.register(ModEntities.ELEGINOPS_MACLOVINUS.get(), EleginopsMaclovinusRenderer::new);
        EntityRenderers.register(ModEntities.PSEUDAPHRITIS_URVILLII.get(), PseudaphritisUrvilliiRenderer::new);
        EntityRenderers.register(ModEntities.BETTA_SPLENDENS.get(), BettaSplendensRenderer::new);
        EntityRenderers.register(ModEntities.PERCOPHIS_BRASILIENSIS.get(), PercophisBrasiliensisRenderer::new);
        EntityRenderers.register(ModEntities.SYNBRANCHUS_MARMORATUS.get(), SynbranchusMarmoratusRenderer::new);
        EntityRenderers.register(ModEntities.CHAUDHURIA_CAUDATA.get(), ChaudhuriaCaudataRenderer::new);
        EntityRenderers.register(ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get(), MastacembelusErythrotaeniaRenderer::new);
        EntityRenderers.register(ModEntities.MASTACEMBELUS_ARMATUS.get(), MastacembelusArmatusRenderer::new);
        EntityRenderers.register(ModEntities.MACROGNATHUS_SIAMENSIS.get(), MacrognathusSiamensisRenderer::new);
    }
}
