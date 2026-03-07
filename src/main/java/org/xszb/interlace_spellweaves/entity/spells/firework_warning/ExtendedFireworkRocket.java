package org.xszb.interlace_spellweaves.entity.spells.firework_warning;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.mixin.LivingEntityAccessor;

import java.util.Map;

public class ExtendedFireworkRocket extends FireworkRocketEntity implements AntiMagicSusceptible {
    protected static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(ExtendedFireworkRocket.class, EntityDataSerializers.ITEM_STACK);

    public ExtendedFireworkRocket(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle, float damage) {
        super(pLevel, pStack, pShooter, pX, pY, pZ, pShotAtAngle);
        this.damage = damage;
        this.radius = 2;
    }

    public ExtendedFireworkRocket(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle, float damage,float radius) {
        super(pLevel, pStack, pShooter, pX, pY, pZ, pShotAtAngle);
        this.damage = damage;
        this.radius = radius;
    }

    private final float damage;
    private final float radius;

    public float getDamage() {
        return damage;
    }

    @Override
    public void tick() {
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        explode();
    }

    private void explode() {
        this.level().broadcastEntityEvent(this, (byte) 17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.dealExplosionDamage();
        this.discard();
    }

    private void dealExplosionDamage() {
        Vec3 hitPos = this.position();
        double explosionRadius = this.radius;
        for (LivingEntity livingentity : level().getEntitiesOfClass(LivingEntity.class, new AABB(hitPos.subtract(explosionRadius, explosionRadius, explosionRadius), hitPos.add(explosionRadius, explosionRadius, explosionRadius)))) {
            if (livingentity.isAlive() && livingentity.isPickable() && Utils.hasLineOfSight(level(), hitPos, livingentity.getBoundingBox().getCenter(), true)) {
                if (getOwner() instanceof NamelessWizardsEntity ent && livingentity != this.getOwner()) {
                    ent.setHealthAttack(0.5f,livingentity);
                    MobEffect[] effectsToApply = { MobEffectRegistry.REND.get(), MobEffectRegistry.BLIGHT.get()};
                    Map<MobEffect, MobEffectInstance> eff = ((LivingEntityAccessor)livingentity).getEffects();
                    for (MobEffect effect : effectsToApply) {
                        MobEffectInstance current = eff.get(effect);
                        int newAmplifier = (current == null) ? 0 : Math.min(current.getAmplifier() + 1, 9);
                        MobEffectInstance updated = new MobEffectInstance(effect, 100 * (newAmplifier + 1), newAmplifier);
                        eff.put(effect, updated);
                        ((LivingEntityAccessor)livingentity).onEffectUpdated(updated, true, null);
                    }
                }
                DamageSources.applyDamage(livingentity, this.getDamage(), SpellRegistry.FIRECRACKER_SPELL.get().getDamageSource(this, getOwner()).setIFrames(0));
            }
        }
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }
}
