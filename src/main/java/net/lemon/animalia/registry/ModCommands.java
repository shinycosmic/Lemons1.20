package net.lemon.animalia.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.lemon.animalia.client.player.HolonetCapabilityProvider;
import net.lemon.animalia.client.player.network.DiscoverSpeciesPacket;
import net.lemon.animalia.client.player.network.ModNetwork;
import net.lemon.animalia.client.player.network.SyncAllDiscoveriesPacket;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class ModCommands {

    /** Suggests only entity IDs that are registered in HolonetEntities. */
    private static final SuggestionProvider<CommandSourceStack> HOLONET_ENTITY_SUGGESTIONS = (context, builder) -> {
        Stream<ResourceLocation> ids = getRegisteredHolonetIds();
        return SharedSuggestionProvider.suggestResource(ids, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("holonet")
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                                cap.replaceAll(new HashSet<>());
                                ModNetwork.sendToPlayer(player, new SyncAllDiscoveriesPacket(cap.getAll(), cap.isLoadedFirstTime()));
                            });
                            player.sendSystemMessage(Component.translatable("commands.animalia.holonet.reset"));
                            return 1;
                        })
                )
                .then(Commands.literal("discoverall")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                                for (Scannable.AppName app : Scannable.AppName.values()) {
                                    for (EntityType<?> entityType : HolonetEntities.getAllForApp(app)) {
                                        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
                                        cap.discover(id, -1);
                                        cap.discover(id, 0);
                                        cap.discover(id, 1);
                                    }
                                }
                                ModNetwork.sendToPlayer(player, new SyncAllDiscoveriesPacket(cap.getAll(), cap.isLoadedFirstTime()));
                            });
                            player.sendSystemMessage(Component.translatable("commands.animalia.holonet.discoverall"));
                            return 1;
                        })
                )
                .then(Commands.literal("discover")
                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                .suggests(HOLONET_ENTITY_SUGGESTIONS)
                                // No gender provided — discover both
                                .executes(context -> discoverEntity(context, -1))
                                // Gender provided — discover only that one
                                .then(Commands.argument("gender", IntegerArgumentType.integer(0, 1))
                                        .executes(context -> discoverEntity(context, IntegerArgumentType.getInteger(context, "gender")))
                                )
                        )
                )
        );
    }

    private static int discoverEntity(CommandContext<CommandSourceStack> context, int gender) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ResourceLocation id = ResourceLocationArgument.getId(context, "entity");

        // Validate: must be a registered Holonet entity
        if (!isRegisteredHolonetEntity(id)) {
            player.sendSystemMessage(Component.translatable("commands.animalia.holonet.invalid", id.toString()).withStyle(ChatFormatting.RED));
            return 0;
        }

        player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
            if (gender == -1) {
                cap.discover(id, -1);
                cap.discover(id, 0);
                cap.discover(id, 1);
                ModNetwork.sendToPlayer(player, new SyncAllDiscoveriesPacket(cap.getAll(), cap.isLoadedFirstTime()));
                ModNetwork.sendToPlayer(player, new DiscoverSpeciesPacket(id, -1));
            } else {
                cap.discover(id, gender);
                ModNetwork.sendToPlayer(player, new DiscoverSpeciesPacket(id, gender));
            }
        });

        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
        Component name = type != null ? type.getDescription() : Component.literal(id.toString());
        player.sendSystemMessage(Component.translatable("commands.animalia.holonet.discovered", name));
        return 1;
    }

    /** Get all ResourceLocations of entities registered in HolonetEntities. */
    private static Stream<ResourceLocation> getRegisteredHolonetIds() {
        List<EntityType<?>> all = new java.util.ArrayList<>();
        for (Scannable.AppName app : Scannable.AppName.values()) {
            all.addAll(HolonetEntities.getAllForApp(app));
        }
        return all.stream()
                .map(ForgeRegistries.ENTITY_TYPES::getKey)
                .filter(java.util.Objects::nonNull);
    }

    /** Check if a ResourceLocation is a valid Holonet-registered entity. */
    private static boolean isRegisteredHolonetEntity(ResourceLocation id) {
        return getRegisteredHolonetIds().anyMatch(registered -> registered.equals(id));
    }
}