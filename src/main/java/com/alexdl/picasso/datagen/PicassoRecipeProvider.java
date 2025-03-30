package com.alexdl.picasso.datagen;

import com.alexdl.picasso.component.PicassoComponents;
import com.alexdl.picasso.item.PicassoItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
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
        for (DyeColor dyeColor : DyeColor.values()) {
            String recipeName = "jar_of_paint_from_" + dyeColor.toString().toLowerCase() + "_dye";
            int colorValue = dyeColor.getTextColor() | 0xFF000000;
            Item dyeIngredient = DyeItem.byColor(dyeColor);

            ItemStack result = new ItemStack((ItemLike) PicassoItems.JAR_OF_PAINT);
            result.set(PicassoComponents.PAINT_COLOR, colorValue);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
                    .requires(dyeIngredient)
                    .requires(Items.EGG)
                    .requires(PicassoItems.GLASS_JAR)
                    .unlockedBy("has_glass_jar", has(PicassoItems.GLASS_JAR))
                    .save(output, picassoResource(recipeName));
        }
    }
}
