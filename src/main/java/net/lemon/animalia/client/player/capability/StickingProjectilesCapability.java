package net.lemon.animalia.client.player.capability;

import net.lemon.animalia.client.player.network.ModNetwork;
import net.lemon.animalia.client.player.network.SyncStickingProjectilePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StickingProjectilesCapability {
    private final Map<StickingProjectileType, Sticking> stuck = new HashMap<>();

    private static final class Sticking {
        int count;
        int removeTime;
    }

    public int getCount(StickingProjectileType type) {
        Sticking s = this.stuck.get(type);
        return s == null ? 0 : s.count;
    }

    public void setCount(StickingProjectileType type, int count) {
        if (count <= 0) {
            this.stuck.remove(type);
        } else {
            this.stuck.computeIfAbsent(type, t -> new Sticking()).count = count;
        }
    }

    public static int getCount(LivingEntity entity, StickingProjectileType type) {
        return entity.getCapability(StickingProjectilesCapabilityProvider.STICKING_PROJECTILES).map(cap -> cap.getCount(type)).orElse(0);
    }

    public static void add(LivingEntity entity, StickingProjectileType type) {
        if (entity.level().isClientSide) {
            return;
        }
        entity.getCapability(StickingProjectilesCapabilityProvider.STICKING_PROJECTILES).ifPresent(cap -> {
            int count = cap.getCount(type) + 1;
            cap.setCount(type, count);
            sync(entity, type, count);
        });
    }

    public static void tick(LivingEntity entity) {
        entity.getCapability(StickingProjectilesCapabilityProvider.STICKING_PROJECTILES).ifPresent(cap -> {
            Iterator<Map.Entry<StickingProjectileType, Sticking>> it = cap.stuck.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<StickingProjectileType, Sticking> entry = it.next();
                Sticking s = entry.getValue();
                if (s.removeTime <= 0) {
                    s.removeTime = entry.getKey().removeDelay().applyAsInt(s.count);
                }
                --s.removeTime;
                if (s.removeTime <= 0) {
                    s.count--;
                    if (s.count <= 0) {
                        it.remove();
                    }
                    sync(entity, entry.getKey(), Math.max(0, s.count));
                }
            }
        });
    }

    public static void syncTo(ServerPlayer player, LivingEntity target) {
        target.getCapability(StickingProjectilesCapabilityProvider.STICKING_PROJECTILES).ifPresent(cap ->
                cap.stuck.forEach((type, s) -> ModNetwork.sendToPlayer(player, new SyncStickingProjectilePacket(target.getId(), type.id(), s.count))));
    }

    private static void sync(LivingEntity entity, StickingProjectileType type, int count) {
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncStickingProjectilePacket(entity.getId(), type.id(), count));
    }
}