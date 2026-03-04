package org.xszb.interlace_spellweaves.entity.spells.ice_strike;

import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;

import java.util.Optional;

public class IceStrikeEntity extends AoeEntity {
    private static final EntityDataAccessor<Boolean> DATA_MIRRORED = SynchedEntityData.defineId(IceStrikeEntity.class, EntityDataSerializers.BOOLEAN);


    public IceStrikeEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceStrikeEntity(Level level, boolean mirrored) {
        this(RegistryEntity.ICE_SLASH.get(), level);
        if (mirrored) {
            this.getEntityData().set(DATA_MIRRORED, true);
        }
    }


    @Override
    public void applyEffect(LivingEntity target) {
    }

    public final int ticksPerFrame = 2;
    public final int deathTime = ticksPerFrame * 4;

    @Override
    public void tick() {
        if (!firstTick) {
            firstTick = true;
        }
        if (tickCount >= deathTime)
            discard();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_MIRRORED, false);
    }

    public boolean isMirrored() {
        return this.getEntityData().get(DATA_MIRRORED);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void refreshDimensions() {
        return;
    }

    @Override
    public void ambientParticles() {
        return;
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
