package com.alexdl.picasso;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class Utils {
    public static void addRandomSpreadMovement(ItemEntity entity, RandomSource random) {
        Vec3 delta = new Vec3(
                (random.nextFloat() - random.nextFloat()) * 0.1F,
                random.nextFloat() * 0.05F,
                (random.nextFloat() - random.nextFloat()) * 0.1F
        );
        entity.setDeltaMovement(entity.getDeltaMovement().add(delta));
    }

    public static ResourceLocation picassoResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Picasso.MOD_ID, path);
    }
}
