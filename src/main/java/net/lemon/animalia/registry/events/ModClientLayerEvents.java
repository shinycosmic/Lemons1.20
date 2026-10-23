package net.lemon.animalia.registry.events;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.client.player.capability.StickingProjectileType;
import net.lemon.animalia.client.player.capability.StickingProjectileTypes;
import net.lemon.animalia.entity.projectiles.QuillProjectile;
import net.lemon.animalia.entity.render.layer.StickingProjectileLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientLayerEvents {
    private static final Map<StickingProjectileType, Function<LivingEntity, ? extends Entity>> STICKING_PROJECTILES = Map.of(
            StickingProjectileTypes.QUILL, entity -> new QuillProjectile(entity.level(), entity.getX(), entity.getY(), entity.getZ()));

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderDispatcher dispatcher = event.getContext().getEntityRenderDispatcher();
        for (String skin : event.getSkins()) {
            attachStickingProjectiles(event.getPlayerSkin(skin), dispatcher);
        }
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            attachStickingProjectiles(event.getEntityRenderer(type), dispatcher);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void attachStickingProjectiles(@Nullable EntityRenderer<?> renderer, EntityRenderDispatcher dispatcher) {
        if (renderer instanceof LivingEntityRenderer living && living.getModel() instanceof HumanoidModel) {
            STICKING_PROJECTILES.forEach((type, factory) -> living.addLayer(new StickingProjectileLayer(living, dispatcher, type, factory)));
        }
    }
}