package com.alexdl.picasso.datagen;

import com.alexdl.picasso.component.PicassoComponents;
import com.alexdl.picasso.item.PicassoItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.alexdl.picasso.Utils.picassoResource;

public class PicassoRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public PicassoRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput output) {
        ItemStack result = new ItemStack((ItemLike) PicassoItems.JAR_OF_PAINT);
        result.set(PicassoComponents.PAINT_COLOR, 0xFFFF0000);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
                .requires(Items.RED_DYE)
                .requires(Items.EGG)
                .requires(PicassoItems.GLASS_JAR)
                .unlockedBy("has_glass_jar", has(PicassoItems.GLASS_JAR))
                .save(output, picassoResource("jar_of_paint_red_dye"));
    }
}
