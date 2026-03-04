package org.xszb.interlace_spellweaves.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.gui.spell_forge.SpellForgeMenu;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;
import org.xszb.interlace_spellweaves.registries.RegistryMenu;

import static org.xszb.interlace_spellweaves.jei.SpellForgeRecipeCategory.SPELL_FORGE_RECIPE_RECIPE_TYPE;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    public static final ResourceLocation SPELL_FORGE_GUI = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/gui/spell_forge.png");


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new SpellForgeRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CMRecipes modRecipes = new CMRecipes();
        registration.addRecipes(SPELL_FORGE_RECIPE_RECIPE_TYPE, modRecipes.getSpellMixRecipes());

    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(SpellForgeMenu.class, RegistryMenu.SPELL_FORGE_MENU.get(), SPELL_FORGE_RECIPE_RECIPE_TYPE, 0, 6, 9, 36);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryBlock.SPELL_FORGE_BLOCK.get()), SPELL_FORGE_RECIPE_RECIPE_TYPE);
    }
}
