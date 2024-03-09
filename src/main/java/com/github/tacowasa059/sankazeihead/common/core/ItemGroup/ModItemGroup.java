package com.github.tacowasa059.sankazeihead.common.core.ItemGroup;

import com.github.tacowasa059.sankazeihead.SankaZeiHead;
import com.github.tacowasa059.sankazeihead.common.core.init.BlockInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup extends ItemGroup {

    public static final ModItemGroup SankaZei_MOD = new ModItemGroup(ModItemGroup.GROUPS.length,
            SankaZeiHead.MOD_ID);

    public ModItemGroup(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BlockInit.HEAD_Blocks.get("roadhog_kun").get());
    }

}