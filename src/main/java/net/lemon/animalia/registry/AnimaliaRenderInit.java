package net.lemon.animalia.registry;

import net.lemon.animalia.entity.custom.PangasianodonEntity;
import net.lemon.animalia.entity.custom.PogonophryneEntity;
import net.lemon.animalia.entity.custom.ToxotesEntity;
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
        EntityRenderers.register(ModEntities.MASTACEMBELUS_BRICHARDI.get(), MastacembelusBrichardiRenderer::new);
        EntityRenderers.register(ModEntities.MACROGNATHUS_SIAMENSIS.get(), MacrognathusSiamensisRenderer::new);
        EntityRenderers.register(ModEntities.SINOBDELLA_SINENSIS.get(), SinobdellaSinensisRenderer::new);
        EntityRenderers.register(ModEntities.RAKTHAMICHTHYS_INDICUS.get(), RakthamichthysIndicusRenderer::new);
        EntityRenderers.register(ModEntities.NEMATISTIUS_PECTORALIS.get(), NematistiusPectoralisRenderer::new);
        EntityRenderers.register(ModEntities.TOXOTES_CHATAREUS.get(), ToxotesChatareusRenderer::new);
        EntityRenderers.register(ModEntities.POGONOPHRYNE_MARMORATA.get(), PogonophryneMarmorataRenderer::new);
        EntityRenderers.register(ModEntities.CHAENOCEPHALUS_ACERATUS.get(), ChaenocephalusAceratusRenderer::new);
        EntityRenderers.register(ModEntities.CYGNODRACO_MAWSONI.get(), CygnodracoMawsoniRenderer::new);
        EntityRenderers.register(ModEntities.SCATOPHAGUS_ARGUS.get(), ScatophagusArgusRenderer::new);
        EntityRenderers.register(ModEntities.PROCAMBARUS_VIRGINALIS.get(), ProcambarusRenderer::new);
        EntityRenderers.register(ModEntities.PROCAMBARUS_ALLENI.get(), ProcambarusRenderer::new);
        EntityRenderers.register(ModEntities.PROCAMBARUS_CLARKII.get(), ProcambarusRenderer::new);
        EntityRenderers.register(ModEntities.PROCAMBARUS_LUCIFUGUS.get(), ProcambarusRenderer::new);
        EntityRenderers.register(ModEntities.PANGASIANODON_GIGAS.get(), PangasianodonGigasRenderer::new);
        EntityRenderers.register(ModEntities.POMACANTHUS_IMPERATOR.get(), PomacanthusImperatorRenderer::new);
        EntityRenderers.register(ModEntities.NASO_BREVIROSTRIS.get(), NasoBrevirostrisRenderer::new);
        EntityRenderers.register(ModEntities.ZANCLUS_CORNUTUS.get(), ZanclusCornutusRenderer::new);
        EntityRenderers.register(ModEntities.PARACANTHURUS_HEPATUS.get(), ParacanthurusHepatusRenderer::new);
        EntityRenderers.register(ModEntities.CHELMON_ROSTRATUS.get(), ChelmonRostratusRenderer::new);
        EntityRenderers.register(ModEntities.CHAETODON_AURIGA.get(), ChaetodonAurigaRenderer::new);






        ///  OTHER RENDERS
        EntityRenderers.register(ModEntities.WATER_SPIT.get(), WaterSpitRenderer::new);

    }
}
