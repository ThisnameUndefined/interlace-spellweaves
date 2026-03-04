package org.xszb.interlace_spellweaves.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import org.xszb.interlace_spellweaves.recipe.SpellMixRecipe;
import org.xszb.interlace_spellweaves.registries.RegistryRecipe;

import java.util.List;

public class CMRecipes
{
    private final RecipeManager recipeManager;

    public CMRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != null) {
            this.recipeManager = level.getRecipeManager();
        } else {
            throw new NullPointerException("minecraft world must not be null.");
        }
    }

    public List<SpellMixRecipe> getSpellMixRecipes() {
        return recipeManager.getAllRecipesFor(RegistryRecipe.SPELL_MIX_RECIPE.get()).stream().toList();
    }

}
