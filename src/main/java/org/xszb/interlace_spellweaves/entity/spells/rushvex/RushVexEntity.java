package org.xszb.interlace_spellweaves.entity.spells.rushvex;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.goals.WispAttackGoal;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.holy.WispSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class RushVexEntity extends PathfinderMob implements GeoEntity , AntiMagicSusceptible {

    @Nullable
    private UUID ownerUUID;

    @Nullable
    private Entity cachedOwner;

    private final RawAnimation animation = RawAnimation.begin().thenPlay("animation.wisp.flying") ;

    private Vec3 targetSearchStart;
    private Vec3 lastTickPos;
    private float damageAmount;

    public RushVexEntity(EntityType<? extends RushVexEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public RushVexEntity(Level levelIn, LivingEntity owner, float damageAmount) {
        this(RegistryEntity.VEX.get(), levelIn);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.damageAmount = damageAmount;
        setOwner(owner);

        var xRot = owner.getXRot();
        var yRot = owner.getYRot();
        var yHeadRot = owner.getYHeadRot();

        this.setYRot(yRot);
        this.setXRot(xRot);
        this.setYBodyRot(yRot);
        this.setYHeadRot(yHeadRot);
        this.lastTickPos = this.position();

        this.addEffect(new MobEffectInstance(MobEffectRegistry.HEARTSTOP.get(),120,0));

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WispAttackGoal(this, 0.8));
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public LivingEntity getTarget() {
        return super.getTarget();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        if (level().isClientSide) {
            spawnParticles();
        } else {
            var target = this.getTarget();
            if (target == null || target.isRemoved() || tickCount > 100) {
                if (tickCount > 10) {
                    this.popAndDie();
                }
            } else {
                if (this.getBoundingBox().intersects(target.getBoundingBox())) {
                    if (cachedOwner instanceof NamelessWizardsEntity ent){
                        ent.setHealthAttack(0.5f,target);
                    }
                    DamageSources.applyDamage(target, damageAmount, SpellRegistry.SUMMON_VEX_SPELL.get().getDamageSource(this, cachedOwner).setIFrames(0));
                    this.playSound(WispSpell.getImpactSound(), 1.0f, 1.0f);
                    var p = target.getEyePosition();
                    MagicManager.spawnParticles(level(), ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 25, 0.5, 0.5, 0.5, .18, true);

                    discard();
                }
            }
        }
        lastTickPos = this.position();
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return this.level.isEmptyBlock(blockPos)  || super.isStableDestination(blockPos);
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.6F;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void setTarget(@org.jetbrains.annotations.Nullable LivingEntity target) {
        super.setTarget(target);

        //irons_spellbooks.LOGGER.debug("WispEntity.setTarget: {}", target);
    }

    @Override
    protected void customServerAiStep() {
        if (this.cachedOwner == null || !this.cachedOwner.isAlive()) {
            this.discard();
        }
    }

    private PlayState predicate(AnimationState event) {
        event.getController().setAnimation(animation);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.FLYING_SPEED, .2)
                .add(Attributes.MOVEMENT_SPEED, .2);

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.hasEffect(MobEffectRegistry.HEARTSTOP.get()) && !level().isClientSide){
            popAndDie();
        }
        return false;
    }

    private void popAndDie() {
        this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
        this.discard();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    public void spawnParticles() {

    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        if (!level().isClientSide){
            popAndDie();
        }
    }
}
