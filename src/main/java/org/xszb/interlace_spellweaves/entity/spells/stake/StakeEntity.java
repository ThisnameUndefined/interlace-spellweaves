package org.xszb.interlace_spellweaves.entity.spells.stake;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;

import java.util.Optional;
import java.util.function.Supplier;

public class StakeEntity extends AbstractMagicProjectile {
    protected int durability;

    public StakeEntity(EntityType<? extends StakeEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public StakeEntity(Level levelIn, LivingEntity shooter) {
        this(RegistryEntity.STAKE_ENTITY.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    public float getSpeed() {
        return 1.75f;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getDurability() {
        return this.durability;
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.ROOT_EMERGE);
    }


    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        var target = entityHitResult.getEntity();
        if (!target.getType().is(ModTags.CANT_ROOT) && !(target.getVehicle() instanceof RootEntity) && target instanceof LivingEntity tar) {
            Vec3 spawn = target.position();
            RootEntity rootEntity = new RootEntity(level(), (LivingEntity) this.getOwner());
            rootEntity.setDuration(getDurability());
            rootEntity.setTarget(tar);
            rootEntity.moveTo(spawn);
            level().addFreshEntity(rootEntity);
            target.stopRiding();
            target.startRiding(rootEntity, true);
        }else {
            this.setDamage(getDamage() * 1.5f);
        }

        DamageSources.applyDamage(target, getDamage(), RegistrySpell.PINNING_STAKE.get().getDamageSource(this, getOwner()));

        if (!level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level();
            float scale = target.getBbWidth() / 0.6f;
            BlockState oakState = Blocks.OAK_LOG.defaultBlockState();
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, oakState),
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), // 目标中心
                    (int) (15 * scale),
                    0.5* scale, 0.5* scale, 0.5* scale,
                    0.1
            );
        }
        discard();
    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    public void trailParticles() {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

    }
}
