package net.lemon.animalia.client.player.network;

import net.lemon.animalia.client.player.capability.StickingProjectileType;
import net.lemon.animalia.client.player.capability.StickingProjectileTypes;
import net.lemon.animalia.client.player.capability.StickingProjectilesCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncStickingProjectilePacket {

    private final int entityId;
    private final ResourceLocation typeId;
    private final int count;

    public SyncStickingProjectilePacket(int entityId, ResourceLocation typeId, int count) {
        this.entityId = entityId;
        this.typeId = typeId;
        this.count = count;
    }

    public SyncStickingProjectilePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.typeId = buf.readResourceLocation();
        this.count = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeResourceLocation(typeId);
        buf.writeVarInt(count);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            StickingProjectileType type = StickingProjectileTypes.get(typeId);
            Minecraft mc = Minecraft.getInstance();
            if (type != null && mc.level != null && mc.level.getEntity(entityId) instanceof LivingEntity living) {
                living.getCapability(StickingProjectilesCapabilityProvider.STICKING_PROJECTILES).ifPresent(cap -> cap.setCount(type, count));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}