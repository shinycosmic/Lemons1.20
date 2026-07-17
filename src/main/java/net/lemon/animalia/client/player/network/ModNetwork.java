package net.lemon.animalia.client.player.network;

import net.lemon.animalia.Animalia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Animalia.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.messageBuilder(DiscoverSpeciesPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DiscoverSpeciesPacket::encode)
                .decoder(DiscoverSpeciesPacket::new)
                .consumerMainThread(DiscoverSpeciesPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncAllDiscoveriesPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncAllDiscoveriesPacket::encode)
                .decoder(SyncAllDiscoveriesPacket::new)
                .consumerMainThread(SyncAllDiscoveriesPacket::handle)
                .add();
    }

    /**
     * Sends a packet to a specific player's client.
     */
    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}