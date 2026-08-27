package net.lemon.animalia.registry;

import net.lemon.animalia.entity.render.*;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AnimaliaRenderInit {
    public static void init() {
        //put render initializers here
        EntityRenderers.register(ModEntities.DISSOSTICHUS_ELEGINOIDES.get(), ChileanSeaBassRenderer::new);
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
        EntityRenderers.register(ModEntities.SIGANUS_VULPINUS.get(), SiganusVulpinusRenderer::new);
        EntityRenderers.register(ModEntities.ZEBRASOMA_VELIFER.get(), ZebrasomaVeliferRenderer::new);
        EntityRenderers.register(ModEntities.ZEBRASOMA_FLAVESCENS.get(), ZebrasomaFlavescensRenderer::new);
        EntityRenderers.register(ModEntities.HYDROCYNUS_GOLIATH.get(), HydrocynusGoliathRenderer::new);
        EntityRenderers.register(ModEntities.INDOSTOMUS_PARADOXUS.get(), IndostomusParadoxusRenderer::new);
        EntityRenderers.register(ModEntities.AMBLYOPSIS_HOOSIERI.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.GITCHAK_NAKANA.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.KRYPTOGLANIS_SHAJII.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.SINOCYCLOCHEILUS_HYALINUS.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.CYPRINODON_DIABOLIS.get(), CavefishRenderer::new);
        EntityRenderers.register(ModEntities.AENIGMACHANNA_GOLLUM.get(), AenigmachannaRenderer::new);
        EntityRenderers.register(ModEntities.LEPTOBRAMA_MUELLERI.get(), LeptobramaMuelleriRenderer::new);
        EntityRenderers.register(ModEntities.HIPPOCAMPUS_INGENS.get(), HippocampusIngensRenderer::new);
        EntityRenderers.register(ModEntities.HIPPOCAMPUS_REIDI.get(), HippocampusIngensRenderer::new);
        EntityRenderers.register(ModEntities.BOVICHTUS_VARIEGATUS.get(), BovichtusVariegatusRenderer::new);
        EntityRenderers.register(ModEntities.HETEROCONGER_HASSI.get(), HeterocongerRenderer::new);
        EntityRenderers.register(ModEntities.GORGASIA_PRECLARA.get(), HeterocongerRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_LEUCOSTERNON.get(), AcanthurusRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_ACHILLES.get(), AcanthurusRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_COERULEUS.get(), AcanthurusRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_SOHAL.get(), AcanthurusSlenderRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_JAPONICUS.get(), AcanthurusRenderer::new);
        EntityRenderers.register(ModEntities.ACANTHURUS_LINEATUS.get(), AcanthurusSlenderRenderer::new);



        //Land
        EntityRenderers.register(ModEntities.SMUTSIA_GIGANTEA.get(), SmutsiaGiganteaRenderer::new);


        EntityRenderers.register(ModEntities.WATER_SPIT.get(), WaterSpitRenderer::new);

    }
}
