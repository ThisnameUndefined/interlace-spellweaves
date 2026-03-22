package org.xszb.interlace_spellweaves.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NameLessWizardConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue HURTLIMIT;
    public static final ForgeConfigSpec.DoubleValue SPELL_POWER_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue HEALTH_ATTACK_MULTIPLIER;
    public static ForgeConfigSpec.IntValue BLAST_INTERVAL;
    public static ForgeConfigSpec.IntValue SHOOT_INTERVAL;
    public static ForgeConfigSpec.IntValue MINI_SHOT_INTERVAL;
    public static ForgeConfigSpec.IntValue FIREWORK_INTERVAL;
    public static ForgeConfigSpec.IntValue VEX_INTERVAL;
    public static ForgeConfigSpec.IntValue CREEPER_INTERVAL;
    public static ForgeConfigSpec.IntValue WIND_INTERVAL;
    public static ForgeConfigSpec.IntValue TP_INTERVAL;
    public static ForgeConfigSpec.IntValue COME_BACK_INTERVAL;
    public static ForgeConfigSpec.IntValue START_GEO_INTERVAL;
    public static ForgeConfigSpec.IntValue BREAK_GEO_INTERVAL;

    static {
        BUILDER.push("Nameless Wizard Stats");

        MAX_HEALTH = BUILDER
                .comment("Nameless Wizard's base maximum health. Default: 160.0")
                .defineInRange("max_health", 160.0, 1.0, 10000.0);

        HURTLIMIT = BUILDER
                .comment("The maximum damage Nameless Wizard can take in a single hit, as a percentage of its max health. (e.g., 0.04 = 4%)")
                .defineInRange("hurt_limit", 0.04, 0, 1);

        SPELL_POWER_MULTIPLIER = BUILDER
                .comment("Multiplier for Nameless Wizard's spell damage. Default: 1.0")
                .defineInRange("spell_power_multiplier", 1.0, 0.0, 10.0);

        HEALTH_ATTACK_MULTIPLIER = BUILDER
                .comment("Multiplier for Nameless Wizard's health-based attack (percent damage). Default: 1.0")
                .defineInRange("health_attack_multiplier", 1.0, 0.0, 100.0);

        BUILDER.comment("Settings for Nameless Wizard AI Goals (Intervals in Ticks, 20 ticks = 1s)")
                .push("Nameless Wizard Spell Cooldowns");

        BLAST_INTERVAL = BUILDER.defineInRange("blast_spell_interval", 10, 1, 2000);
        SHOOT_INTERVAL = BUILDER.defineInRange("shoot_spell_interval", 10, 1, 2000);
        MINI_SHOT_INTERVAL = BUILDER.defineInRange("mini_shot_spell_interval", 6, 1, 2000);
        FIREWORK_INTERVAL = BUILDER.defineInRange("firework_spell_interval", 15, 1, 2000);
        VEX_INTERVAL = BUILDER.defineInRange("vex_spell_interval", 25, 1, 2000);
        CREEPER_INTERVAL = BUILDER.defineInRange("creeper_spell_interval", 10, 1, 2000);
        WIND_INTERVAL = BUILDER.defineInRange("gust_spell_interval", 5, 1, 2000);
        TP_INTERVAL = BUILDER.defineInRange("teleport_spell_interval", 5, 1, 2000);
        COME_BACK_INTERVAL = BUILDER.defineInRange("come_back_spell_interval", 5, 1, 2000);
        START_GEO_INTERVAL = BUILDER.defineInRange("start_geo_interval", 10, 1, 2000);
        BREAK_GEO_INTERVAL = BUILDER.defineInRange("break_geo_interval", 10, 1, 2000);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double maxHealth;
    public static double hurtLimit;
    public static double spellPowerMultiplier;
    public static double healthAttackMultiplier;
    public static int blastInt, shootInt, miniShotInt, fireworkInt;
    public static int vexInt, creeperInt, windInt;
    public static int tpInt, comeBackInt, startGeoInt, breakGeoInt;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            maxHealth = MAX_HEALTH.get();
            spellPowerMultiplier = SPELL_POWER_MULTIPLIER.get();
            blastInt = BLAST_INTERVAL.get();
            shootInt = SHOOT_INTERVAL.get();
            miniShotInt = MINI_SHOT_INTERVAL.get();
            fireworkInt = FIREWORK_INTERVAL.get();
            vexInt = VEX_INTERVAL.get();
            creeperInt = CREEPER_INTERVAL.get();
            windInt = WIND_INTERVAL.get();
            tpInt = TP_INTERVAL.get();
            comeBackInt = COME_BACK_INTERVAL.get();
            startGeoInt = START_GEO_INTERVAL.get();
            breakGeoInt = BREAK_GEO_INTERVAL.get();
            healthAttackMultiplier = HEALTH_ATTACK_MULTIPLIER.get();
            hurtLimit = HURTLIMIT.get();
            NamelessWizardsEntity.finalLimit = maxHealth;

        }
    }
}
