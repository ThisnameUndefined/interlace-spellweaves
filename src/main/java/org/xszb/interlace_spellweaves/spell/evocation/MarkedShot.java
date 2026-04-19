package org.xszb.interlace_spellweaves.spell.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.entity.spells.tracking_arrow.TrackMagicArrow;

import java.util.List;

public class MarkedShot extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "marked_shot");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(8)
            .setCooldownSeconds(10)
            .build();

    public MarkedShot() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 100;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 2)).append(Component.translatable("ui.iss_csw.arrow_damage")),
                Component.translatable("ui.irons_spellbooks.projectile_count", Utils.stringTruncation(getCount(spellLevel, caster), 2)),
                Component.translatable("ui.irons_spellbooks.duration", Utils.stringTruncation((double) getTime(spellLevel, caster) / 20, 2))
        );
    }


    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            return true;
        }
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, .15f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        var recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(getSpellId())) {
            if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
                recasts.addRecast(new RecastInstance(
                        getSpellId(),
                        spellLevel,
                        getRecastCount(spellLevel, entity),
                        getTime(spellLevel, entity),
                        castSource,
                        new MultiTargetEntityCastData(((ServerLevel) level).getEntity(targetEntityCastData.getTargetUUID()))
                ), playerMagicData);
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * 0.5f + spellLevel / 2f;
    }

    private int getTime(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 20 + 400);
    }

    public int getCount(int spellLevel, @Nullable LivingEntity entity) {
        return (int) Math.ceil(Mth.clamp(this.getSpellPower(spellLevel,entity) / 10,1,5));
    }

    public void trackShoot(Entity target, LivingEntity shooter, int spellLevel, double add) {
        Level world = shooter.level();
        int count = this.getCount(spellLevel, shooter);
        float damageBase = this.getDamage(spellLevel, shooter);
        float finalDamage = (float) (damageBase + add);

        for (int i = 0; i < count; i++) {
            TrackMagicArrow newArrow = new TrackMagicArrow(world, shooter);

            double offsetX = shooter.getRandom().nextDouble() * 4.0 - 2.0;
            double offsetY = shooter.getRandom().nextDouble() * 2.0;
            double offsetZ = shooter.getRandom().nextDouble() * 4.0 - 2.0;
            Vec3 spawnPos = shooter.position().add(offsetX, shooter.getEyeHeight() + offsetY, offsetZ);

            newArrow.moveTo(spawnPos);

            Vec3 targetVec = target.getBoundingBox().getCenter().subtract(spawnPos).normalize();

            newArrow.shoot(targetVec.scale(1.5), 0.2f);

            newArrow.setOwner(shooter);
            newArrow.setDamage(finalDamage);
            if (target instanceof LivingEntity ent){
                newArrow.setHomingTarget(ent);
            }


            world.addFreshEntity(newArrow);
        }
    }

    public ICastDataSerializable getEmptyCastData() {
        return new MultiTargetEntityCastData(new Entity[0]);
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setIFrames(0);
    }
}
