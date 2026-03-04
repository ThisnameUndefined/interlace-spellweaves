package org.xszb.interlace_spellweaves.entity.spells.firework_warning;


import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

import static org.xszb.interlace_spellweaves.util.Firework.randomFireworkRocket;

public class FireworkWarnEntity extends AoeEntity implements GeoEntity {

    public FireworkWarnEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCircular();
    }

    public FireworkWarnEntity(Level level, LivingEntity owner, float damage, float radius) {
        this(RegistryEntity.FIREWORK_BURST.get(), level);
        setOwner(owner);
        this.setRadius(radius);
        this.setDamage(damage);
    }
    @Override
    public void applyEffect(LivingEntity target) {
        // Effect handling is done in tick
        return;
    }

    public final int waitTime = 40;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

    }

    @Override
    public void tick() {
        if (tickCount == waitTime) {
            if (!level().isClientSide) {
                ExtendedFireworkRocket firework = new ExtendedFireworkRocket(level(), randomFireworkRocket(), getOwner(), getX(), getY() + 1, getZ(), true, getDamage());
                level().addFreshEntity(firework);
                firework.shoot(0, 0, 0, 0, 0);
            }
        } else if (tickCount > waitTime) {
            discard();
        }
        if (level().isClientSide && tickCount < waitTime / 2) {

        }
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

    private boolean played = false;

    private final RawAnimation ANIMATION = RawAnimation.begin().thenPlay("idle");

    private final AnimationController controller = new AnimationController(this, "controller", 0, this::animationPredicate);

    private PlayState animationPredicate(software.bernie.geckolib.core.animation.AnimationState event) {
        var controller = event.getController();

        if (!played && controller.getAnimationState() == AnimationController.State.STOPPED) {
            controller.forceAnimationReset();
            controller.setAnimation(ANIMATION);
            played = true;
        }

        return PlayState.CONTINUE;
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

}
