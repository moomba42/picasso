package com.alexdl.picasso;

import com.alexdl.picasso.block.PicassoBlocks;
import com.alexdl.picasso.block.entity.PicassoBlockEntities;
import com.alexdl.picasso.component.PicassoComponents;
import com.alexdl.picasso.item.PicassoItems;
import com.alexdl.picasso.item.color.JarOfPaintItemColor;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Picasso.MOD_ID)
public class Picasso {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "picasso";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Picasso(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Picasso) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        PicassoItems.register(modEventBus);
        PicassoBlocks.register(modEventBus);
        PicassoBlockEntities.register(modEventBus);
        PicassoComponents.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::buildCreativeTabContents);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(PicassoItems.HORSEHAIR);
            event.accept(PicassoItems.GLASS_JAR);
            event.accept(PicassoItems.JAR_OF_PAINT);
        }
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(PicassoItems.PAINTBRUSH_SMALL);
            event.accept(PicassoItems.PAINTBRUSH_MEDIUM);
            event.accept(PicassoItems.PAINTBRUSH_BIG);
        }
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(PicassoBlocks.PAINT_MIXER);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
            event.register(new JarOfPaintItemColor(), PicassoItems.JAR_OF_PAINT);
        }
    }
}
