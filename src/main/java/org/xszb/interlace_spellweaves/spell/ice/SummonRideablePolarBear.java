package org.xszb.interlace_spellweaves.spell.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.entity.mobs.polorbear.RideablePolarBear;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SummonRideablePolarBear  extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "summon_polar_bear");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.hp", getBearHealth(spellLevel, null)),
                Component.translatable("ui.irons_spellbooks.damage", getBearDamage(spellLevel, null))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(180)
            .build();

    public SummonRideablePolarBear() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
            super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        }

    }

    public ICastDataSerializable getEmptyCastData() {
        return new SummonedEntitiesCastData();
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(this)) {
            SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
            int summonTime = 12000;
            RideablePolarBear polarBear = new RideablePolarBear(world, entity);
            polarBear.setPos(entity.position());

            polarBear.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(getBearDamage(spellLevel, entity));
            polarBear.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getBearHealth(spellLevel, entity));
            setAttributes(polarBear,getSpellPower(spellLevel, entity));
            polarBear.setHealth(polarBear.getMaxHealth());
            world.addFreshEntity(polarBear);
            if (entity instanceof Player player && polarBear.isAddedToWorld()) {
                polarBear.doPlayerRide(player);
            }
            SummonManager.initSummon(entity, polarBear, summonTime, summonedEntitiesCastData);
            RecastInstance recastInstance = new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData);
            recasts.addRecast(recastInstance, playerMagicData);
        }


        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getBearHealth(int spellLevel, LivingEntity caster) {
        return 20 + spellLevel * 4;
    }

    private float getBearDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    private void setAttributes(RideablePolarBear bear, float power) {
        int maxPower = baseSpellPower + (getMaxLevel() - 1) * spellPowerPerLevel;
        float quality = power / (float) maxPower;
        float minSpeed = .2f;
        float maxSpeed = .45f;
        bear.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(quality, minSpeed, maxSpeed));
    }

}
