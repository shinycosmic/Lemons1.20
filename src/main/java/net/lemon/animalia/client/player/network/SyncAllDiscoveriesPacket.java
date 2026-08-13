package net.lemon.animalia.client.player.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SyncAllDiscoveriesPacket {

    private final Set<String> discoveries;
    private final boolean hasSeenWelcome;

    public SyncAllDiscoveriesPacket(Set<String> discoveries, boolean hasSeenWelcome) {
        this.discoveries = discoveries;
        this.hasSeenWelcome = hasSeenWelcome;
    }

    public SyncAllDiscoveriesPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.discoveries = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            discoveries.add(buf.readUtf());
        }
        this.hasSeenWelcome = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(discoveries.size());
        for (String key : discoveries) {
            buf.writeUtf(key);
        }
        buf.writeBoolean(hasSeenWelcome);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientDiscoveryCache.replaceAll(discoveries);
            ClientWelcomeCache.set(hasSeenWelcome);
        });
        ctx.get().setPacketHandled(true);
    }
}