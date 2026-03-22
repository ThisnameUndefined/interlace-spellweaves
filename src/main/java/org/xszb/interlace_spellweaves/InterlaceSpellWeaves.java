package org.xszb.interlace_spellweaves;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.api.registry.RegistrySchool;
import org.xszb.interlace_spellweaves.config.MainConfig;
import org.xszb.interlace_spellweaves.config.NameLessWizardConfig;
import org.xszb.interlace_spellweaves.gui.spell_forge.SpellForgeScreen;
import org.xszb.interlace_spellweaves.recipe.SpellScrollIngredient;
import org.xszb.interlace_spellweaves.registries.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(InterlaceSpellWeaves.MODID)
public class InterlaceSpellWeaves {

    public static final String MODID = "iss_csw";

    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public InterlaceSpellWeaves() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        String configFolder = InterlaceSpellWeaves.MODID + "/";



        RegistryBlock.register(modEventBus);
        RegistryItem.register(modEventBus);
        RegistryEntity.register(modEventBus);

        RegistryEnchantments.register(modEventBus);
        RegistryAttribute.register(modEventBus);
        RegistrySound.register(modEventBus);
        RegistryParticle.register(modEventBus);
        RegistrySchool.register(modEventBus);
        RegistryRecipe.register(modEventBus);
        RegistryMenu.register(modEventBus);
        RegistryEffect.register(modEventBus);
        RegistrySpell.register(modEventBus);
        RegistryCreativeTab.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onCommonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.SPEC,configFolder + "common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,NameLessWizardConfig.SPEC,configFolder + "nameless_wizard.toml");


        MinecraftForge.EVENT_BUS.register(this);


    }

    @SuppressWarnings("removal")
    private void clientSetup(final FMLClientSetupEvent e) {
        MenuScreens.register(RegistryMenu.SPELL_FORGE_MENU.get(), SpellForgeScreen::new);
    }


    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CraftingHelper.register(
                    ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "spell_scroll"),
                    SpellScrollIngredient.Serializer.INSTANCE
            );
        });
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, path);
    }
}
