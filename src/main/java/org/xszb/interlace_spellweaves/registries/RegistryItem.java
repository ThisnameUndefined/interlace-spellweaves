package org.xszb.interlace_spellweaves.registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.item.HighEvokerSpellBook;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;

import java.util.Collection;

public class RegistryItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InterlaceSpellWeaves.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    //不中立伏兵
    public static final RegistryObject<ForgeSpawnEggItem> FROSTBONE_SPAWN_EGG = ITEMS.register("frost_bone_spawn_egg", () -> new ForgeSpawnEggItem(RegistryEntity.FROSTBONE, 6842447, 15066584, ItemPropertiesHelper.material().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> NAMELESS_SPAWN_EGG = ITEMS.register("nameless_wizards_spawn_egg", () -> new ForgeSpawnEggItem(RegistryEntity.NAMELESS, 6842147, 15076584, ItemPropertiesHelper.material().stacksTo(64)));



    public static final RegistryObject<Item> SPELL_FORGE_BLOCK = ITEMS.register("spell_forge", () -> new BlockItem(RegistryBlock.SPELL_FORGE_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ALTAR_OF_NAMELESE = ITEMS.register("altar_of_nameless", () -> new BlockItem(RegistryBlock.ALTAR_OF_NAMELESE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));


    public static final RegistryObject<Item> TOTEMRITE_ITEM = ITEMS.register("hollow_totem", () -> new Item(ItemPropertiesHelper.material()));
    public static final RegistryObject<Item> RUNE_WEAVE = ITEMS.register("rune_weave", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.EPIC)));


    public static final RegistryObject<Item> NAMELESS_HELMET = ITEMS.register("nameless_helmet", () -> new NamelessArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.equipment().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> NAMELESS_CHESTPLATE = ITEMS.register("nameless_chestplate", () -> new NamelessArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.equipment().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> NAMELESS_LEGGINGS = ITEMS.register("nameless_leggings", () -> new NamelessArmorItem(ArmorItem.Type.LEGGINGS, ItemPropertiesHelper.equipment().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> NAMELESS_BOOTS = ITEMS.register("nameless_boots", () -> new NamelessArmorItem(ArmorItem.Type.BOOTS, ItemPropertiesHelper.equipment().rarity(Rarity.EPIC)));


    public static final RegistryObject<Item> HIGH_EVOKER_SPELL_BOOK = ITEMS.register("high_evoker_spell_book", HighEvokerSpellBook::new);

    public static final RegistryObject<Item> FORSAKEN_BOOK = ITEMS.register("forsaken_grimoire", () -> new UniqueSpellBook(
            new SpellDataRegistryHolder[]{
                    new SpellDataRegistryHolder(RegistrySpell.TP, 1)},
            0).withSpellbookAttributes(new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, (double)100.0F, AttributeModifier.Operation.ADDITION)})

    );


    public static Collection<RegistryObject<Item>> getItems() {
        return ITEMS.getEntries();
    }

}
