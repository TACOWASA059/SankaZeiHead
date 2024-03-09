package com.github.tacowasa059.sankazeihead.server;

import com.github.tacowasa059.sankazeihead.SankaZeiHead;
import com.github.tacowasa059.sankazeihead.common.config.ExecuteConfig;
import com.github.tacowasa059.sankazeihead.common.core.item.HeadBlockItem;
import com.github.tacowasa059.sankazeihead.common.core.init.BlockInit;
import com.github.tacowasa059.sankazeihead.common.core.init.SankaZeiList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//地面を置き換えるとき用
@Mod.EventBusSubscriber(modid = SankaZeiHead.MOD_ID)
public class PlayerMovementEventHandler {
    private static Map<String, SankaZeiList> enumMap = new HashMap<>();
    static {
        for (SankaZeiList enumValue : SankaZeiList.values()) {
            enumMap.put(enumValue.name(), enumValue);
        }
    }

    //オフハンドにヘッドを持ってるときは、そのブロックで地面を置き換える
    //置き換えれなかったときは、そのプレイヤーのブロックで置き換える
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {

            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            if(player.isSpectator()||player.isCreative())return;
            Item item=player.getItemStackFromSlot(EquipmentSlotType.OFFHAND).getItem();
            boolean replaced=false;
            if(item instanceof HeadBlockItem){
                HeadBlockItem blockItem=(HeadBlockItem) item;
                if(ExecuteConfig.item_executed.get()) replaced=setBlockBelowPlayer(player,blockItem.getBlock());
            }
            if(ExecuteConfig.player_executed.get()&&!replaced)setBlockBelowPlayer(player,null);
        }
    }

    //deg_block ブロックの指定があるときはそのブロックで置き換える
    //ないときはプレイヤー名でブロックを探して置き換える。
    public static boolean setBlockBelowPlayer(ServerPlayerEntity player,Block deg_block) {
        // プレイヤーの現在位置を取得
        Vector3d playerPos = player.getPositionVec();
        String player_name;

        if(deg_block==null)player_name=processString(player.getName().getString());
        else player_name = extract_block_name(deg_block.getTranslationKey()).toString();

        // プレイヤーの真下の位置を計算
        BlockPos blockBelowPlayerPos = new BlockPos(playerPos).down();

        Block currentBlock= player.world.getBlockState(blockBelowPlayerPos).getBlock();
        //System.out.println(currentBlock.getTranslationKey());
        // 空気ではないとき
        if (currentBlock!= Blocks.AIR&&currentBlock!=Blocks.CAVE_AIR&&currentBlock!=Blocks.VOID_AIR) {
            RegistryObject<Block> registryObject=BlockInit.HEAD_Blocks.get(player_name);

            //nullのときskip
            if(registryObject==null) return false;

            Block block=registryObject.get();

            //同じブロックのときは変更しない
            if(block.equals(currentBlock)) return false;
            String currentBlockName=extract_block_name(currentBlock.getTranslationKey().toString());
            BlockState newBlockState= block.getDefaultState();
            ScoreBoardHandler.incrementScore(player_name,1);
            if(currentBlockName!=null) ScoreBoardHandler.incrementScore(currentBlockName,-1);
            player.world.setBlockState(blockBelowPlayerPos, newBlockState);
            return true;
        }
        return false;
    }

    // 大文字->小文字
    // 数字から始まるときはアンダースコアをつける
    public static String processString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = input.charAt(0);
        if (Character.isDigit(firstChar)) {
            input = "_" + input;
        }

        return input.toLowerCase();
    }

    //blockのtranslationkeyからblock名を取得する
    public static String extract_block_name(String input) {

        // 正規表現パターンを定義
        Pattern pattern = Pattern.compile("^(.+)\\.(.+)$");
        Matcher matcher = pattern.matcher(input);

        // マッチした部分を取得
        if (matcher.find()) {
            String beforeLastDot = matcher.group(1); // 一番右の.の前の部分
            String afterLastDot = matcher.group(2);  // 一番右の.以降の部分
            if(beforeLastDot.equals("block.sankazeihead")||beforeLastDot.equals("sankazeihead")){
                return afterLastDot;
            }
        }
        return null;
    }
}
