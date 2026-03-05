package org.xszb.interlace_spellweaves.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.NoneSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.spell.blood.Hemovaporize;
import org.xszb.interlace_spellweaves.spell.evocation.BarrageVolley;
import org.xszb.interlace_spellweaves.spell.evocation.NameLessTeleport;
import org.xszb.interlace_spellweaves.spell.evocation.TotemRite;
import org.xszb.interlace_spellweaves.spell.evocation.pro.GustProSpell;
import org.xszb.interlace_spellweaves.spell.evocation.pro.LobCreeperChainSpell;
import org.xszb.interlace_spellweaves.spell.fusion.SpellEmpowerment;
import org.xszb.interlace_spellweaves.spell.ice.Blizzard;
import org.xszb.interlace_spellweaves.spell.ice.ChargeRayOfFrostSpell;
import org.xszb.interlace_spellweaves.spell.ice.FrostwhirlSlash;
import org.xszb.interlace_spellweaves.spell.ice.RimeVeil;
import org.xszb.interlace_spellweaves.spell.nature.FireflySeeker;
import org.xszb.interlace_spellweaves.spell.nature.PinningStake;

import java.util.List;
import java.util.function.Supplier;

public class RegistrySpell {

    private static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, InterlaceSpellWeaves.MODID);
    public static final Supplier<IForgeRegistry<AbstractSpell>> REGISTRY = SPELLS.makeRegistry(() -> new RegistryBuilder<AbstractSpell>().disableSaving().disableOverrides());
    private static final NoneSpell noneSpell = new NoneSpell();
    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static NoneSpell none() {
        return noneSpell;
    }

    private static RegistryObject<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static List<AbstractSpell> getEnabledSpells() {
        return RegistrySpell.SPELLS.getEntries()
                .stream()
                .filter(spell->spell.get().isEnabled())
                .map(RegistryObject::get)
                .toList();
    }


    public static final RegistryObject<AbstractSpell> CHARGE_RAY_OF_FROST_SPELL = registerSpell(new ChargeRayOfFrostSpell());

    public static final RegistryObject<AbstractSpell> ICE_SLASH = registerSpell(new FrostwhirlSlash());

    public static final RegistryObject<AbstractSpell> BLIZZARD = registerSpell(new Blizzard());

    public static final RegistryObject<AbstractSpell> RIME_VEIL = registerSpell(new RimeVeil());

//    public static final RegistryObject<AbstractSpell> RIDEABLE_POLAORBEAR = registerSpell(new SummonRideablePolarBear());

    public static final RegistryObject<AbstractSpell> PINNING_STAKE = registerSpell(new PinningStake());

    public static final RegistryObject<AbstractSpell> BARRAGE_VOLLEY = registerSpell(new BarrageVolley());

    public static final RegistryObject<AbstractSpell> HEMOVAPORIZE = registerSpell(new Hemovaporize());

    public static final RegistryObject<AbstractSpell> TOTEM_RITE = registerSpell(new TotemRite());

    public static final RegistryObject<AbstractSpell> SPELL_IMPROVE = registerSpell(new SpellEmpowerment());

    public static final RegistryObject<AbstractSpell> FIREFLY_SEEKER = registerSpell(new FireflySeeker());

    public static final RegistryObject<AbstractSpell> GUST_PRO = registerSpell(new GustProSpell());

    public static final RegistryObject<AbstractSpell> CREPER_CHAIN = registerSpell(new LobCreeperChainSpell());

    public static final RegistryObject<AbstractSpell> TP = registerSpell(new NameLessTeleport());




}
