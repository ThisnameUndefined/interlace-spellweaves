package org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;

import java.util.Optional;
import java.util.Set;

public class noGravityMagicArrow extends AbstractMagicProjectile {
    protected static final int EXPIRE_TIME = 6 * 20;
    public noGravityMagicArrow(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public noGravityMagicArrow(Level levelIn, Entity shooter) {
        this(RegistryEntity.SMALL_MAGIC_ARROW.get(), levelIn);
        setOwner(shooter);
    }

    public void sethastrail(boolean hastrail) {
        this.hastrail = hastrail;
    }

    public int shakeTime;
    protected boolean hastrail;
    protected boolean inGround;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(noGravityMagicArrow.class, EntityDataSerializers.INT);
    private Potion potion = Potions.EMPTY;

    public void addEffect(MobEffectInstance p_36871_) {
        this.effects.add(p_36871_);
        this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.level().isClientSide) {
            if (!this.inGround) {
                this.makeParticle(2);
            }
        }
        if (tickCount > EXPIRE_TIME) {
            discard();
            super.tick();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide)
            return;
        Entity entity = entityHitResult.getEntity();
        if (this.getOwner() instanceof NamelessWizardsEntity ent && entity instanceof LivingEntity entity1){
            ent.setHealthAttack(0.5f,entity1);
        }

        DamageSources.applyDamage(entity, getDamage(), SpellRegistry.ARROW_VOLLEY_SPELL.get().getDamageSource(this, getOwner()));

        if (entity instanceof LivingEntity entity1){
            if (!this.effects.isEmpty()) {
                for(MobEffectInstance mobeffectinstance1 : this.effects) {
                    entity1.addEffect(mobeffectinstance1, entity);
                }
            }
        }


        this.discard();
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double x, double y, double z) {
    }

    @Override
    public float getSpeed() {
        return 2f;
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    private void makeParticle(int p_36877_) {
        int i = this.getColor();
        if (i != -1 && p_36877_ > 0) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < p_36877_; ++j) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }

        }
    }
}
