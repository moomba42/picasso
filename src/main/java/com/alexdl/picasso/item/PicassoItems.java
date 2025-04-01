package com.alexdl.picasso.item;

import com.alexdl.picasso.Picasso;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PicassoItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Picasso.MOD_ID);

    public static final DeferredItem<Item> HORSEHAIR = ITEMS.register("horsehair",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> PAINTBRUSH_SMALL = ITEMS.register("paintbrush_small",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PAINTBRUSH_MEDIUM = ITEMS.register("paintbrush_medium",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PAINTBRUSH_BIG = ITEMS.register("paintbrush_big",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GLASS_JAR = ITEMS.register("glass_jar",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> JAR_OF_PAINT = ITEMS.register("jar_of_paint",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
