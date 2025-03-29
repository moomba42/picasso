package com.alexdl.picasso.item.color;

import com.alexdl.picasso.component.PicassoComponents;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class JarOfPaintItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }

        Integer color = stack.get(PicassoComponents.PAINT_COLOR);

        if (color == null) {
            return 0xFFFFFFFF;
        }

        return color;
    }
}
