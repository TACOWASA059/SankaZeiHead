package com.github.tacowasa059.sankazeihead.common.commands;

import com.github.tacowasa059.sankazeihead.SankaZeiHead;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = SankaZeiHead.MOD_ID)
public class CommandRegister {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            ClientCommandHandler.onRegisterClientCommand(event);
        } else {
            ServerCommandHandler.onRegisterCommand(event);
        }
    }
}