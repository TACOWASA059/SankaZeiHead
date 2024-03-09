package com.github.tacowasa059.sankazeihead.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

//足元のブロック置き換えON/OFF切り替え
public class ExecuteConfig {
    public static final ForgeConfigSpec.Builder buider=new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec spec;

    public static final Boolean default_item_executed=false;
    public static ForgeConfigSpec.ConfigValue<Boolean> item_executed;
    public static final Boolean default_player_executed=false;

    public static ForgeConfigSpec.ConfigValue<Boolean> player_executed;
    static {
        buider.push("Config for sankazei head mod");
        item_executed=buider.comment("Whether to turn on block replacement on off-hand block.").define("item_executed",default_item_executed);
        player_executed=buider.comment("Whether to turn on block replacement by participants.").define("player_executed",default_player_executed);

        buider.pop();
        spec= buider.build();
    }
}
