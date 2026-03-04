package org.xszb.interlace_spellweaves.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> HEMOVAPORIZE_RESISTANCE = BUILDER
            .comment("hemovaporize will not effect these mobs")
            .defineList(
                    "hemovaporize_resistance",
                    Arrays.asList("traveloptics:the_nightwarden_defeated"),
                    element -> element instanceof String && !((String) element).isEmpty()
            );

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_SPELL_EMPOWERMENT = BUILDER
            .comment("spell empowerment will not effect these spells")
            .defineList(
                    "no_spell_empowerment",
                    Arrays.asList("irons_spellbooks:haste","irons_spellbooks:oakskin","irons_spellbooks:heartstop","irons_spellbooks:fortify","traveloptics:shadowed_miasma","traveloptics:vigor_siphon","traveloptics:lingering_strain","traveloptics:floodgate","irons_spellbooks:blight","irons_spellbooks:spider_aspect","iss_csw:spell_empowerment"),
                    element -> element instanceof String && !((String) element).isEmpty()
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static List<? extends String> hemovaporize_resistance;

    public static List<? extends String> no_spell_empowerment;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hemovaporize_resistance = HEMOVAPORIZE_RESISTANCE.get();
        no_spell_empowerment = NO_SPELL_EMPOWERMENT.get();
    }
}
