package com.github.tacowasa059.sankazeihead;

import com.github.tacowasa059.sankazeihead.client.PacketHandler;
import com.github.tacowasa059.sankazeihead.common.core.block.HeadBlock;
import com.github.tacowasa059.sankazeihead.common.config.ExecuteConfig;
import com.github.tacowasa059.sankazeihead.common.core.item.HeadBlockItem;
import com.github.tacowasa059.sankazeihead.common.core.ItemGroup.ModItemGroup;
import com.github.tacowasa059.sankazeihead.common.core.init.BlockInit;
import com.github.tacowasa059.sankazeihead.common.core.init.SankaZeiList;
import com.github.tacowasa059.sankazeihead.server.ScoreBoardHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SankaZeiHead.MOD_ID)
public class SankaZeiHead {
    public static final  String MOD_ID="sankazeihead";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SankaZeiHead() {
        //ModBlocks.register(modEventBus);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExecuteConfig.spec,"sankazeiheadmod.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        for (SankaZeiList sankazei : SankaZeiList.values()) {
            BlockInit.HEAD_Blocks.put(sankazei.name(), BlockInit.BLOCKS.register(sankazei.name(), HeadBlock::new));
            BlockInit.ITEMS.register(sankazei.name(), () -> new HeadBlockItem(BlockInit.HEAD_Blocks.get(sankazei.name()).get(), new Item.Properties()
                    .group(ModItemGroup.SankaZei_MOD)));
        }
        BlockInit.BLOCKS.register(bus);
        BlockInit.ITEMS.register(bus);
        PacketHandler.registerPackets();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("sankazeihead", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        if(ExecuteConfig.item_executed.get()||ExecuteConfig.player_executed.get()) ScoreBoardHandler.setup();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }


}
