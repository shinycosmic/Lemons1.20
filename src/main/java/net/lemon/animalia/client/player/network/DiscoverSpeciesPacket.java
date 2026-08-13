package net.lemon.animalia.client.player.network;

import net.lemon.animalia.client.toast.DiscoveryToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DiscoverSpeciesPacket {

    private final ResourceLocation entityType;
    private final int gender;

    public DiscoverSpeciesPacket(ResourceLocation entityType, int gender) {
        this.entityType = entityType;
        this.gender = gender;
    }

    public DiscoverSpeciesPacket(FriendlyByteBuf buf) {
        this.entityType = buf.readResourceLocation();
        this.gender = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(entityType);
        buf.writeVarInt(gender);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientDiscoveryCache.addDiscovery(entityType.toString() + "_" + gender);
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entityType);
                if (type != null) {
                    Component name = type.getDescription();
                    mc.getToasts().addToast(new DiscoveryToast(name));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}