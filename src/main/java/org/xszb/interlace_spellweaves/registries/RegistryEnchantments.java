package org.xszb.interlace_spellweaves.registries;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.enchantment.*;

public class RegistryEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, InterlaceSpellWeaves.MODID);

    public static final RegistryObject<Enchantment> FLAME_BURN =  ENCHANTMENTS.register("flameburn", Flameburn::new);

    public static final RegistryObject<Enchantment> FROZEN_DECK =  ENCHANTMENTS.register("frostdeck", Frozendeck::new);

    public static final RegistryObject<Enchantment> WILD_GROW =  ENCHANTMENTS.register("wildgrowth", Wildgrowth::new);

    public static final RegistryObject<Enchantment> BLOOD_LINE =  ENCHANTMENTS.register("bloodline", Bloodline::new);

    public static final RegistryObject<Enchantment> HOLINESS =  ENCHANTMENTS.register("holiness", Holiness::new);

    public static final RegistryObject<Enchantment> EVOCATION_SOUL =  ENCHANTMENTS.register("evocationsoul", Evocationsoul::new);

    public static final RegistryObject<Enchantment> THUNDER =  ENCHANTMENTS.register("thunderflash", ThunderFlash::new);

    public static final RegistryObject<Enchantment> END_BREATH =  ENCHANTMENTS.register("endbreath", Endbreath::new);

    public static final RegistryObject<Enchantment> ELUSIVE =  ENCHANTMENTS.register("elusive", Elusive::new);

    public static final RegistryObject<Enchantment> SHATTER =  ENCHANTMENTS.register("shatter", Shatter::new);

    public static final RegistryObject<Enchantment> MINIATURIZE =  ENCHANTMENTS.register("miniaturize", Miniaturize::new);

    public static final RegistryObject<Enchantment> OVERLOAD =  ENCHANTMENTS.register("overload", Overload::new);



    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
