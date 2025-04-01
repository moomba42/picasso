package com.alexdl.picasso.block.entity;

import com.alexdl.picasso.Picasso;
import com.alexdl.picasso.block.PicassoBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PicassoBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Picasso.MOD_ID);

    public static final Supplier<BlockEntityType<PaintMixerBlockEntity>> PAINT_MIXER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("paint_mixer", () -> BlockEntityType.Builder.of(
                    PaintMixerBlockEntity::new,
                    PicassoBlocks.PAINT_MIXER.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
