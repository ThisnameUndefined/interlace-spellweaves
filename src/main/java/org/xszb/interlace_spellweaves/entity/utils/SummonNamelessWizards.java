package org.xszb.interlace_spellweaves.entity.utils;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import org.xszb.interlace_spellweaves.util.EntityUtil;

import javax.annotation.Nullable;

public class SummonNamelessWizards extends Entity {

    private int lifetime = 100;
    private boolean bossSpawned = false;
    @Nullable
    private LivingEntity user;
    private CameraShakeData cameraShakeData;

    public SummonNamelessWizards(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public SummonNamelessWizards(Level level, double x, double y, double z, int lifetime,@Nullable  LivingEntity user) {
        this(RegistryEntity.SUMMON_NAMELESS.get(), level);
        setPos(x, y, z);
        this.lifetime = lifetime;
        this.user = user;
    }
    @Override
    public void tick() {
        super.tick();
        if (tickCount == 1) {
            createScreenShake();
        }
        if (tickCount % 20 == 1) {
            this.playSound(SoundRegistry.EARTHQUAKE_LOOP.get(), 2f, .9f + random.nextFloat() * .15f);
        }
        if (lifetime - 2 == 0) {
            this.playSound(SoundEvents.END_PORTAL_SPAWN, 1.5f, .9f + random.nextFloat() * .2f);
        }
        if (tickCount / 30f > 1.5f && tickCount % 10 == 0) {
            this.playSound(SoundRegistry.EARTHQUAKE_IMPACT.get(), 1.5f, .9f + random.nextFloat() * .2f);
        }
        if (!level().isClientSide) {
            if (lifetime-- <= 0 && !bossSpawned) {
                spawnBoss();
                this.discard();
            }
        } else {
            Vec3 centerPos = this.blockPosition().getCenter();
            float currentHeight = tickCount / 30f;
            float maxHeight = 1.5f;

            for (int i : new int[]{-1, 1}) {
                for (int j : new int[]{-1, 1}) {
                    Vec3 corner = centerPos.add(i * 0.5, -0.5, j * 0.5);
                    Vec3 topPoint = centerPos.add(0, Math.min(currentHeight,maxHeight), 0);
                    EntityUtil.clientParticleLine(corner, topPoint, this.level(), 10, ParticleTypes.ENCHANT);
                }
            }
            Vec3 burstPos = centerPos.add(0, maxHeight, 0);
            if (tickCount == 97) {
                level().addParticle(new BlastwaveParticleOptions(RegistrySpell.TOTEM_RITE.get().getSchoolType().getTargetingColor(), 4), burstPos.x, burstPos.y, burstPos.z, 0, 0, 0);

            }
            if (currentHeight >= maxHeight && tickCount % 10 == 0) {
                for (int k = 0; k < 30; k++) {
                    double vx = (Math.random() - 0.5) * 1.4;
                    double vy = (Math.random() - 0.5) * 1.4;
                    double vz = (Math.random() - 0.5) * 1.4;
                    level().addParticle(ParticleTypes.ENCHANTED_HIT, burstPos.x, burstPos.y, burstPos.z, vx, vy, vz);
                }
            }
        }
    }

    protected void createScreenShake() {
        if (!this.level().isClientSide && !this.isRemoved()) {
            this.cameraShakeData = new CameraShakeData(this.lifetime - this.tickCount, this.position(), 15);
            CameraShakeManager.addCameraShake(cameraShakeData);
        }
    }

    private void spawnBoss() {
        if (level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel)this.level();
        NamelessWizardsEntity illusion = RegistryEntity.NAMELESS.get().create(serverLevel);
        if (illusion != null) {
            illusion.setNowPos(this.blockPosition());
            illusion.setHomePos(this.blockPosition());
            illusion.setPreActType(NamelessWizardsEntity.ActType.START);
            illusion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
            illusion.setAlphaPercent(100);
            if (user != null) {
                double dx = user.getX() - this.getX();
                double dz = user.getZ() - this.getZ();
                float yaw = (float) (Math.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;
                this.setYBodyRot(yaw);
            }
            serverLevel.addFreshEntityWithPassengers(illusion);
        }

        bossSpawned = true;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Lifetime")) {
            lifetime = tag.getInt("Lifetime");
        }
        createScreenShake();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Lifetime", lifetime);
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if (!this.level().isClientSide) {
            CameraShakeManager.removeCameraShake(this.cameraShakeData);
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}