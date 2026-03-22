package org.xszb.interlace_spellweaves.registries;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.recipe.SpellMixRecipe;

public class RegistryRecipe {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, InterlaceSpellWeaves.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, InterlaceSpellWeaves.MODID);



    public static final RegistryObject<RecipeType<SpellMixRecipe>> SPELL_MIX_RECIPE = RECIPE_TYPES.register("mix_spell_recipe", () -> new RecipeType<>()
    {
        public String toString() {
            return InterlaceSpellWeaves.MODID + ":" + "mix_spell_recipe";
        }
    });

    public static final RegistryObject<RecipeSerializer<SpellMixRecipe>> SPELL_MIX_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("mix_spell_recipe",
                    SpellMixRecipe.Serializer::new);

    public static void register(IEventBus eventBus){
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }


}
