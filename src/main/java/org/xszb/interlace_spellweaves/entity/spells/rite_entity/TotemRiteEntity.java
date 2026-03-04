package org.xszb.interlace_spellweaves.entity.spells.rite_entity;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import org.xszb.interlace_spellweaves.util.EntityUtil;

import java.util.Optional;



public class TotemRiteEntity extends AoeEntity implements AntiMagicSusceptible {
    public TotemRiteEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    private static final EntityDataAccessor<Boolean> DATA_FINISH_CRAFT = SynchedEntityData.defineId(TotemRiteEntity.class, EntityDataSerializers.BOOLEAN);


    protected boolean can_spawn = false;

    public TotemRiteEntity(Level level,boolean can_spawn) {
        this(RegistryEntity.TOTEM_RITE.get(), level);
        this.can_spawn = can_spawn;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FINISH_CRAFT, false);
    }

    public boolean isFinishCraft() {
        return this.entityData.get(DATA_FINISH_CRAFT);
    }

    @Override
    public void applyEffect(LivingEntity target) {

    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
    }

    @Override
    public float getParticleCount() {
        return .15f;
    }

    @Override
    public void tick() {
        if (tickCount % 10 == 0) {
            if (!level().isClientSide) {
                var center = this.getBoundingBox().getCenter();
                float explosionRadius = getRadius();
                var explosionRadiusSqr = explosionRadius * explosionRadius;
                var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
                var losCenter = Utils.moveToRelativeGroundLevel(level(), center, 2);
                losCenter = Utils.raycastForBlock(level(), losCenter, losCenter.add(0, 3, 0), ClipContext.Fluid.NONE).getLocation().add(losCenter).scale(.5f);
                for (Entity entity : entities) {
                    double distanceSqr = entity.distanceToSqr(center);
                    if (distanceSqr < explosionRadiusSqr && canHitEntity(entity) && Utils.hasLineOfSight(level(), losCenter, entity.getBoundingBox().getCenter(), true)) {
                        boolean damaged = DamageSources.applyDamage(entity, damage, RegistrySpell.TOTEM_RITE.get().getDamageSource(this, getOwner()));
                        if (damaged) {
                            float floatOffset = ((float) Math.abs(tickCount % 40 - 20)) / 80;
                            Vec3 start = this.position().add(0, 1.5 + floatOffset, 0);
                            Vec3 end = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
                            EntityUtil.particleLine(start,end, (ServerLevel) level(),20,ParticleTypes.ENCHANT);
                        }
                        if (entity instanceof LivingEntity tar && tar.isDeadOrDying() && !this.isFinishCraft()) {
                            this.entityData.set(DATA_FINISH_CRAFT, true);

                            if (this.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(
                                        ParticleTypes.TOTEM_OF_UNDYING,
                                        this.getX(), this.getY() + 0.8, this.getZ(),
                                        20,
                                        0.4, 0.4, 0.4,
                                        0.15
                                );
                            }
                        }
                    }
                }
            }
        }
        if (tickCount > duration){
            if (this.isFinishCraft() && can_spawn) {
                ItemEntity itemEntity = new ItemEntity(
                        level(),
                        this.getX(),
                        this.getY() + 0.5,
                        this.getZ(),
                        new ItemStack(Items.TOTEM_OF_UNDYING)
                );

                itemEntity.setPickUpDelay(20);
                itemEntity.setNoGravity(false);
                itemEntity.setDeltaMovement(0, 0.2, 0);

                level().addFreshEntity(itemEntity);
            }else {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.EXPLOSION,
                            this.getX(), this.getY() + 0.8, this.getZ(),
                            20,
                            0.4, 0.4, 0.4,
                            0.15
                    );
                }
            }
            discard();
        }
    }



    @Override
    protected Vec3 getInflation() {
        return new Vec3(0, 1, 0);
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public void onAntiMagic(MagicData magicData) {
        discard();
    }
}
