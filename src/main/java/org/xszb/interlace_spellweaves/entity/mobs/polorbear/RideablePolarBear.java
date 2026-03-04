package org.xszb.interlace_spellweaves.entity.mobs.polorbear;

import io.redspace.ironsspellbooks.entity.mobs.SummonedPolarBear;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;

public class RideablePolarBear extends SummonedPolarBear implements PlayerRideableJumping {

    protected float playerJumpPendingScale;
    protected boolean allowStandSliding;
    protected boolean isJumping;
    private int standCounter;

    public RideablePolarBear(EntityType<? extends PolarBear> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 0;
    }

    public RideablePolarBear(Level pLevel, LivingEntity owner) {
        this(RegistryEntity.SUMMONED_POLAR_BEAR.get(), pLevel);
        setSummoner(owner);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.JUMP_STRENGTH, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    protected void tickRidden(Player player, Vec3 pTravelVector) {
        super.tickRidden(player, pTravelVector);
        this.yRotO = this.getYRot();
        this.setYRot(player.getYRot());
        this.setXRot(player.getXRot());
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.yRotO;
        this.yHeadRot = this.getYRot();
        if (this.isControlledByLocalInstance()) {

            if (this.onGround()) {
                this.setIsJumping(false);

                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    this.executeRidersJump(this.playerJumpPendingScale, pTravelVector);
                }

                this.playerJumpPendingScale = 0.0F;  // 重置跳跃蓄力
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isEffectiveAi() && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }
    }

    public void standIfPossible() {
        if (this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }

    }

    public void doPlayerRide(Player pPlayer) {
        this.setStanding(false);
        if (!this.level().isClientSide) {
            pPlayer.setYRot(this.getYRot());
            pPlayer.setXRot(this.getXRot());
            pPlayer.startRiding(this);
        }

    }

    public double getCustomJump() {
        return this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    protected void executeRidersJump(float pPlayerJumpPendingScale, Vec3 pTravelVector) {

        double d0 = this.getCustomJump() * (double)pPlayerJumpPendingScale * (double)this.getBlockJumpFactor();

        double d1 = d0 + (double)this.getJumpBoostPower();

        Vec3 vec3 = this.getDeltaMovement();

        this.setDeltaMovement(vec3.x, d1, vec3.z);
        this.setIsJumping(true);
        this.hasImpulse = true;

        net.minecraftforge.common.ForgeHooks.onLivingJump(this);

        if (pTravelVector.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float f1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            this.setDeltaMovement(this.getDeltaMovement().add(
                    (double)(-0.4F * f * pPlayerJumpPendingScale),
                    0.0D,
                    (double)(0.4F * f1 * pPlayerJumpPendingScale)
            ));
        }
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.POLAR_BEAR_WARNING, 0.4F, 1.0F);
    }

    public void onPlayerJump(int pJumpPower) {
        if (pJumpPower < 0) {
            pJumpPower = 0;
        } else {
            this.allowStandSliding = true;
            this.standIfPossible();
        }

        if (pJumpPower >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)pJumpPower / 90.0F;
        }
    }

    public boolean canJump() {
        return true;
    }

    public void handleStartJump(int pJumpPower) {
        this.allowStandSliding = true;
        this.standIfPossible();
        this.playJumpSound();
    }

    public void handleStopJump() {

    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setIsJumping(boolean pJumping) {
        this.isJumping = pJumping;
    }
}
