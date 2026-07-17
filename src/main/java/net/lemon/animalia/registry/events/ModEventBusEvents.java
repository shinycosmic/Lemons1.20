package net.lemon.animalia.registry.events;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.custom.*;
import net.lemon.animalia.client.player.HolonetCapability;
import net.lemon.animalia.client.player.HolonetCapabilityProvider;
import net.lemon.animalia.client.player.network.DiscoverSpeciesPacket;
import net.lemon.animalia.client.player.network.ModNetwork;
import net.lemon.animalia.client.player.network.SyncAllDiscoveriesPacket;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    /***
     * register Attributes here. You set them in the entity and here is where you build them.
     * @param event
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CHILEANSEABASS.get(), ToothfishEntity.setAttributes());
        event.put(ModEntities.ELEGINOPS_MACLOVINUS.get(), ToothfishEntity.setAttributes());
        event.put(ModEntities.PSEUDAPHRITIS_URVILLII.get(), CongolliEntity.setAttributes());
        event.put(ModEntities.BETTA_SPLENDENS.get(), BettaEntity.setAttributes());
        event.put(ModEntities.PERCOPHIS_BRASILIENSIS.get(), ToothfishEntity.setAttributes());
        event.put(ModEntities.SYNBRANCHUS_MARMORATUS.get(), SynbranchusEntity.setAttributes());
        event.put(ModEntities.CHAUDHURIA_CAUDATA.get(), SynbranchusEntity.setAttributes());
        event.put(ModEntities.MASTACEMBELUS_ARMATUS.get(), MastacembelusEntity.setAttributes());
        event.put(ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get(), MastacembelusEntity.setAttributes());
        event.put(ModEntities.MACROGNATHUS_SIAMENSIS.get(), MastacembelusEntity.setAttributes());
        event.put(ModEntities.MASTACEMBELUS_BRICHARDI.get(), MastacembelusEntity.setAttributes());
        event.put(ModEntities.SINOBDELLA_SINENSIS.get(), MastacembelusEntity.setAttributes());
        event.put(ModEntities.RAKTHAMICHTHYS_INDICUS.get(), RakthamichthysEntity.setAttributes());
        event.put(ModEntities.NEMATISTIUS_PECTORALIS.get(), RoosterfishEntity.setAttributes());
        event.put(ModEntities.TOXOTES_CHATAREUS.get(), ToxotesEntity.setAttributes());

    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HolonetCapability.class);
    }

    @Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        private static final ResourceLocation HOLONET_CAP_ID =
                new ResourceLocation(Animalia.MODID, "holonet_discovery");

        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(HOLONET_CAP_ID, new HolonetCapabilityProvider());
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(oldCap -> {
                event.getEntity().getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(newCap -> {
                    newCap.copyFrom(oldCap);
                });
            });
            event.getOriginal().invalidateCaps();
        }

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            if (event.getSide().isClient()) return;
            if (event.getTarget() instanceof Scannable scannable) {
                Player player = event.getEntity();
                player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                    ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getTarget().getType());
                    int gender = scannable.hasDimorphism() ? ((AnimaliaBreedableWater) event.getTarget()).getGender() : -1;
                    if (cap.discover(id, gender)) {
                        ModNetwork.sendToPlayer((ServerPlayer) player, new DiscoverSpeciesPacket(id, gender));
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (event.getEntity().level().isClientSide()) return;
            if (event.getTarget() instanceof Scannable scannable) {
                Player player = event.getEntity();
                player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                    ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getTarget().getType());
                    int gender = scannable.hasDimorphism() ? ((AnimaliaBreedableWater) event.getTarget()).getGender() : -1;
                    if (cap.discover(id, gender)) {
                        ModNetwork.sendToPlayer((ServerPlayer) player, new DiscoverSpeciesPacket(id, gender));
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                ModNetwork.sendToPlayer(player, new SyncAllDiscoveriesPacket(cap.getAll(), cap.isLoadedFirstTime()));
            });
        }
    }
}
