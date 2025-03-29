package com.alexdl.picasso.item.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class JarOfPaintItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }

        return 0xFFFF0000;
    }
}
