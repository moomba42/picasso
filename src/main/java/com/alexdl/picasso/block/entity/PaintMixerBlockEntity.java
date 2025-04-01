package com.alexdl.picasso.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PaintMixerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SLOT_PAINT_1 = 0;
    private static final int SLOT_PAINT_2 = 1;
    private static final int SLOT_PAINT_3 = 2;
    private static final int SLOT_PAINT_4 = 3;
    private static final int SLOT_PAINT_5 = 4;
    private static final int SLOT_GLASS_JAR = 5;
    private static final int SLOT_OUTPUT = 6;

    public final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final ContainerData data;
    private int paint1weight = 0;
    private int paint2weight = 0;
    private int paint3weight = 0;
    private int paint4weight = 0;
    private int paint5weight = 0;

    public PaintMixerBlockEntity(BlockPos pos, BlockState blockState) {
        super(PicassoBlockEntities.PAINT_MIXER_BLOCK_ENTITY.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case SLOT_PAINT_1 -> paint1weight;
                    case SLOT_PAINT_2 -> paint2weight;
                    case SLOT_PAINT_3 -> paint3weight;
                    case SLOT_PAINT_4 -> paint4weight;
                    case SLOT_PAINT_5 -> paint5weight;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case SLOT_PAINT_1 -> paint1weight = value;
                    case SLOT_PAINT_2 -> paint2weight = value;
                    case SLOT_PAINT_3 -> paint3weight = value;
                    case SLOT_PAINT_4 -> paint4weight = value;
                    case SLOT_PAINT_5 -> paint5weight = value;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    @Override
    public @Nonnull Component getDisplayName() {
        return Component.translatable("block.picasso.paint_mixer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId,
                                                      @Nonnull Inventory playerInventory,
                                                      @Nonnull Player player) {
        return null;
    }

    public void drops() {
        if(level == null) return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag,
                                  @Nonnull HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("paint_mixer.paint1weight", paint1weight);
        tag.putInt("paint_mixer.paint2weight", paint2weight);
        tag.putInt("paint_mixer.paint3weight", paint3weight);
        tag.putInt("paint_mixer.paint4weight", paint4weight);
        tag.putInt("paint_mixer.paint5weight", paint5weight);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag,
                                  @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        paint1weight = tag.getInt("paint_mixer.paint1weight");
        paint2weight = tag.getInt("paint_mixer.paint2weight");
        paint3weight = tag.getInt("paint_mixer.paint3weight");
        paint4weight = tag.getInt("paint_mixer.paint4weight");
        paint5weight = tag.getInt("paint_mixer.paint5weight");
    }

    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
