package org.xszb.interlace_spellweaves.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.entity.mobs.frostbone.FrostboneEntity;
import org.xszb.interlace_spellweaves.entity.mobs.polorbear.RideablePolarBear;
import org.xszb.interlace_spellweaves.entity.spells.rushvex.RushVexEntity;
import org.xszb.interlace_spellweaves.recipe.EnchantmentIngredient;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(RegistryEntity.FROSTBONE.get(), FrostboneEntity.prepareAttributes().build());
        event.put(RegistryEntity.SUMMONED_POLAR_BEAR.get(), RideablePolarBear.createAttributes().build());
        event.put(RegistryEntity.VEX.get(), RushVexEntity.prepareAttributes().build());

        event.put(RegistryEntity.NAMELESS.get(), NamelessWizardsEntity.prepareAttributes().build());
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
            CraftingHelper.register(
                    ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "enchantment"),
                    EnchantmentIngredient.Serializer.INSTANCE
            );
        });
    }

}
