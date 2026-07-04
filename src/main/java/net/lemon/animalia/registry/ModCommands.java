package net.lemon.animalia.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.lemon.animalia.player.HolonetCapabilityProvider;
import net.lemon.animalia.player.network.DiscoverSpeciesPacket;
import net.lemon.animalia.player.network.ModNetwork;
import net.lemon.animalia.player.network.SyncAllDiscoveriesPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("holonet")
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                                cap.replaceAll(new HashSet<>());
                                ModNetwork.sendToPlayer(player, new SyncAllDiscoveriesPacket(cap.getAll()));
                            });
                            player.sendSystemMessage(Component.literal("Holonet discoveries reset."));
                            return 1;
                        })
                )
                .then(Commands.literal("discover")
                        .then(Commands.argument("entity", ResourceLocationArgument.id())
                                // No gender provided — discover both
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ResourceLocation id = ResourceLocationArgument.getId(context, "entity");
                                    player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                                        cap.discover(id, 0);
                                        cap.discover(id, 1);
                                        ModNetwork.sendToPlayer(player, new DiscoverSpeciesPacket(id, 0));
                                        ModNetwork.sendToPlayer(player, new DiscoverSpeciesPacket(id, 1));
                                    });
                                    player.sendSystemMessage(Component.literal("Discovered: " + id + " (both genders)"));
                                    return 1;
                                })
                                // Gender provided — discover only that one
                                .then(Commands.argument("gender", IntegerArgumentType.integer(0, 1))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ResourceLocation id = ResourceLocationArgument.getId(context, "entity");
                                            int gender = IntegerArgumentType.getInteger(context, "gender");
                                            player.getCapability(HolonetCapabilityProvider.HOLONET_CAPABILITY).ifPresent(cap -> {
                                                cap.discover(id, gender);
                                                ModNetwork.sendToPlayer(player, new DiscoverSpeciesPacket(id, gender));
                                            });
                                            player.sendSystemMessage(Component.literal("Discovered: " + id + " (gender " + gender + ")"));
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}