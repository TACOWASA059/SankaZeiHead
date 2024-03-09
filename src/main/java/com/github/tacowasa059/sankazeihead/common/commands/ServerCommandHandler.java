package com.github.tacowasa059.sankazeihead.common.commands;

import com.github.tacowasa059.sankazeihead.common.config.ExecuteConfig;
import com.github.tacowasa059.sankazeihead.server.ScoreBoardHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;


public class ServerCommandHandler {
    //サーバー側でのコマンド
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sankazeihead")
                .requires(context -> context.hasPermissionLevel(4))
                .then(Commands.literal("Filled_By_Player")
                    .then(Commands.literal("on")
                        .executes(context -> {
                            PlayerExecutedSwitch(context,true);
                            return Command.SINGLE_SUCCESS;
                        }))
                    .then(Commands.literal("off")
                        .executes(context -> {
                            PlayerExecutedSwitch(context,false);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(Commands.literal("Filled_With_Block")
                    .then(Commands.literal("on")
                        .executes(context -> {
                            ItemExecutedSwitch(context,true);
                            return Command.SINGLE_SUCCESS;
                        }))
                    .then(Commands.literal("off")
                        .executes(context -> {
                            ItemExecutedSwitch(context,false);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(Commands.literal("resetScore")
                    .executes(context -> {
                        ScoreBoardHandler.resetScoreboard();
                        context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod] スコアをリセットしました", TextFormatting.GREEN), false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("getConfig")
                    .executes(context -> {
                        String item_config= ExecuteConfig.item_executed.get()?"ON":"OFF";
                        String player_config=ExecuteConfig.player_executed.get()?"ON":"OFF";
                        context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod]オフハンドブロックでのブロック置換:"+item_config, TextFormatting.GREEN), false);
                        context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod]参加勢によるブロック置換:"+player_config, TextFormatting.GREEN), false);
                        return Command.SINGLE_SUCCESS;
                    })
                );

        event.getDispatcher().register(builder);
    }

    public static void ItemExecutedSwitch(CommandContext<CommandSource> context, boolean on) {
        String status=on?"オン":"オフ";
        if(!ExecuteConfig.item_executed.get()==on){
            ExecuteConfig.item_executed.set(on);
            ExecuteConfig.item_executed.save();
            // falseのとき実行者のみにフィードバック
            context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod] オフハンドブロックでのブロック置換が"+status+"になりました", TextFormatting.GREEN), false);
            ScoreBoardHandler.setup();
        }
        else{
            context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod] オフハンドブロックでのブロック置換は既に"+status+"になっています", TextFormatting.RED), false);
        }
    }

    public static void PlayerExecutedSwitch(CommandContext<CommandSource> context,boolean on) {
        String status=on?"オン":"オフ";
        if(!ExecuteConfig.player_executed.get()==on){
            ExecuteConfig.player_executed.set(on);
            ExecuteConfig.player_executed.save();
            // falseのとき実行者のみにフィードバック
            context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod] 参加勢によるブロック置換が"+status+"になりました", TextFormatting.GREEN), false);
            ScoreBoardHandler.setup();
        }
        else{
            context.getSource().sendFeedback(createColoredText("[SankaZeiHeadMod] 参加勢によるブロック置換は既に"+status+"になっています", TextFormatting.RED), false);
        }
    }

    private static ITextComponent createColoredText(String text, TextFormatting color) {
        return new StringTextComponent(text).mergeStyle(color);
    }
}
