package org.xszb.interlace_spellweaves.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryCreativeTab {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InterlaceSpellWeaves.MODID);

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }

    public static final RegistryObject<CreativeModeTab> ITEM_TAB = TABS.register("spellweaves_item", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + InterlaceSpellWeaves.MODID + ".spellweaves_item_tab"))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(RegistryItem.SPELL_FORGE_BLOCK.get());

                entries.accept(RegistryItem.ALTAR_OF_NAMELESE.get());

                entries.accept(RegistryItem.NAMELESS_HELMET.get());
                entries.accept(RegistryItem.NAMELESS_CHESTPLATE.get());
                entries.accept(RegistryItem.NAMELESS_LEGGINGS.get());
                entries.accept(RegistryItem.NAMELESS_BOOTS.get());
                entries.accept(RegistryItem.HIGH_EVOKER_SPELL_BOOK.get());;
                entries.accept(RegistryItem.FORSAKEN_BOOK.get());
                entries.accept(RegistryItem.RUNE_WEAVE.get());
                entries.accept(RegistryItem.NAMELESS_SPAWN_EGG.get());

                entries.accept(RegistryItem.TOTEMRITE_ITEM.get());
                entries.accept(RegistryItem.MANA_PEARL.get());
                entries.accept(RegistryItem.ARCANE_PEARL.get());
                entries.accept(RegistryItem.ABYSS_PEARL.get());

                entries.accept(RegistryItem.FIRE_PEARL.get());
                entries.accept(RegistryItem.ICE_PEARL.get());
                entries.accept(RegistryItem.NATURE_PEARL.get());
                entries.accept(RegistryItem.EVOCATION_PEARL.get());
                entries.accept(RegistryItem.HOLY_PEARL.get());
                entries.accept(RegistryItem.BLOOD_PEARL.get());
                entries.accept(RegistryItem.THUNDER_PEARL.get());
                entries.accept(RegistryItem.END_PEARL.get());

                entries.accept(RegistryItem.CHARGE_SHRIVING_STONE.get());
            })
            .icon(() -> new ItemStack(RegistryItem.SPELL_FORGE_BLOCK.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());

    public static final RegistryObject<CreativeModeTab> SCROLLS_TAB = TABS.register("spellbook_scrolls", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + InterlaceSpellWeaves.MODID + ".spellbook_scrolls_tab"))
            .icon(() -> new ItemStack(ItemRegistry.SCROLL.get()))
            .withTabsBefore(ITEM_TAB.getKey())
            .build());

    @SubscribeEvent
    public static void fillCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == CreativeModeTabs.searchTab() || event.getTab() == SCROLLS_TAB.get()) {
            RegistrySpell.getEnabledSpells().stream()
                    .filter(spellType -> spellType != SpellRegistry.none())
                    .forEach(spell -> {
                        for (int i = spell.getMinLevel(); i <= spell.getMaxLevel(); i++) {
                            var itemstack = new ItemStack(ItemRegistry.SCROLL.get());
                            var spellList = ISpellContainer.createScrollContainer(spell, i, itemstack);
                            event.accept(itemstack);
                        }
                    });
        }
    }
}
