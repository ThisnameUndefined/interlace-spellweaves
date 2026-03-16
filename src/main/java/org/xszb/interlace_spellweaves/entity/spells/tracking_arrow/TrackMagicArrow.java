package org.xszb.interlace_spellweaves.entity.spells.tracking_arrow;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TrackMagicArrow extends AbstractMagicProjectile implements IEntityAdditionalSpawnData {
    public TrackMagicArrow(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public TrackMagicArrow(Level pLevel, LivingEntity pShooter) {
        this(RegistryEntity.TRACK_MAGIC_ARROW.get(), pLevel);
        this.setOwner(pShooter);
    }

    public void shoot(Vec3 rotation, float inaccuracy) {
        var speed = rotation.length();
        Vec3 offset = Utils.getRandomVec3(1).normalize().scale(inaccuracy);
        var motion = rotation.normalize().add(offset).normalize().scale(speed);
        super.shoot(motion);
    }

    @Nullable
    Entity cachedHomingTarget;
    @Nullable
    UUID homingTargetUUID;

    @Nullable
    public Entity getHomingTarget() {
        if (this.cachedHomingTarget != null && !this.cachedHomingTarget.isRemoved()) {
            return this.cachedHomingTarget;
        } else if (this.homingTargetUUID != null && this.level() instanceof ServerLevel) {
            this.cachedHomingTarget = ((ServerLevel) this.level()).getEntity(this.homingTargetUUID);
            return this.cachedHomingTarget;
        } else {
            return null;
        }
    }

    public void setHomingTarget(LivingEntity entity) {
        this.homingTargetUUID = entity.getUUID();
        this.cachedHomingTarget = entity;
    }

    @Override
    public void tick() {
        super.tick();
        var homingTarget = getHomingTarget();
        if (homingTarget != null) {
            if (!doHomingTowards(homingTarget)) {
                this.homingTargetUUID = null;
                this.cachedHomingTarget = null;
            }
        }
    }

    private boolean doHomingTowards(Entity entity) {
        if (entity.isRemoved()) {
            return false;
        }
        var motion = this.getDeltaMovement();
        var speed = this.getDeltaMovement().length();
        var delta = entity.getBoundingBox().getCenter().subtract(this.position()).add(entity.getDeltaMovement());
        float f = .08f;
        var newMotion = new Vec3(Mth.lerp(f, motion.x, delta.x), Mth.lerp(f, motion.y, delta.y), Mth.lerp(f, motion.z, delta.z)).normalize().scale(speed);
        this.setDeltaMovement(newMotion);
        return this.tickCount <= 10 || !(newMotion.dot(delta) < 0);
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double x, double y, double z) {
    }

    @Override
    public float getSpeed() {
        return 1.85f;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level().isClientSide) {
            var target = pResult.getEntity();
            var owner = getOwner();
            DamageSources.applyDamage(target, damage, RegistrySpell.MARK_SHOT.get().getDamageSource(this, owner));
            if (target.getUUID().equals(homingTargetUUID)) {
                target.invulnerableTime = 0;
            }
            this.discard();
        }
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.homingTargetUUID != null) {
            tag.putUUID("homingTarget", homingTargetUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("homingTarget", 11)) {
            this.homingTargetUUID = tag.getUUID("homingTarget");
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        var owner = getOwner();
        buffer.writeInt(owner == null ? 0 : owner.getId());
        var homingTarget = getHomingTarget();
        buffer.writeInt(homingTarget == null ? 0 : homingTarget.getId());

    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        Entity owner = this.level().getEntity(additionalData.readInt());
        if (owner != null) {
            this.setOwner(owner);
        }
        Entity homingTarget = this.level().getEntity(additionalData.readInt());
        if (homingTarget != null) {
            this.cachedHomingTarget = homingTarget;
            this.homingTargetUUID = homingTarget.getUUID();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
