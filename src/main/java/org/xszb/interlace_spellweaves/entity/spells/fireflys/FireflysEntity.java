package org.xszb.interlace_spellweaves.entity.spells.fireflys;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;

import javax.annotation.Nullable;
import java.util.Optional;

public class FireflysEntity extends AoeEntity {
    @Nullable
    private LivingEntity target;

    public FireflysEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireflysEntity(Level level, LivingEntity owner, float damage) {
        this(RegistryEntity.FIRE_FLYS.get(),  level);
        setOwner(owner);
        this.setRadius(1);
        this.setDamage(damage);

    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                var motion = Utils.getRandomVec3(.05f).add(this.getDeltaMovement());
                var spawn = Utils.getRandomVec3(.25f);
                level().addParticle(ParticleHelper.FIREFLY, getX() + spawn.x, getY() + this.getBbHeight() * .5f + spawn.z, getZ() + spawn.z, motion.x, motion.y, motion.z);
            }
        }else if (this.target != null) {
            Vec3 vector = this.position().vectorTo(this.target.position());
            this.setDeltaMovement(vector.normalize().scale(Math.min(.3f,vector.length())));
        }else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        super.tick();
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (canHitEntity(target)) {
            DamageSources.applyDamage(target, damage, SpellRegistry.FIREFLY_SWARM_SPELL.get().getDamageSource(this, getOwner()));
        }
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return (getOwner() != null && pTarget != getOwner() && !getOwner().isAlliedTo(pTarget)) && super.canHitEntity(pTarget);
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}
