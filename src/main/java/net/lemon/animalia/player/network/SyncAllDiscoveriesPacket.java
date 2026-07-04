package net.lemon.animalia.player.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Server → Client: sends the player's entire discovery set on login or dimension change.
 * Replaces the client cache completely.
 */
public class SyncAllDiscoveriesPacket {

    private final Set<String> discoveries;

    public SyncAllDiscoveriesPacket(Set<String> discoveries) {
        this.discoveries = discoveries;
    }

    /** Decoder constructor — reads from network buffer. */
    public SyncAllDiscoveriesPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.discoveries = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            discoveries.add(buf.readUtf());
        }
    }

    /** Encodes this packet into the network buffer. */
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(discoveries.size());
        for (String key : discoveries) {
            buf.writeUtf(key);
        }
    }

    /** Handles the packet on the client thread. */
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientDiscoveryCache.replaceAll(discoveries);
        });
        ctx.get().setPacketHandled(true);
    }
}