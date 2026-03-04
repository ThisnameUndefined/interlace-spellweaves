package org.xszb.interlace_spellweaves.entity.spells.creeper_chain;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;

import java.util.Optional;
import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.spells.evocation.ChainCreeperSpell.summonCreeperRing;

public class CreeperChainEntiy  extends AbstractMagicProjectile {
    public CreeperChainEntiy(EntityType<? extends CreeperChainEntiy> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected float damage;
    protected float speed;

    public CreeperChainEntiy(LivingEntity shooter, Level level, float speed, float damage) {
        super((EntityType) RegistryEntity.CREEPER_HEAD_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.speed = speed;
        this.damage = damage;
        this.explosionRadius = 5.0F;
        this.shoot(shooter.getLookAngle());
    }

    public CreeperChainEntiy(LivingEntity shooter, Level level, Vec3 speed, float damage) {
        this(RegistryEntity.CREEPER_HEAD_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.damage = damage;
        this.explosionRadius = 5.0F;
        this.speed = (float)speed.length();
        this.shoot(speed);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS) {
                onHit(hitresult);
            }
        }
        ProjectileUtil.rotateTowardsMovement(this, 1);
        setPos(position().add(getDeltaMovement()));

        if (!this.isNoGravity()) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
        }


        this.baseTick();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            float explosionRadius = 3.5f;
            var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.position().distanceTo(hitResult.getLocation());
                if (distance < explosionRadius) {
                    //Prevent duplicate chains
                    if (entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying() && !canHitEntity(entity))
                        break;
                    float damage = (float) (this.damage * (1 - Math.pow(distance / (explosionRadius), 2)));
                    DamageSources.applyDamage(entity, damage, SpellRegistry.LOB_CREEPER_SPELL.get().getDamageSource(this, getOwner()));
                    entity.invulnerableTime = 0;
                    if (getOwner() instanceof NamelessWizardsEntity ent && entity instanceof LivingEntity livingEntity ) {
                        ent.setHealthAttack(3,livingEntity);
                    }

                }
            }

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, false, Level.ExplosionInteraction.NONE);

            Vec3 spawn = this.position();
            summonCreeperRing(this.level(), (LivingEntity) getOwner(), spawn.add(0, 0.5, 0), damage, 8);

            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void trailParticles() {
        Vec3 vec3 = this.getBoundingBox().getCenter();
        this.level().addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, (double)0.0F, (double)0.0F, (double)0.0F);
    }

    public void impactParticles(double x, double y, double z) {
    }

    public float getSpeed() {
        return this.speed;
    }

    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }
}
