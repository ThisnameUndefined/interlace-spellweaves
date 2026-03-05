package org.xszb.interlace_spellweaves.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.recipe.SpellMixRecipe;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.xszb.interlace_spellweaves.jei.JeiPlugin.SPELL_FORGE_GUI;

public class SpellForgeRecipeCategory implements IRecipeCategory<SpellMixRecipe> {
    public static final RecipeType<SpellMixRecipe> SPELL_FORGE_RECIPE_RECIPE_TYPE = RecipeType.create(InterlaceSpellWeaves.MODID, "spell_forge", SpellMixRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public SpellForgeRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(SPELL_FORGE_GUI,52, 16, 68, 53);
        icon = guiHelper.createDrawableItemStack(new ItemStack(RegistryBlock.SPELL_FORGE_BLOCK.get()));

    }

    @Override
    public RecipeType<SpellMixRecipe> getRecipeType() {
        return SPELL_FORGE_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return RegistryBlock.SPELL_FORGE_BLOCK.get().getName();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpellMixRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 2)
                .addIngredients(recipe.mainspell);

        List<ItemStack> addition1Items = Arrays.stream(recipe.addition1)
                .flatMap(ingredient -> Arrays.stream(ingredient.getItems()))
                .collect(Collectors.toList());

        builder.addSlot(RecipeIngredientRole.INPUT, 3, 2)
                .addItemStacks(addition1Items);

        List<ItemStack> addition2Items = Arrays.stream(recipe.addition2)
                .flatMap(ingredient -> Arrays.stream(ingredient.getItems()))
                .collect(Collectors.toList());

        builder.addSlot(RecipeIngredientRole.INPUT, 49, 2)
                .addItemStacks(addition2Items);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 26, 31)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

}
