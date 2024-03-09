package com.github.tacowasa059.sankazeihead.client;

import com.github.tacowasa059.sankazeihead.common.config.ExecuteConfig;
import com.github.tacowasa059.sankazeihead.server.ScoreBoardHandler;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CommandPacket {
    private int command;

    public CommandPacket(int command) {
        this.command = command;
    }

    public static void encode(CommandPacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.command);
    }

    public static CommandPacket decode(PacketBuffer buffer) {
        return new CommandPacket(buffer.readInt());
    }
    public static void handle(CommandPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            if (sender != null) {
                int commandToExecute = msg.command;
                switch (commandToExecute){
                    case 0: {
                        PlayerExecutedSwitch(sender,true);
                        break;
                    }
                    case 1:{
                        PlayerExecutedSwitch(sender,false);
                        break;
                    }
                    case 2:{
                        ItemExecutedSwitch(sender,true);
                        break;
                    }
                    case 3:{
                        ItemExecutedSwitch(sender,false);
                        break;
                    }
                    case 4:{
                        ScoreBoardHandler.resetScoreboard();
                        sender.sendMessage(createColoredText("[SankaZeiHeadMod] スコアをリセットしました", TextFormatting.GREEN),Util.DUMMY_UUID);
                        break;
                    }
                    case 5:
                    {
                        String item_config= ExecuteConfig.item_executed.get()?"ON":"OFF";
                        String player_config=ExecuteConfig.player_executed.get()?"ON":"OFF";
                        sender.sendMessage(createColoredText("[SankaZeiHeadMod]オフハンドブロックでのブロック置換:"+item_config, TextFormatting.GREEN), Util.DUMMY_UUID);
                        sender.sendMessage(createColoredText("[SankaZeiHeadMod]参加勢によるブロック置換:"+player_config, TextFormatting.GREEN), Util.DUMMY_UUID);
                        break;
                    }

                }
            }
        });
        context.setPacketHandled(true);
    }
    public static void ItemExecutedSwitch(ServerPlayerEntity sender, boolean on) {
        String status=on?"オン":"オフ";
        if(!ExecuteConfig.item_executed.get()==on){
            ExecuteConfig.item_executed.set(on);
            ExecuteConfig.item_executed.save();
            // falseのとき実行者のみにフィードバック
            sender.sendMessage(createColoredText("[SankaZeiHeadMod] オフハンドブロックでのブロック置換が"+status+"になりました", TextFormatting.GREEN), Util.DUMMY_UUID);
            ScoreBoardHandler.setup();
        }
        else{
            sender.sendMessage(createColoredText("[SankaZeiHeadMod] オフハンドブロックでのブロック置換は既に"+status+"になっています", TextFormatting.RED), Util.DUMMY_UUID);
        }
    }

    public static void PlayerExecutedSwitch(ServerPlayerEntity sender,boolean on) {
        String status=on?"オン":"オフ";
        if(!ExecuteConfig.player_executed.get()==on){
            ExecuteConfig.player_executed.set(on);
            ExecuteConfig.player_executed.save();
            // falseのとき実行者のみにフィードバック
            sender.sendMessage(createColoredText("[SankaZeiHeadMod] 参加勢によるブロック置換が"+status+"になりました", TextFormatting.GREEN), Util.DUMMY_UUID);
            ScoreBoardHandler.setup();
        }
        else{
            sender.sendMessage(createColoredText("[SankaZeiHeadMod] 参加勢によるブロック置換は既に"+status+"になっています", TextFormatting.RED), Util.DUMMY_UUID);
        }
    }
    private static ITextComponent createColoredText(String text, TextFormatting color) {
        return new StringTextComponent(text).mergeStyle(color);
    }
}
