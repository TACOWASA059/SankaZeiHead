package com.github.tacowasa059.sankazeihead.common.core.init;

import com.github.tacowasa059.sankazeihead.SankaZeiHead;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SankaZeiHead.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            SankaZeiHead.MOD_ID);

    public static HashMap<String, RegistryObject<Block>> HEAD_Blocks= new HashMap<>();;
}
