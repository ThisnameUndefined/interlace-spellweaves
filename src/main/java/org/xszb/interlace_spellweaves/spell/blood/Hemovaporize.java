package org.xszb.interlace_spellweaves.spell.blood;

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
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.config.MainConfig;
import org.xszb.interlace_spellweaves.util.EntityUtil;

import java.util.List;
import java.util.Optional;

public class Hemovaporize extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "hemovaporize");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(15)
            .build();

    public Hemovaporize() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 50;
        this.baseManaCost = 8;

    }

    public int getCastTime(int spellLevel) {
        return castTime + 10 * spellLevel;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster) * 20, 2)),
                Component.translatable("ui.iss_csw.lose_health", Utils.stringTruncation(getDamage(spellLevel, caster), 2)),
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getRange(spellLevel), 1)));
    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.BLOOD_CAST.get());
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
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        var hitResult = Utils.raycastForEntity(level, entity, getRange(0), true, 2.2f);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hitResult).getEntity();
            AABB box = target.getBoundingBox();
            Vec3 spawn = box.getCenter();
            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, spawn.x, spawn.y, spawn.z, (int) (box.getSize() * 10), box.getXsize()/2, box.getYsize()/2, box.getZsize()/2, 0.2, true);
            if (target instanceof LivingEntity tar) {
                boilBlood(spellLevel, entity, playerMagicData, target, tar);
            }
            if (target instanceof PartEntity<?> part) {
                if (part.getParent() instanceof LivingEntity tar) {
                    boilBlood(spellLevel, entity, playerMagicData, target, tar);
                }
            }
        }
    }

    public void boilBlood(int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData, Entity target, LivingEntity tar) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(tar.getType());
        boolean isBlackListed = false;
        if (id != null) {
            if (MainConfig.hemovaporize_resistance.contains(id.toString())) isBlackListed = true;
        }

        if (playerMagicData != null  && (playerMagicData.getCastDurationRemaining() + 1) % 20 == 0 ){
            DamageSources.applyDamage(target, getDamage(spellLevel, entity) * 20, getDamageSource(entity));
        }else {
            if (!isBlackListed){

                EntityUtil.setHealth(tar,Math.max(tar.getHealth() - getDamage(spellLevel, entity),0));
                if (tar.isDeadOrDying()){
                    tar.die(getDamageSource(entity,entity));
                }
            }else {
                DamageSources.applyDamage(target, getDamage(spellLevel, entity), getDamageSource(entity));
            }
        }
    }

    public static float getRange(int level) {
        return 20;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return Math.max(0.1f, getSpellPower(spellLevel, caster) * 0.05f);
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setIFrames(0);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }
}
