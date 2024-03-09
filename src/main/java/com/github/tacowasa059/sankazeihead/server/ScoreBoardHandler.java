package com.github.tacowasa059.sankazeihead.server;

import com.github.tacowasa059.sankazeihead.SankaZeiHead;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;


@Mod.EventBusSubscriber(modid = SankaZeiHead.MOD_ID)
public class ScoreBoardHandler {
    private static MinecraftServer minecraftServer;
    private static Scoreboard scoreboard;
    private static ScoreObjective scoreObjective;
    private static final String scorename="headcount";

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        minecraftServer = event.getServer();
    }
    public static void setup(){
        scoreboard = minecraftServer.getScoreboard();
        if(scoreboard==null) return;
        scoreObjective=getScoreObject();
        //0: スコアボードを非表示//1: 右側のサイドバーに表示//2: 右側のプレイヤーリストに表示//3: 上部に表示
        scoreboard.setObjectiveInDisplaySlot(1, scoreObjective);
    }
    public static void removeScoreboard(){
        scoreboard = minecraftServer.getScoreboard();
        if(scoreboard==null) return;
        scoreObjective=getScoreObject();
        scoreboard.removeObjective(scoreObjective);
    }
    public static void resetScoreboard(){
        removeScoreboard();
        setup();
    }
    public static void incrementScore(String strname,int i){
        if(scoreboard==null) return;
        Score score = scoreboard.getOrCreateScore(strname, scoreObjective);
        score.increaseScore(i);
    }
    private static ScoreObjective getScoreObject(){
        // hasObjectiveはDist.clientのみ
        try{
            //スコアボードのスコアを表示する際のレンダリングタイプを指定する列挙型//INTEGER: スコアが整数として表示
            scoreObjective=scoreboard.addObjective(scorename, ScoreCriteria.DUMMY, new TranslationTextComponent("ブロック数"), ScoreCriteria.RenderType.INTEGER);
        }catch (IllegalArgumentException e){scoreObjective=scoreboard.getObjective(scorename);}
        return scoreObjective;
    }
}
