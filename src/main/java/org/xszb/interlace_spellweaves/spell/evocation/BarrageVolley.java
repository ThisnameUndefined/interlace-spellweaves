package org.xszb.interlace_spellweaves.spell.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.util.ParticleHelper;
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
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow.noGravityMagicArrow;

import java.util.List;
import java.util.Optional;

public class BarrageVolley extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "barrage_volley");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 2)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    public BarrageVolley() {
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 2;
        this.castTime = 60;
        this.baseManaCost = 5;
    }

    @Override
    public int getCastTime(int spellLevel) {
        return castTime + 20 * spellLevel;
    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
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
        return Optional.of(SoundEvents.BLAZE_AMBIENT);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.BOW_CHARGE_ANIMATION;
    }


    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (playerMagicData != null &&  (playerMagicData.getCastDurationRemaining() + 1) % 2 == 0) {

            Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(1.2f)).subtract(0,.2f, 0);
            MagicManager.spawnParticles(level, ParticleHelper.UNSTABLE_ENDER, origin.x, origin.y, origin.z, 3, .02, .02, .02, 0, false);
            if (playerMagicData.getCastDuration() - playerMagicData.getCastDurationRemaining() > 40 ){
                shootBlazeFireball(level, spellLevel, entity);
            }
        }
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * 0.25f + (float) Math.min(spellLevel, 10) / 2;
    }

    public void shootBlazeFireball(Level world, int spellLevel, LivingEntity entity) {
        Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(2f)).subtract(0,.2f, 0);;
        noGravityMagicArrow fireball = new noGravityMagicArrow(world, entity);
        fireball.setPos(origin);
        fireball.shoot(entity.getLookAngle());
        fireball.setDamage(getDamage(spellLevel, entity));
        MagicManager.spawnParticles(world, ParticleHelper.UNSTABLE_ENDER, origin.x, origin.y, origin.z, 3, .02, .02, .02, 0.2, false);
        world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 2.0f, 1.0f);
        world.addFreshEntity(fireball);
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setIFrames(0);
    }
}
