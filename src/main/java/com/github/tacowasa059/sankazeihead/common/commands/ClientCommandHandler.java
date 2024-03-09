package com.github.tacowasa059.sankazeihead.common.commands;


import com.github.tacowasa059.sankazeihead.client.CommandPacket;
import com.github.tacowasa059.sankazeihead.client.PacketHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;


public class ClientCommandHandler {

    // クライアント側でのコマンド
    // サーバーにpacketを送る
    public static void onRegisterClientCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sankazeihead")
                .requires(context -> context.hasPermissionLevel(4))
                .then(Commands.literal("Filled_By_Player")
                        .then(Commands.literal("on")
                                .executes(context -> {
                                    PacketHandler.INSTANCE.sendToServer(new CommandPacket(0));
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("off")
                                .executes(context -> {
                                    PacketHandler.INSTANCE.sendToServer(new CommandPacket(1));
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
                .then(Commands.literal("Filled_With_Block")
                        .then(Commands.literal("on")
                                .executes(context -> {
                                    PacketHandler.INSTANCE.sendToServer(new CommandPacket(2));
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("off")
                                .executes(context -> {
                                    PacketHandler.INSTANCE.sendToServer(new CommandPacket(3));
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
                .then(Commands.literal("resetScore")
                        .executes(context -> {
                            PacketHandler.INSTANCE.sendToServer(new CommandPacket(4));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("getConfig")
                        .executes(context -> {
                            PacketHandler.INSTANCE.sendToServer(new CommandPacket(5));

                            return Command.SINGLE_SUCCESS;
                        }));

        event.getDispatcher().register(builder);
    }
}
