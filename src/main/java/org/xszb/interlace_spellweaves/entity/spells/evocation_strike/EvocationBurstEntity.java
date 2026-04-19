package org.xszb.interlace_spellweaves.entity.spells.evocation_strike;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.mixin.NearestAttackableTargetGoalAccessor;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import org.xszb.interlace_spellweaves.util.EntityUtil;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

@AutoSpellConfig
public class EvocationBurstEntity extends AoeEntity implements GeoAnimatable {

    private static final EntityDataAccessor<Integer> TIME = SynchedEntityData.defineId(EvocationBurstEntity.class, EntityDataSerializers.INT);
    public int getWaitTime() {
        return this.entityData.get(TIME);
    }

    public EvocationBurstEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCircular();
    }

    public EvocationBurstEntity(Level level, LivingEntity owner, float damage, float radius,boolean isBurst,int waitTime) {
        this(RegistryEntity.EVOCATION_BURST.get(), level);
        setOwner(owner);
        this.setRadius(radius);
        this.setDamage(damage);
        this.entityData.set(TIME, waitTime);
    }
    @Override
    public void applyEffect(LivingEntity target) {
        // Effect handling is done in tick
        return;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TIME, 60);
    }

    @Override
    public void tick() {
        if (tickCount == getWaitTime()) {
            this.playSound(SoundRegistry.ELDRITCH_BLAST.get(), getRadius() * 2, Utils.random.nextIntBetweenInclusive(8, 12) * .1f);
            if (!level().isClientSide) {
                var center = this.getBoundingBox().getCenter();
                float explosionRadius = getRadius();
                var explosionRadiusSqr = explosionRadius * explosionRadius;
                MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(RegistrySpell.TOTEM_RITE.get().getSchoolType().getTargetingColor(), this.getRadius() * .9f), center.x, center.y, center.z, 1, 0, 0, 0, 0, true);
                var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
                var losCenter = Utils.moveToRelativeGroundLevel(level(), center, 2);
                losCenter = Utils.raycastForBlock(level(), losCenter, losCenter.add(0, 3, 0), ClipContext.Fluid.NONE).getLocation().add(losCenter).scale(.5f);
                for (Entity entity : entities) {
                    double distanceSqr = entity.distanceToSqr(center);
                    if (distanceSqr < explosionRadiusSqr ) {
                        if ( canHitEntity(entity) && Utils.hasLineOfSight(level(), losCenter, entity.getBoundingBox().getCenter(), true)){
                            double p = entity instanceof Mob mob && mob.getMobType().equals(MobType.UNDEAD) ? 2:1;
                            float damage = (float) (this.damage * p);
                            DamageSources.applyDamage(entity, damage, RegistrySpell.SPELL_BREAKER.get().getDamageSource(this, getOwner()));
                            if (getOwner() instanceof NamelessWizardsEntity ent && entity instanceof LivingEntity livingEntity) {
                                ent.setHealthAttack(40F, livingEntity);
                            }
                        }
                        if (entity instanceof AoeEntity) {
                            EntityUtil.forceRemoveEntity(level(), entity);
                        }
                        if (entity instanceof AntiMagicSusceptible magic && entity != getOwner() && getOwner() instanceof LivingEntity livingEntity) {
                            MagicData magicData = MagicData.getPlayerMagicData(livingEntity);
                            magic.onAntiMagic(magicData);
                        }
                    }
                }


                if (getOwner() instanceof NamelessWizardsEntity ){
                    for (Entity entity : entities) {
                        double distanceSqr = entity.distanceToSqr(center);
                        if (distanceSqr < explosionRadiusSqr * 4 && canHitEntity(entity) ) {
                            if (entity instanceof Mob mob && willAttackPlayers(mob) ) {
                                if (mob.getMobType().equals(MobType.UNDEAD) && mob.getMaxHealth() < ((LivingEntity) getOwner()).getMaxHealth() * 4){
                                    EntityUtil.forceRemoveEntity(level(), mob);
                                }
                            }
                        }
                    }
                }
            }
        } else if (tickCount > getWaitTime()) {
            discard();
        }
    }

    public boolean willAttackPlayers(Mob mob) {
        return mob.targetSelector.getAvailableGoals().stream().anyMatch(goal -> {
            Goal task = goal.getGoal();
            return task instanceof NearestAttackableTargetGoal<?> targetGoal &&
                    (((NearestAttackableTargetGoalAccessor) targetGoal).getTargetType() == Player.class );
        });
    }
    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
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


    private final AnimationController controller = new AnimationController(this, "controller", 0, this::animationPredicate);

    private PlayState animationPredicate(software.bernie.geckolib.core.animation.AnimationState event) {
        var controller = event.getController();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
