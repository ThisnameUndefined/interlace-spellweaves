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
import org.xszb.interlace_spellweaves.item.*;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;

import java.util.Collection;
import java.util.List;

public class RegistryItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InterlaceSpellWeaves.MODID);


    //不中立伏兵
    public static final RegistryObject<ForgeSpawnEggItem> FROSTBONE_SPAWN_EGG = ITEMS.register("frost_bone_spawn_egg", () -> new ForgeSpawnEggItem(RegistryEntity.FROSTBONE, 6842447, 15066584, ItemPropertiesHelper.material().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> NAMELESS_SPAWN_EGG = ITEMS.register("nameless_wizards_spawn_egg", () -> new ForgeSpawnEggItem(RegistryEntity.NAMELESS, 6842147, 15076584, ItemPropertiesHelper.material().stacksTo(64)));



    public static final RegistryObject<Item> SPELL_FORGE_BLOCK = ITEMS.register("spell_forge", () -> new BlockItem(RegistryBlock.SPELL_FORGE_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ALTAR_OF_NAMELESE = ITEMS.register("altar_of_nameless", () -> new BlockItem(RegistryBlock.ALTAR_OF_NAMELESE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));


    public static final RegistryObject<Item> TOTEMRITE_ITEM = ITEMS.register("hollow_totem", () -> new Item(ItemPropertiesHelper.material()));
    public static final RegistryObject<Item> RUNE_WEAVE = ITEMS.register("rune_weave", () -> new Item(ItemPropertiesHelper.material().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CHARGE_SHRIVING_STONE = ITEMS.register("shriving_stone", () -> new ShrivingStone(ItemPropertiesHelper.material().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> MANA_PEARL = ITEMS.register("mana_pearl", () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON),0,false));
    public static final RegistryObject<Item> ARCANE_PEARL = ITEMS.register("arcane_pearl", () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE),1,false));
    public static final RegistryObject<Item> ABYSS_PEARL = ITEMS.register("abyss_pearl", () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.EPIC),2,true));

    public static final RegistryObject<Item> FIRE_PEARL = ITEMS.register("fire_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.FLAME_BURN.get()),false));

    public static final RegistryObject<Item> ICE_PEARL = ITEMS.register("ice_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.FROZEN_DECK.get()),false));

    public static final RegistryObject<Item> NATURE_PEARL = ITEMS.register("nature_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.WILD_GROW.get()),false));

    public static final RegistryObject<Item> EVOCATION_PEARL = ITEMS.register("evoca_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.EVOCATION_SOUL.get()),false));

    public static final RegistryObject<Item> HOLY_PEARL = ITEMS.register("holy_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.HOLINESS.get()),false));

    public static final RegistryObject<Item> BLOOD_PEARL = ITEMS.register("blood_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.BLOOD_LINE.get()),false));

    public static final RegistryObject<Item> THUNDER_PEARL = ITEMS.register("thunder_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.THUNDER.get()),false));

    public static final RegistryObject<Item> END_PEARL = ITEMS.register("end_pearl",
            () -> new EnchancedPearlItem(ItemPropertiesHelper.material().rarity(Rarity.RARE), 0,
                    () -> List.of(RegistryEnchantments.END_BREATH.get()),false));

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
    //
    public static final RegistryObject<Item> MAGIC_COOKIES = ITEMS.register("magic_cookies",
            () -> new MagicCookiesItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .food(MagicCookiesItem.DEFAULT_COOKIE)));

    public static final RegistryObject<Item> GUDIN_DAO = ITEMS.register("gu_ding_saber",() -> new GuDingDao(new Item.Properties().rarity(Rarity.RARE)));


    public static Collection<RegistryObject<Item>> getItems() {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


}
