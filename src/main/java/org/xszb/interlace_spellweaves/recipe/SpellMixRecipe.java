package org.xszb.interlace_spellweaves.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.registries.RegistryRecipe;

import java.util.Arrays;

public class SpellMixRecipe implements Recipe<Container> {

    public final Ingredient mainspell;
    public final Ingredient[] addition1;
    public final Ingredient[] addition2;
    public final ResourceLocation id;
    private final ItemStack result;

    public SpellMixRecipe(ResourceLocation id, Ingredient main, Ingredient[] add1, Ingredient[] add2,ItemStack result) {
        this.id = id;
        this.mainspell = main;
        this.addition1 = add1;
        this.addition2 =  add2;
        this.result = result;
    }


    @Override
    public boolean matches(Container container, Level p_44003_) {
        if (container.getContainerSize() < 3) {
            return false;
        }
        boolean mainspellMatches = this.mainspell.test(container.getItem(0));
        boolean addition1Matches = Arrays.stream(this.addition1).anyMatch(addition1 -> addition1.test(container.getItem(1)));
        boolean addition2Matches = Arrays.stream(this.addition2).anyMatch(addition2 -> addition2.test(container.getItem(2)));


        return mainspellMatches && addition1Matches && addition2Matches;
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryRecipe.SPELL_MIX_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RegistryRecipe.SPELL_MIX_RECIPE.get();
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<SpellMixRecipe> {

        @Override
        public SpellMixRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient mainSpell = Ingredient.EMPTY;
            if (json.has("mainspell")) {
                mainSpell = CraftingHelper.getIngredient(json.get("mainspell"), false);
            }

            JsonArray addition1Array = GsonHelper.getAsJsonArray(json, "addition1");
            Ingredient[] addition1 = new Ingredient[addition1Array.size()];
            for (int i = 0; i < addition1Array.size(); i++) {
                addition1[i] = Ingredient.fromJson(addition1Array.get(i));
            }

            JsonArray addition2Array = GsonHelper.getAsJsonArray(json, "addition2");
            Ingredient[] addition2 = new Ingredient[addition2Array.size()];
            for (int i = 0; i < addition2Array.size(); i++) {
                addition2[i] = Ingredient.fromJson(addition2Array.get(i));
            }

            ItemStack result = CraftingHelper.getItemStack(json.get("result").getAsJsonObject(), true, true);

            return new SpellMixRecipe(recipeId, mainSpell, addition1, addition2, result);
        }

        @Nullable
        @Override
        public SpellMixRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            Ingredient mainSpell = Ingredient.EMPTY;
            boolean hasMainSpell = buffer.readBoolean();
            if (hasMainSpell) {
                mainSpell = Ingredient.fromNetwork(buffer);
            }

            int addition1Count = buffer.readVarInt();
            Ingredient[] addition1 = new Ingredient[addition1Count];
            for (int i = 0; i < addition1Count; i++) {
                addition1[i] = Ingredient.fromNetwork(buffer);
            }

            int addition2Count = buffer.readVarInt();
            Ingredient[] addition2 = new Ingredient[addition2Count];
            for (int i = 0; i < addition2Count; i++) {
                addition2[i] = Ingredient.fromNetwork(buffer);
            }

            ItemStack result = buffer.readItem();

            return new SpellMixRecipe(recipeId, mainSpell, addition1, addition2, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SpellMixRecipe recipe) {

            buffer.writeBoolean(recipe.mainspell != null && recipe.mainspell != Ingredient.EMPTY);
            if (recipe.mainspell != null && recipe.mainspell != Ingredient.EMPTY) {
                recipe.mainspell.toNetwork(buffer);
            }

            buffer.writeVarInt(recipe.addition1.length);
            for (Ingredient ingredient : recipe.addition1) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeVarInt(recipe.addition2.length);
            for (Ingredient ingredient : recipe.addition2) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
        }
    }
}
