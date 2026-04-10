package org.xszb.interlace_spellweaves.spell.fusion;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.config.IronConfigParameters;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.spells.fireball.SmallMagicFireball;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.registry.RegistrySchool;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow.noGravityMagicArrow;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class ChaosStrike extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "chaos_strike");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(RegistrySchool.FUSION_RESOURCE)
            .setMaxLevel(15)
            .setCooldownSeconds(20)
            .build();
    @FunctionalInterface
    interface ProjectileEmitter {
        void accept(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData);
    }

    private final List<ProjectileEmitter> projectileEmitters;

    public ChaosStrike() {
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.baseManaCost = 1;
        this.castTime = 160;
        this.projectileEmitters = List.of(
                this::shootFireball,
                this::shootIcicle,
                this::shootLightning,
                this::shootGuidingBolt,
                this::shootMagicArrow,
                this::shootBloodSlash,
                this::shootSonicBoom,
                this::shootArrow,
                this::shootBall
        );
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.iss_csw.chaos_strike")
        );
    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
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
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @org.jetbrains.annotations.Nullable MagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
            int index = entity.getRandom().nextInt(projectileEmitters.size());
            projectileEmitters.get(index).accept(level, spellLevel, entity, playerMagicData.getCastSource(), playerMagicData);
        }
    }

    @Override
    public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {

        double entitySpellPowerModifier = 1;

        float configPowerModifier = ((Double) SpellConfigManager.getSpellConfigValue(this, IronConfigParameters.POWER_MULTIPLIER)).floatValue();
        if (sourceEntity instanceof LivingEntity livingEntity) {
            entitySpellPowerModifier = (float) livingEntity.getAttributeValue(AttributeRegistry.SPELL_POWER.get());
        }

        return (float) ((baseSpellPower + spellPowerPerLevel * (spellLevel - 1)) * entitySpellPowerModifier * configPowerModifier);
    }
    //
    public void shootFireball(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(.2f));
        SmallMagicFireball fireball = new SmallMagicFireball(world, entity);
        fireball.setPos(origin.subtract(0, fireball.getBbHeight(), 0));
        fireball.shoot(entity.getLookAngle(), .05f);
        fireball.setDamage(SpellRegistry.BLAZE_STORM_SPELL.get().getSpellPower(spellLevel, entity) * 1.4f);
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 2.0f, 1.0f);
        world.addFreshEntity(fireball);
    }

    public void shootIcicle(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.ICICLE_SPELL.get().onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public void shootLightning(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.LIGHTNING_LANCE_SPELL.get().onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public void shootGuidingBolt(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.GUIDING_BOLT_SPELL.get().onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public void shootMagicArrow(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.MAGIC_ARROW_SPELL.get().onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public void shootBloodSlash(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.BLOOD_SLASH_SPELL.get().onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public void shootSonicBoom(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.SONIC_BOOM_SPELL.get().onCast(world,spellLevel,entity,castSource,playerMagicData);
    }

    public void shootArrow(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(2f)).subtract(0,.2f, 0);;
        noGravityMagicArrow fireball = new noGravityMagicArrow(world, entity);
        fireball.setPos(origin);
        fireball.shoot(entity.getLookAngle());
        fireball.setDamage(RegistrySpell.BARRAGE_VOLLEY.get().getSpellPower(spellLevel, entity) * 0.25f + (float) Math.min(spellLevel, 10) / 2);
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 2.0f, 1.0f);
        world.addFreshEntity(fireball);
    }

    public void shootBall(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        SpellRegistry.ACID_ORB_SPELL.get().onCast(world,spellLevel,entity,castSource,playerMagicData);
    }

}


