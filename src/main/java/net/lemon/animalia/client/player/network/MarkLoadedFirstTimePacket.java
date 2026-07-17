package net.lemon.animalia.client.player.network;

import net.lemon.animalia.client.player.HolonetCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MarkLoadedFirstTimePacket {

    public MarkLoadedFirstTimePacket() {}

    public MarkLoadedFirstTimePacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                cap.setLoadedFirstTime(true);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
