package org.xszb.interlace_spellweaves.spell.fusion;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.registry.RegistrySchool;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.item.MagicCookiesItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ConjureRefreshment extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "conjure_refreshment");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(RegistrySchool.FUSION_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(15)
            .build();

    public ConjureRefreshment() {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 55;

    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.iss_csw.nutrition", Utils.stringTruncation(Math.floor(getSpellPower(spellLevel, caster) * 2)+ spellLevel, 0))
        );
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.EVOCATION_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        if (entity instanceof Player player && !entity.level().isClientSide) {
            float power = getSpellPower(spellLevel, player) * 2  + spellLevel;
            ItemStack magicCookie = MagicCookiesItem.createCustomCookie((int) Math.floor(power),1f);


            if (!player.getInventory().add(magicCookie)) {
                player.drop(magicCookie, false);
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {

        double entitySpellPowerModifier = 1;
        double additionalSpellPowerModifier = 1;

        float configPowerModifier = (float) ServerConfigs.getSpellConfig(this).powerMultiplier();
        if (sourceEntity instanceof LivingEntity livingEntity) {
            double entitySpellPower = livingEntity.getAttributeValue(AttributeRegistry.SPELL_POWER.get());
            entitySpellPowerModifier = (float) entitySpellPower;
            additionalSpellPowerModifier = (float) (entitySpellPower - 1) * 10 + 1;
        }


        return (float) ((baseSpellPower + spellPowerPerLevel ) * entitySpellPowerModifier *  configPowerModifier * additionalSpellPowerModifier);
    }

}