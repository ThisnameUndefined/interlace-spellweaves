package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastVisualEntity;
import io.redspace.ironsspellbooks.network.ClientboundEntityEvent;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.config.NameLessWizardConfig;
import org.xszb.interlace_spellweaves.entity.boss.UnRemoveBossEntity;
import org.xszb.interlace_spellweaves.entity.spells.HailStone;
import org.xszb.interlace_spellweaves.entity.spells.creeper_chain.CreeperChainEntiy;
import org.xszb.interlace_spellweaves.entity.spells.evocation_strike.EvocationBurstEntity;
import org.xszb.interlace_spellweaves.entity.spells.firework_warning.ExtendedFireworkRocket;
import org.xszb.interlace_spellweaves.entity.spells.firework_warning.FireworkWarnEntity;
import org.xszb.interlace_spellweaves.entity.spells.gust.DamageGustCollider;
import org.xszb.interlace_spellweaves.entity.spells.rushvex.RushVexEntity;
import org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow.noGravityMagicArrow;
import org.xszb.interlace_spellweaves.mixin.EntityAccessor;
import org.xszb.interlace_spellweaves.mixin.LivingEntityAccessor;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistryItem;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import org.xszb.interlace_spellweaves.util.EntityUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static org.xszb.interlace_spellweaves.util.EntityUtil.canDiscard;
import static org.xszb.interlace_spellweaves.util.Firework.randomFireworkRocket;

public class NamelessWizardsEntity extends UnRemoveBossEntity implements Enemy, AntiMagicSusceptible, IClientEventEntity {

    private static final EntityDataAccessor<Integer> DATA_HURTCOOLDOWN = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WHITETIME = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ALPHAPERCENT = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> NOW_POS = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> IS_ILLUSION = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_PHASE_2 = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BOOLEAN);



    private static final EntityDataAccessor<Float> DATA_EXISTENCE = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.FLOAT);


    private ActType activeSpell = ActType.NONE;
    private double cooldown;
    private ActType preSpell = ActType.NONE;
    private int shootCoolDown;
    private int overHitCount;
    protected UUID BossIdentity;
    public static double finalLimit;

    private static final EntityDataAccessor<Byte> SPELL = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PRESPELL = SynchedEntityData.defineId(NamelessWizardsEntity.class, EntityDataSerializers.BYTE);


    private @Nullable ActType cancelCastAnimation = null;

    public NamelessWizardsEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.BossIdentity = UUID.randomUUID();
        this.setPersistenceRequired();
        this.refreshDimensions();
    }

    public NamelessWizardsEntity(Level pLevel) {
        this(RegistryEntity.NAMELESS.get(), pLevel);
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(5, new BlastSpellGoal(this, NameLessWizardConfig.blastInt));
        this.goalSelector.addGoal(5, new ShootSpellGoal(this, NameLessWizardConfig.shootInt));
        this.goalSelector.addGoal(5, new MiniShotSpellGoal(this, NameLessWizardConfig.miniShotInt));
        this.goalSelector.addGoal(5, new FireWorkSpellGoal(this, NameLessWizardConfig.fireworkInt));
        this.goalSelector.addGoal(5, new VexSpellGoal(this, NameLessWizardConfig.vexInt));
        this.goalSelector.addGoal(5, new CreeperSpellGoal(this, NameLessWizardConfig.creeperInt));
        this.goalSelector.addGoal(5, new WindSpellGoal(this, NameLessWizardConfig.windInt));
        this.goalSelector.addGoal(5, new TPSpellGoal(this, NameLessWizardConfig.tpInt));
        this.goalSelector.addGoal(5, new ComeBackSpellGoal(this, NameLessWizardConfig.comeBackInt));
        this.goalSelector.addGoal(5, new StartGeoGoal(this, NameLessWizardConfig.startGeoInt));
        this.goalSelector.addGoal(5, new BreakGeoGoal(this, NameLessWizardConfig.breakGeoInt));
        this.goalSelector.addGoal(5,new CloneSpellGoal(this,0));
        this.goalSelector.addGoal(5,new BreakPhaseGoal(this,0));
        this.goalSelector.addGoal(5,new ChangePhaseGoal(this,0));
        this.goalSelector.addGoal(5,new DeadGoal(this,0));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, NamelessWizardsEntity.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL, (byte)0);
        this.entityData.define(PRESPELL, (byte)0);
        this.entityData.define(DATA_HURTCOOLDOWN, 0);
        this.entityData.define(DATA_WHITETIME, 0);
        this.entityData.define(DATA_ALPHAPERCENT, 0);
        this.entityData.define(HOME_POS, BlockPos.ZERO);
        this.entityData.define(NOW_POS, BlockPos.ZERO);
        this.entityData.define(IS_ILLUSION, false);
        this.entityData.define(IS_PHASE_2, false);
        this.entityData.define(DATA_EXISTENCE,0f);;
    }

    public void setActType(ActType ActType) {
        this.activeSpell = ActType;
        this.entityData.set(SPELL, (byte)ActType.id);
    }

    public void setPreActType(ActType ActType) {
        this.preSpell = ActType;
        this.entityData.set(PRESPELL, (byte)ActType.id);
    }


    public ActType getActType() {
        return !this.level().isClientSide ? this.activeSpell : ActType.getFromId(this.entityData.get(SPELL));
    }

    private ActType getPreActType() {
        return !this.level().isClientSide ? this.preSpell : ActType.getFromId(this.entityData.get(PRESPELL));
    }


    protected SoundEvent getSpellSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    public double getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public int getDamagecooldown() {
        return this.entityData.get(DATA_HURTCOOLDOWN);
    }

    private void setDamagecooldown(int damagecooldown) {
        this.entityData.set(DATA_HURTCOOLDOWN, damagecooldown);
    }

    public int getOverhitCount() {
        return this.overHitCount;
    }

    public void setOverhitCount(int count) {
        this.overHitCount = count;
    }

    public int getWhiteDown() {
        return this.entityData.get(DATA_WHITETIME);
    }

    private void setWhiteDown(int num) {
        this.entityData.set(DATA_WHITETIME, num);
    }

    protected ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_20);

    public int getAlphaPercent() {
        return this.entityData.get(DATA_ALPHAPERCENT);
    }

    public void setAlphaPercent(int num) {
        this.entityData.set(DATA_ALPHAPERCENT, num);
    }

    public boolean getIsIllusion() {
        return this.entityData.get(IS_ILLUSION);
    }

    public void setIsIllusion(boolean isIllusion) {
        this.entityData.set(IS_ILLUSION, isIllusion);
    }

    public boolean getIsPhase2() {return this.entityData.get(IS_PHASE_2);}

    public void setIsPhase2(boolean isPhase2) { this.entityData.set(IS_PHASE_2, isPhase2);}

    public void setAntiCheatMode (boolean cheatMode){
        if (!this.getIsAntiCheatMode() && cheatMode){
            this.setExistence(0);
            this.setPreActType(ActType.PHASE);
        }
        super.setAntiCheatMode(cheatMode);
    }

    private void changeBody(NamelessWizardsEntity ent){
        if (this.getIsIllusion() || !ent.getIsIllusion()) return;
        this.setIsIllusion(true);
        ent.setIsIllusion(false);
        ent.setExistence(this.getExistence());
        ent.setCanDie(false);
        ent.bossEvent = this.bossEvent;
    }

    protected float getExistence (){ return this.getIsAntiCheatMode()?0:this.entityData.get(DATA_EXISTENCE);}

    protected float getExistenceHealth(){ return this.getMaxHealth() - getExistence();}

    protected void setExistence(float existence){
        if (Float.isNaN(existence)){
            return;
        }
        this.entityData.set(DATA_EXISTENCE, Math.min(existence,this.getMaxHealth()));
    }


    public boolean getCanKill() {return this.getIsIllusion() || super.getCanKill();}

    public boolean getCanDie(){return super.getCanDie() && !this.getIsAntiCheatMode();}

    //我有三千炼狱，待汝万世轮回
    public void setHealthAttack(float percent,LivingEntity tar){
        float multiplier = (float) NameLessWizardConfig.healthAttackMultiplier;
        float basePercent = percent * multiplier;

        float v = basePercent + (this.getIsPhase2() ? 0.5f : 0) + (this.getIsAntiCheatMode() ? 100 : 0);
        if ((tar instanceof Player pla && pla.isCreative()) || tar instanceof NamelessWizardsEntity){
            return;
        }
        int addition = tar instanceof Player? 0:1;
        EntityUtil.setHealth(tar,Math.max(tar.getHealth() - addition - Math.max(tar.getMaxHealth() * v/100,0.2f),0));
        if (tar.isDeadOrDying()){
            Vec3 spawn = tar.getEyePosition();
            ExtendedFireworkRocket firework = new ExtendedFireworkRocket(this.level(), randomFireworkRocket(), this, spawn.x, spawn.y, spawn.z, true, 12,5);
            this.level().addFreshEntity(firework);
            firework.shoot(0, 0, 0, 0, 0);
            tar.die(SpellRegistry.FIRECRACKER_SPELL.get().getDamageSource(this, this));

        }
        if (this.getIsAntiCheatMode() && !tar.isDeadOrDying() && !(tar instanceof Player)){
            EntityAccessor entityAccessor = (EntityAccessor)tar;
            entityAccessor.setRemovalReason(RemovalReason.KILLED);
            entityAccessor.getLevelCallback().onRemove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean isCasting() {
        return super.isCasting() || getActType() != ActType.NONE;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof NamelessWizardsEntity) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public boolean startRiding(@NotNull Entity p_19966_, boolean p_19967_) {
        return false;
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return false;
    }

    public void setHomePos(BlockPos homePos) {
        this.entityData.set(HOME_POS, homePos);
    }

    public void setNowPos(BlockPos homePos) {
        this.entityData.set(NOW_POS, homePos);
        Vec3 pos = homePos.getCenter().add(0,-0.5,0);
        this.setPos(pos);
    }

    BlockPos getHomePos() {
        return this.entityData.get(HOME_POS);
    }

    BlockPos getNowPos() {
        return this.entityData.get(NOW_POS);
    }


    @Override
    public boolean removeWhenFarAway(double p_21542_) {
        return false;
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effectInstance) {
        return MobEffectRegistry.MOB_EFFECT_DEFERRED_REGISTER.getEntries().stream().anyMatch(mobEffectRegistryObject -> mobEffectRegistryObject.get().equals(effectInstance.getEffect()));
    }


    public List<BlockPos> getPositionsAroundHome() {
        BlockPos home = getHomePos();
        int x = home.getX() ;
        int y = home.getY();
        int z = home.getZ() ;
        List<BlockPos> list = new ArrayList<>();
        list.add(new BlockPos(x, y, z - 11));
        list.add(new BlockPos(x, y, z + 11));
        list.add(new BlockPos(x - 11, y, z));
        list.add(new BlockPos(x + 11, y, z));
        int diagonal = 14;
        list.add(new BlockPos(x + diagonal, y, z + diagonal));
        list.add(new BlockPos(x - diagonal, y, z + diagonal));
        list.add(new BlockPos(x + diagonal, y, z - diagonal));
        list.add(new BlockPos(x - diagonal, y, z - diagonal));
        return list;
    }

    public List<BlockPos> getCanUsePos() {
        List<BlockPos> candidates = getPositionsAroundHome();

        List<BlockPos> available = new ArrayList<>();
        for (BlockPos pos : candidates) {
            AABB checkArea = new AABB(pos).inflate(2);
            List<NamelessWizardsEntity> others = this.level().getEntitiesOfClass(
                    NamelessWizardsEntity.class,
                    checkArea,
                    e -> true
            );
            if (others.isEmpty()) {
                available.add(pos);
            }
        }

        return available;
    }

    public List<NamelessWizardsEntity> getAllIllusion(boolean isAll) {
        AABB checkArea = new AABB(this.getHomePos()).inflate(30);
        return this.level().getEntitiesOfClass(
                NamelessWizardsEntity.class,
                checkArea,
                e -> (e.getIsIllusion() || isAll) && e.getCustomBossIdentity().equals(this.getCustomBossIdentity())
        );
    }

    public List<NamelessWizardsEntity> getAllIllusion() {
        return getAllIllusion(false);
    }

    public BlockPos getRandomSpawnPositionWithoutSameType() {
        List<BlockPos> available = getCanUsePos();
        return available.get(this.random.nextInt(available.size()));
    }

    public void setCustomBossIdentity(UUID newId) {
        this.BossIdentity = newId;
    }

    public UUID getCustomBossIdentity() {
        return this.BossIdentity;
    }

    public float getSpellDamage(float baseDamage) {
        return baseDamage * (float) NameLessWizardConfig.spellPowerMultiplier;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HomePosX", this.getHomePos().getX());
        compound.putInt("HomePosY", this.getHomePos().getY());
        compound.putInt("HomePosZ", this.getHomePos().getZ());

        compound.putInt("NowPosX", this.getNowPos().getX());
        compound.putInt("NowPosY", this.getNowPos().getY());
        compound.putInt("NowPosZ", this.getNowPos().getZ());

        compound.putBoolean("is_Illusion", getIsIllusion());
        compound.putBoolean("is_phase2", getIsPhase2());
        if (this.getActType() != ActType.DEAD) {
            compound.putFloat("Health", this.getMaxHealth());
        }
        if (this.BossIdentity != null) {
            compound.putUUID("CustomBossIdentity", this.BossIdentity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        int i = compound.getInt("HomePosX");
        int j = compound.getInt("HomePosY");
        int k = compound.getInt("HomePosZ");
        this.setHomePos(new BlockPos(i, j, k));

        int l = compound.getInt("NowPosX");
        int m = compound.getInt("NowPosY");
        int n = compound.getInt("NowPosZ");
        this.setNowPos(new BlockPos(l,m,n));

        this.setIsIllusion(compound.getBoolean("is_Illusion"));
        this.setIsPhase2(compound.getBoolean("is_phase2"));
        if (this.BossIdentity == null) {
            this.setCustomBossIdentity(compound.getUUID("CustomBossIdentity"));
        }

        super.readAdditionalSaveData(compound);

    }

    @Override
    public void tick() {
        super.tick();
        this.cooldown --;
        this.shootCoolDown --;
        if (this.getWhiteDown() > 0){
            this.setWhiteDown(this.getWhiteDown() - 1);
        }
        if (this.getNowPos() != BlockPos.ZERO) {
            Vec3 pos = this.getNowPos().getCenter().add(0,-0.5,0);
            this.setPos(pos);
            ((EntityAccessor) this).setChunkPos(new ChunkPos(this.getNowPos()));
        }
        if (this.isNoAi()){
            this.setNoAi(false);
            this.setAntiCheatMode(true);
        }

        if (this.isRemoved() && !this.getCanDie()) {
            this.aiStep();
            this.serverAiStep();
        }

        if (this.getDamagecooldown() > 0) {
            this.setDamagecooldown(this.getDamagecooldown() - 1);
        }
        if (cancelCastAnimation != null) {
            this.setActType(cancelCastAnimation);
            this.setPreActType(cancelCastAnimation);
            cancelCastAnimation = null;
        }
        if (!this.getIsIllusion() && !this.getIsPhase2() && this.getExistenceHealth() < this.getMaxHealth() / 3 && (this.cancelCastAnimation != ActType.BREAK2 && this.getActType() != ActType.BREAK2 && this.getPreActType() != ActType.BREAK2)  && (this.cancelCastAnimation != ActType.PHASE && this.getActType() != ActType.PHASE && this.getPreActType() != ActType.PHASE)) {
            this.cancelCastAnimation = ActType.BREAK2;
        }

        if (!this.getIsIllusion() && this.getIsPhase2() && this.getExistenceHealth() <= 0 && (this.cancelCastAnimation != ActType.DEAD && this.getActType() != ActType.DEAD && this.getPreActType() != ActType.DEAD)) {
            this.cancelCastAnimation = ActType.DEAD;
            this.getAllIllusion().forEach(NamelessWizardsEntity::IllusionExplode);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Map<MobEffect, MobEffectInstance> eff = ((LivingEntityAccessor)this).getEffects();
        if (!eff.isEmpty()) {
            Iterator<Map.Entry<MobEffect, MobEffectInstance>> iterator = eff.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<MobEffect, MobEffectInstance> entry = iterator.next();
                MobEffect effect = entry.getKey();
                boolean isNotAllowed = MobEffectRegistry.MOB_EFFECT_DEFERRED_REGISTER.getEntries()
                        .stream()
                        .noneMatch(reg -> reg.get().equals(effect));
                if (isNotAllowed) {
                    iterator.remove();
                    this.onEffectRemoved(entry.getValue());
                }
            }
        }
    }

    @Override
    public boolean isDeadOrDying() {
        return this.getCanDie() && super.isDeadOrDying();
    }

    @Override
    public void die(DamageSource damageSource) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, damageSource)) return;
        if (!this.isRemoved() && !this.dead && !this.getIsAntiCheatMode()) {
            Entity entity = damageSource.getEntity();
            LivingEntity livingentity = this.getKillCredit();
            if (this.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.deathScore, damageSource);
            }
            if (this.isSleeping()) {
                this.stopSleeping();
            }
            this.playSound(SoundRegistry.VOID_TENTACLES_LEAVE.get());
            this.dead = true;
            this.deathTime = 20;
            this.getCombatTracker().recheckStatus();
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel serverlevel = (ServerLevel)level;
                if (entity == null || entity.killedEntity(serverlevel, this)) {
                    this.gameEvent(GameEvent.ENTITY_DIE);
                    this.dropAllDeathLoot(damageSource);
                }
                this.level().broadcastEntityEvent(this, (byte)3);
                this.IllusionExplode();
            }
        }
    }

    @Override
    public float getHealth() {
        return this.getActType() == ActType.DEAD? this.getMaxHealth() - Math.max(this.getMaxHealth() * (this. getAlphaPercent() / 70f),0):this.getMaxHealth();
    }

    @Override
    public double getAttributeValue(@NotNull Attribute attribute) {
        if (attribute == Attributes.MAX_HEALTH){
            return Math.max(finalLimit,super.getAttributeValue(attribute));
        }
        return super.getAttributeValue(attribute);
    }

    @Override
    public void heal(float num) {
        num = net.minecraftforge.event.ForgeEventFactory.onLivingHeal(this, num);
        if (num <= 0) return;
        if (!this.getCanKill()) {
            this.setExistence(this.getExistence() - num);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.getActType() != ActType.DEAD) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            float yaw = (float) (Math.atan2(dz, dx) * 180.0F / Math.PI) - 90.0F;
            this.setYRot(yaw);
            this.setYBodyRot(yaw);
            this.setYHeadRot(yaw);

            if (cooldown <= 0 && this.getActType() == ActType.NONE && this.getPreActType() == ActType.NONE) {
                ActType selected = selectSpellByWeight();
                if (selected != ActType.NONE) {
                    this.setPreActType(selected);
                }
            }
        }

        if (!this.getIsIllusion()){
            if (this.getActType() == ActType.DEAD) {
                this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
//            }else if (this.tickCount % 40 == 0 || this.getActType() == ActType.PHASE ) {
//                this.bossEvent.setProgress(this.getExistenceHealth()/this.getMaxHealth());
//            }
            }else {this.bossEvent.setProgress(this.getExistenceHealth()/this.getMaxHealth());}
        }
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.ILLAGER;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (this.getCanKill() ) {
            super.remove(reason);
        }else {
            if (canDiscard(this)){
                super.remove(reason);
                return;
            }
            this.setAntiCheatMode(true);
        }
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.getHomePos().equals(BlockPos.ZERO)) {
            this.setHomePos(this.blockPosition());
            this.setNowPos(this.blockPosition());
        }
        if (this.getIsIllusion()) return;
        if (isSilent())
            this.bossEvent.setName(this.getBossName());

    }

    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        if (this.getIsIllusion()) return;
        this.bossEvent.setName(this.getBossName());

    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (this.getIsIllusion()) return;
        this.bossEvent.addPlayer(pPlayer);
        Messages.sendToPlayer(new ClientboundEntityEvent<NamelessWizardsEntity>(this, START_MUSIC), pPlayer);
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        if (this.getIsIllusion()) return;
        this.bossEvent.removePlayer(pPlayer);
        Messages.sendToPlayer(new ClientboundEntityEvent<NamelessWizardsEntity>(this, STOP_MUSIC), pPlayer);
    }

    public Component getBossName() {
        return this.getIsPhase2()?Component.literal("???"):this.getDisplayName();
    }

    @Override
    public @NotNull Vec3 position() {
        if (this.getCanKill()) return super.position();
        return this.getNowPos().getCenter().add(0,-0.5,0);
    }

    @Override
    public @NotNull ChunkPos chunkPosition() {
        if (this.getCanKill()) return super.chunkPosition();
        return new ChunkPos(this.getNowPos());
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 p_20257_) {
        super.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void move(MoverType type, Vec3 pos) {}

    @Override
    public void push(Entity entity) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean isNoGravity() {
        return true;
    }


    public void IllusionExplode(){
        Vec3 spawn = this.getEyePosition();
        ExtendedFireworkRocket firework = new ExtendedFireworkRocket(this.level(), randomFireworkRocket(), this, spawn.x, spawn.y, spawn.z, true, this.getSpellDamage(12),5);
        this.level().addFreshEntity(firework);
        firework.shoot(0, 0, 0, 0, 0);
        if (!this.getIsIllusion()) return;
        this.setCanDie(true);
        this.setCanKill(true);
        this.discard();
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (source.getEntity() == null || this.getActType() == ActType.START || source.getEntity() instanceof NamelessWizardsEntity) return false;
        Entity tar = source.getEntity();
        Entity directSource = source.getDirectEntity();
        if (this.distanceTo(tar) > 8) {
            Vec3 impactPos = directSource != null ? directSource.position() : tar.position();
            impactPos = this.getBoundingBox().getCenter().add(this.getBoundingBox().getCenter().vectorTo(impactPos).normalize().scale(2));

            if (this.level() instanceof ServerLevel serverLevel) {

                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                        impactPos.x, impactPos.y, impactPos.z,
                        1, 0.0, 0.0, 0.0, 0.0);

                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        impactPos.x, impactPos.y, impactPos.z,
                        15, 0.5, 0.5, 0.5, 0.1);

                serverLevel.playSound(null,
                        impactPos.x, impactPos.y, impactPos.z,
                        SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE,
                        1.0f, 1.5f);
            }

            if (directSource instanceof Projectile projectile) {
                projectile.setDeltaMovement(this.getBoundingBox().getCenter().vectorTo(impactPos).normalize().scale(9));
            }
            return false;
        }

        if (this.getIsIllusion() && !this.level().isClientSide() && this.distanceTo(tar) <= 6 && this.getPreActType() != ActType.TP && this.getActType() != ActType.ILLUSION) {
            this.IllusionExplode();
            return false;
        }

        if (this.getActType().resistance == 1 || this.getWhiteDown() > 0 && this.getDamagecooldown() > 0 ) {
            return false;
        }
        return flag;
    }

    @Override
    public void actuallyHurt(DamageSource source, float damage){
        if (this.getActType() == ActType.DEAD && !this.getCanKill()) {
            return;
        }
        if (this.getDamagecooldown() > 0){
            this.playSound(SoundRegistry.FORCE_IMPACT.get(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.setOverhitCount(this.getOverhitCount() + 1);
            if (this.getOverhitCount() >= 4){
                List<NamelessWizardsEntity> Illusions = this.getAllIllusion();
                if (!Illusions.isEmpty()){
                    this.changeBody(Illusions.get(this.random.nextInt(Illusions.size())));
                }
            }
        }
        if (this.getActType().resistance == 1 || this.getDamagecooldown() > 0 || this.getWhiteDown() > 0 || source.getEntity() == null || source.getEntity() instanceof NamelessWizardsEntity || this.isInvulnerableTo(source)) {
            return ;
        }
        Entity tar = source.getEntity();
        if (this.distanceTo(tar) > 8) return ;

        if (this.getCanKill() && !this.level().isClientSide()){
            this.setCanDie(true);
        }
        super.actuallyHurt(source, damage);
        damage = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, source, damage);
        if (damage <= 0) return;
        damage = this.getDamageAfterArmorAbsorb(source,damage);
        damage = this.getDamageAfterMagicAbsorb(source,damage);
        float f1 = Math.max(damage - this.getAbsorptionAmount(), 0.0F);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (damage - f1));
        float f = damage - f1;
        if (f > 0.0F && f < 3.4028235E37F) {
            Entity entity = source.getEntity();
            if (entity instanceof ServerPlayer serverplayer) {
                serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }
        }

        f1 = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, source, f1);

        if (f1 == 0.0F) return;
        if (!(source.isCreativePlayer() && source.getEntity() instanceof Player)){
            //十万白马尽折蹄
            if (f1 > this.getMaxHealth() * 100000 && !this.getIsIllusion()) this.setAntiCheatMode(true);
            f1 = f1 * (1 - this.getActType().resistance);
            if (this.getIsPhase2()){
                f1 = (float) Math.min(Math.max(0,(f1 - this.getArmorValue())), this.getMaxHealth() * 0.04);
            }else {
                f1 = (float) Math.min(f1, this.getMaxHealth() * 0.04);
            }
            if (!(source instanceof SpellDamageSource)){
                f1 = f1 * 0.5f;
            }
        }
        if (this.getActType() != ActType.DEAD) this.playSound(SoundEvents.EVOKER_HURT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);


        this.getCombatTracker().recordDamage(source, f1);
        this.setDamagecooldown(30);
        this.setExistence(this.getExistence() + f1);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - f1);

        this.gameEvent(GameEvent.ENTITY_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return null;
    }
    @Override
    protected float getSoundVolume() {
        return 4.0F;
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        if (this.getActType().canbreak) {
            this.cancelCastAnimation = ActType.BREAK;
            this.setCooldown(0);
            this.setWhiteDown(4);
        }
        if (this.getIsIllusion() && !this.level().isClientSide()) {
            this.IllusionExplode();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RegistryItem.NAMELESS_HELMET.get()));
        this.setDropChance(EquipmentSlot.HEAD, 0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = Utils.random;
        if (!this.getIsPhase2()) this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }


    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.MAX_HEALTH, 160)
                .add(Attributes.ARMOR, 7)
                .add(Attributes.ARMOR_TOUGHNESS, 7)
                .add(RegistryAttribute.EX_PROTECT_LEVEL.get(), 7)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }



    //是栗牢师让我动起来的
    private final RawAnimation idle_animation = RawAnimation.begin().thenLoop("idle") ;
    private final RawAnimation arrow_animation = RawAnimation.begin().thenPlay("shoot_arrow") ;
    private final RawAnimation wind_animation = RawAnimation.begin().thenPlay("wind") ;
    private final RawAnimation blast_animation = RawAnimation.begin().thenPlay("blast") ;
    private final RawAnimation swing_animation = RawAnimation.begin().thenPlay("swing") ;
    private final RawAnimation throw_animation = RawAnimation.begin().thenPlay("throw") ;
    private final RawAnimation swing_tp_animation = RawAnimation.begin().thenPlay("swing_teleport") ;
    private final RawAnimation start_animation = RawAnimation.begin().thenPlay("start") ;
    private final RawAnimation firework_animation = RawAnimation.begin().thenPlay("fireworks") ;
    private final RawAnimation break_animation = RawAnimation.begin().thenPlay("break_stance") ;
    private final RawAnimation swing_third_animation = RawAnimation.begin().thenPlay("swing_third") ;
    private final RawAnimation clone_animation = RawAnimation.begin().thenPlay("charge_clone") ;
    private final RawAnimation break2_animation = RawAnimation.begin().thenPlay("break_phase2") ;
    private final RawAnimation phase2_animation = RawAnimation.begin().thenLoop("phase2_cast") ;
    private final RawAnimation dead_animation = RawAnimation.begin().thenPlay("death") ;



    RawAnimation animationToPlay = null;

    private final AnimationController masterController = new AnimationController(this, "master", 1, this::masterPredicate);

    private PlayState masterPredicate(AnimationState event) {
        if (cancelCastAnimation != null) return PlayState.STOP;

        ActType type = this.getActType();
        RawAnimation animation = switch (type) {
            case SHOOT -> arrow_animation;
            case WIND -> wind_animation;
            case BLAST -> blast_animation;
            case VEX -> swing_animation;
            case COMEBACK -> swing_animation;
            case CREEPER -> throw_animation;
            case TP -> swing_tp_animation;
            case START -> start_animation;
            case BREAK -> break_animation;
            case FIREWORK -> firework_animation;
            case SHOT_M -> swing_third_animation;
            case ILLUSION -> clone_animation;
            case BREAK2 -> break2_animation;
            case PHASE -> phase2_animation;
            case DEAD -> dead_animation;
            default -> idle_animation;
        };

        event.getController().setAnimation(animation);
        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(masterController);
    }

    public void setShootCoolDown(int shootCoolDown) {
        this.shootCoolDown = shootCoolDown;
    }

    public int getShootCoolDown() {
        return this.shootCoolDown;
    }

    public static final byte STOP_MUSIC = 0;
    public static final byte START_MUSIC = 1;

    @Override
    public void handleClientEvent(byte eventId) {
        switch (eventId) {
            case STOP_MUSIC -> NamelessWizardMusicManager.stop(this);
            case START_MUSIC -> NamelessWizardMusicManager.createOrResumeInstance(this);
        }
    }


    //动作类别
    public enum ActType {
        NONE(0,false,null,0),
        SHOOT(1,false,ParticleTypes.PORTAL,0),
        WIND(2,true,ParticleTypes.ENCHANT,0),
        TP(3,false,ParticleHelper.UNSTABLE_ENDER, 0.4F),
        VEX(4,true,ParticleTypes.ENCHANT,0),
        ILLUSION(5,false,ParticleTypes.ENCHANT,0.7f),
        BLAST(6,false,ParticleHelper.UNSTABLE_ENDER,0),
        FIREWORK(7,true,ParticleTypes.ASH,0),
        SHOT_M(8,true,ParticleTypes.SNOWFLAKE,0),
        CREEPER(9,true,ParticleTypes.ENCHANT,0),
        COMEBACK(10,true,ParticleTypes.PORTAL,0),
        START(20,false,null,1),
        BREAK(21,false,null,0),
        BREAK2(22,false,null,1),
        PHASE(99,false,ParticleTypes.ENCHANT,1),
        DEAD(100,false,null,0),;

        private final int id;
        private final boolean canbreak;
        public final ParticleOptions handparticle;
        public final float resistance;

        public static ActType getFromId(int idIn) {
            for(ActType NamelessWizardEntity$ActType : values()) {
                if (idIn == NamelessWizardEntity$ActType.id) {
                    return NamelessWizardEntity$ActType;
                }
            }

            return NONE;
        }

        ActType(int idIn,boolean canbreak,@Nullable ParticleOptions handparticle,float resistance) {
            this.id = idIn;
            this.canbreak = canbreak;
            this.handparticle = handparticle;
            this.resistance = resistance;
        }
    }

    private static class SpellWeight {
        ActType spell;
        int weight;
        Predicate<NamelessWizardsEntity> condition;

        SpellWeight(ActType spell, int weight, Predicate<NamelessWizardsEntity> condition) {
            this.spell = spell;
            this.weight = weight;
            this.condition = condition;
        }
    }

    private ActType selectSpellByWeight() {
        List<SpellWeight> pool = Arrays.asList(
                new SpellWeight(ActType.VEX, 20, e -> !e.getIsIllusion()),
                new SpellWeight(ActType.ILLUSION, 1, e -> !e.getIsIllusion() && getAllIllusion().size() >= 2 && !e.getIsAntiCheatMode()),
                new SpellWeight(ActType.ILLUSION, 80, e -> !e.getIsIllusion() && getAllIllusion().size() <= 1 && !e.getIsAntiCheatMode()),
                new SpellWeight(ActType.SHOOT, 30, e -> e.getShootCoolDown() <= 0),
                new SpellWeight(ActType.FIREWORK, 40, e -> true),
                new SpellWeight(ActType.CREEPER, 30, e -> true),
                new SpellWeight(ActType.COMEBACK, 70, e -> !e.getIsIllusion() && e.getTarget() != null && e.getTarget().position().distanceTo(e.getHomePos().getCenter()) > 25),
                new SpellWeight(ActType.SHOT_M, 50, e -> true),
                new SpellWeight(ActType.TP, 20, e -> true),
                new SpellWeight(ActType.TP, 20, NamelessWizardsEntity::getIsIllusion),
                new SpellWeight(ActType.WIND,  40, e -> e.getTarget() != null && e.distanceTo(e.getTarget()) < 8 ),
                new SpellWeight(ActType.WIND,  70, e -> e.getTarget() != null && e.distanceTo(e.getTarget()) < 2 ),

                new SpellWeight(ActType.BLAST, 15, e -> e.getIsPhase2() && e.getShootCoolDown() <= 0)
        );

        List<SpellWeight> available = new ArrayList<>();
        int totalWeight = 0;
        for (SpellWeight sw : pool) {
            if (sw.condition.test(this)) {
                available.add(sw);
                totalWeight += sw.weight;
            }
        }

        if (available.isEmpty()) {
            return ActType.NONE;
        }

        int rand = this.random.nextInt(totalWeight);
        int cumulative = 0;
        for (SpellWeight sw : available) {
            cumulative += sw.weight;
            if (rand < cumulative) {
                return sw.spell;
            }
        }

        return ActType.NONE;
    }

    public abstract class GeoActGoal extends Goal {
        protected int spellWarmup;
        protected int spellTick;
        protected final double globalCoolDown;
        protected NamelessWizardsEntity entity;

        protected GeoActGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            entity = ent;
            this.globalCoolDown = globalCoolDown;
        }

        public boolean canUse() {
            LivingEntity livingentity = entity.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (entity.isCasting() || entity.getCooldown() > 0 || cancelCastAnimation != null) {
                    return false;
                }
                return entity.getPreActType() == getActType();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity =entity.getTarget();
            return livingentity != null && livingentity.isAlive() && this.spellTick > 0 && cancelCastAnimation == null && entity.getActType() == getActType();
        }

        public void start() {
            this.spellWarmup = this.getCastWarmupTime();
            this.spellTick = this.getCastingTime();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                entity.playSound(soundevent, 4.0F, 1.0F);
            }
            entity.setAlphaPercent(0);
            entity.setActType(this.getActType());
        }

        public void stop() {
            double cooldown = entity.getIsPhase2()?globalCoolDown/2:globalCoolDown;
            entity.setCooldown(cooldown + (entity.getIsIllusion()?10:0));
            if (entity.getActType() == getActType()) {
                entity.setPreActType(ActType.NONE);
            }
            entity.setActType(ActType.NONE);
        }

        public void tick() {

            if (this.spellWarmup == 15) {
                SoundEvent soundevent = this.getSoundOnCast();
                if (soundevent != null) {
                    entity.playSound(soundevent, 4.0F, 1.0F);
                }
            }
            SoundEvent soundevent = this.getTickSpellPrepareSound();
            if (soundevent != null) {
                if (this.spellWarmup > 15 && this.spellTick % this.getTickPrepareSoundTick() == 0) {
                    entity.playSound(soundevent, 4.0F, 1.0F);
                }
            }

            --this.spellWarmup;
            --this.spellTick;


            if (this.spellWarmup == 0) {
                this.castSpell();

            }
        }


        @Nullable
        public SoundEvent getTickSpellPrepareSound(){
            return null;
        }

        public int getTickPrepareSoundTick(){
            return 1;
        }

        @Nullable
        public SoundEvent getSoundOnCast(){
            return null;
        }

        protected abstract void castSpell();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract ActType getActType();
    }

    class BlastSpellGoal extends GeoActGoal {

        private BlastSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 33;
        }

        @Override
        protected int getCastWarmupTime() {return 21;}

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {
                Vec3 EndPos = target.getBoundingBox().getCenter();
                Vec3 StartPos = entity.getBoundingBox().getCenter().add(entity.getLookAngle().scale(0.5));
                Vec3 BlastVec = StartPos.vectorTo(EndPos);
                EldritchBlastVisualEntity BlastEnt = new EldritchBlastVisualEntity(entity.level(), StartPos, EndPos, entity);

                double yRot = Math.atan2(BlastVec.z(), BlastVec.x());
                yRot = Math.toDegrees(yRot) - 90.0f;
                while (yRot < -180) yRot += 360;
                while (yRot >= 180) yRot -= 360;
                double horizontalDistance = Math.sqrt(BlastVec.x() * BlastVec.x() + BlastVec.z() * BlastVec.z());
                double xRot = Math.atan2(BlastVec.y(), horizontalDistance);
                xRot = Math.toDegrees(xRot);
                BlastEnt.setXRot((float) -xRot);
                BlastEnt.setYRot((float) yRot);
                entity.level().addFreshEntity(BlastEnt);

                entity.playSound(SoundRegistry.ELDRITCH_BLAST.get(), 4.0F, 1.0F);

                entity.setHealthAttack(((target.getMaxHealth() - target.getHealth())/target.getMaxHealth()/2f + 0.05f) * 100,target);
                DamageSources.applyDamage(target, entity.getSpellDamage(5), entity.damageSources().indirectMagic(entity,entity));
                MagicManager.spawnParticles(entity.level(), ParticleHelper.UNSTABLE_ENDER, EndPos.x, EndPos.y, EndPos.z, 50, 0, 0, 0, .3, false);
            }

        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.BLAST;
        }
    }

    class WindSpellGoal extends GeoActGoal {

        private WindSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 15;
        }

        @Override
        protected int getCastWarmupTime() {return 7;}

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {
                float range = 8;
                float strength = 4;

                Vec3 sourcePos = entity.position().add(0, entity.getEyeHeight() * 0.7, 0);
                Vec3 targetPos = target.getBoundingBox().getCenter();
                Vec3 relativeVec = targetPos.subtract(sourcePos);
                float yaw = (float) (Mth.atan2(relativeVec.x, relativeVec.z) * (180.0 / Math.PI));

                DamageGustCollider gust = new DamageGustCollider(level(), entity);
                gust.setPos(entity.position().add(0, entity.getEyeHeight() * .7, 0).add(entity.getForward().normalize().scale(2f)));
                gust.range = range;
                gust.strength = strength;
                gust.amplifier = 4;
                gust.setYRot(yaw);
                gust.setDamage(entity.getSpellDamage(10));
                level().addFreshEntity(gust);
                gust.setDealDamageActive();
                gust.tick();
            }

        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.GUST_CHARGE.get();
        }

        @Override
        public SoundEvent getTickSpellPrepareSound() {
            return SoundEvents.PHANTOM_FLAP;
        }

        @Override
        public int getTickPrepareSoundTick(){
            return 1;
        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundRegistry.SONIC_BOOM.get();
        }

        @Override
        protected ActType getActType()
        {
            return ActType.WIND;
        }
    }

    class ShootSpellGoal extends GeoActGoal {

        private ShootSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 55;
        }

        @Override
        protected int getCastWarmupTime() {return 16;}

        @Override
        protected void castSpell() {
            entity.setShootCoolDown(300);
        }

        @Override
        public void tick(){
            super.tick();
            if (this.spellWarmup <= 0 && this.spellTick > 20) {
                LivingEntity target = entity.getTarget();

                if (target == null || !target.isAlive()) {
                    return;
                }

                Vec3 StarPos = entity.position().add(0, entity.getEyeHeight() * 1.15, 0).add(entity.getForward().normalize().scale(1.5f));
                Vec3 EndPos = target.getBoundingBox().getCenter();

                noGravityMagicArrow arrow = new noGravityMagicArrow(entity.level(), entity);
                if (StarPos.distanceTo(EndPos) < 1.5f) {
                    entity.setHealthAttack(0.5f,target);
                    DamageSources.applyDamage(target, 2, SpellRegistry.ARROW_VOLLEY_SPELL.get().getDamageSource(arrow, entity));
                    arrow.discard();
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT,
                                target.getX(), target.getY() + 1, target.getZ(),
                                15, 0.2, 0.2, 0.2, 0.2);
                    }
                }else {


                    arrow.setDamage(entity.getSpellDamage(2));
                    arrow.setPos(StarPos);
                    Vec3 forward = entity.getForward().normalize();
                    Vec3 shootVec = StarPos.vectorTo(EndPos);
                    if (shootVec.dot(forward) < 0) {
                        shootVec = forward;
                    }
                    arrow.shoot(shootVec.x, shootVec.y, shootVec.z, 1.5F, 0.0F);
                    entity.level().addFreshEntity(arrow);
                }
                entity.playSound(SoundEvents.ARROW_SHOOT, 4.0F, 1.0F);

            }
        }

        @Override
        public void start() {
            super.start();
            entity.getAllIllusion(true).forEach(illusion -> {
                illusion.setShootCoolDown(200);
            });
        }

        @Override
        public SoundEvent getTickSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        @Override
        public int getTickPrepareSoundTick(){
            return 3;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.SHOOT;
        }
    }

    class VexSpellGoal extends GeoActGoal {

        private VexSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 17;
        }

        @Override
        protected int getCastWarmupTime() {return 10;}

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {

                Level world = entity.level();
                Vec3 forward = entity.getForward().normalize();
                Vec3 left = new Vec3(-forward.z, 0, forward.x).normalize();   // 左方向
                Vec3 right = new Vec3(forward.z, 0, -forward.x).normalize();  // 右方向

                Vec3 basePos = entity.position().add(0, entity.getEyeHeight() * 0.7, 0);

                spawnVex(world, entity, basePos.add(forward.scale(2)),target);
                spawnVex(world, entity, basePos.add(left.scale(2)),target);
                spawnVex(world, entity, basePos.add(right.scale(2)),target);


            }

        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundRegistry.ARIAL_SUMMONING_5_CUSTOM_1.get();
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.VEX;
        }
    }

    class ComeBackSpellGoal extends GeoActGoal {

        private ComeBackSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 17;
        }

        @Override
        protected int getCastWarmupTime() {return 10;}

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity =entity.getTarget();
            return super.canContinueToUse() && livingentity != null && livingentity.position().distanceTo(entity.getHomePos().getCenter()) >= 25;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.spellWarmup <= 0) return ;
            LivingEntity target = entity.getTarget();
            if (target != null) {
                Vec3 p = target.getEyePosition();
                MagicManager.spawnParticles(entity.level(), ParticleHelper.UNSTABLE_ENDER, p.x, p.y, p.z, 25, 0.5, 0.5, 0.5, .18, true);
            }

        }

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {
                target.setPos(entity.getHomePos().getCenter());
                EchoingStrikeEntity echo = new EchoingStrikeEntity(entity.level(), entity, entity.getSpellDamage(35), 6f);
                echo.setPos(entity.getHomePos().getCenter().subtract(0, echo.getBbHeight() * .5f, 0));
                entity.level().addFreshEntity(echo);
            }
        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundRegistry.ARIAL_SUMMONING_5_CUSTOM_1.get();
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.ABYSSAL_SHROUD.get();
        }

        @Override
        protected ActType getActType()
        {
            return ActType.COMEBACK;
        }
    }

    class CreeperSpellGoal extends GeoActGoal {

        private CreeperSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 17;
        }

        @Override
        protected int getCastWarmupTime() {return 10;}

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {

                Level world = entity.level();
                Vec3 spawnPos = entity.getEyePosition().add(entity.getForward());
                Vec3 targetPos = target.getBoundingBox().getCenter();
                double dist = entity.distanceTo(target);
                targetPos = targetPos.add(0, dist * 1.2 , 0);

                Vec3 shootVec = targetPos.subtract(spawnPos);

                float speed = (float) dist / 20.0F;

                float damage = entity.getSpellDamage(8);

                CreeperChainEntiy head = new CreeperChainEntiy(entity, world, speed, damage);
                head.setPos(spawnPos.x, spawnPos.y, spawnPos.z);;
                head.setDeltaMovement(shootVec.normalize().scale(speed));

                world.addFreshEntity(head);

            }

        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundEvents.CREEPER_PRIMED;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.CREEPER;
        }
    }

    private void spawnVex(Level world, LivingEntity caster, Vec3 pos,LivingEntity target) {
        RushVexEntity vex = new RushVexEntity(world, caster, this.getSpellDamage(7));
        vex.setPos(pos.x, pos.y, pos.z);
        vex.setTarget(target);
        world.addFreshEntity(vex);
    }

    class FireWorkSpellGoal extends GeoActGoal {

        private FireWorkSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 45;
        }

        @Override
        protected int getCastWarmupTime() {return 15;}

        @Override
        protected void castSpell() {
            LivingEntity target = entity.getTarget();
            if (target != null) {
                Level level = entity.level();
                Vec3 Pos = target.position();

                FireworkWarnEntity ent1 = new FireworkWarnEntity(level,entity,entity.getSpellDamage(10),2);
                ent1.setPos(Pos);
                level.addFreshEntity(ent1);

                RandomSource random = entity.getRandom();
                for (int i = 0; i < 6; i++) {
                    double angle = random.nextDouble() * 3 * Math.PI;
                    double distance = 3 + random.nextDouble() * 6;
                    double dx = Math.cos(angle) * distance;
                    double dz = Math.sin(angle) * distance;
                    Vec3 randomPos = Pos.add(dx, 0, dz);
                    FireworkWarnEntity ent2 = new FireworkWarnEntity(level,entity,10,2);
                    ent2.setPos(randomPos);
                    level.addFreshEntity(ent2);
                }
            }

        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_CELEBRATE;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.FIREWORK;
        }
    }

    class TPSpellGoal extends GeoActGoal {

        private TPSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 14;
        }

        @Override
        protected int getCastWarmupTime() {return 14;}

        @Override
        public void tick() {
            super.tick();
            if (this.spellTick == 1) {
                entity.setWhiteDown(5);
            }
        }

        @Override
        protected void castSpell() {
            BlockPos spawnPos = getRandomSpawnPositionWithoutSameType();
            if (spawnPos != null) {
                var p = entity.getEyePosition();
                MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, p.x, p.y, p.z, 25, 0.5, 0.5, 0.5, .18, true);
                entity.setNowPos(spawnPos);
                entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 4.0F, 1.0F);
            }
        }
        @Override
        public void stop() {
            super.stop();
            cancelCastAnimation = ActType.NONE;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.TP;
        }
    }

    class StartGeoGoal extends GeoActGoal {

        private StartGeoGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        @Override
        public boolean canUse() {
            return entity.getPreActType() == getActType();
        }
        @Override
        public boolean canContinueToUse() {
            return this.spellTick > 0 ;
        }

        @Override
        protected int getCastingTime()
        {
            return 45;
        }

        @Override
        protected int getCastWarmupTime() {return 38;}


        @Override
        protected void castSpell() {
            cancelCastAnimation = ActType.NONE;
        }

        @Override
        public void start() {
            super.start();
            entity.setAlphaPercent(100);
        }

        @Override
        public void tick() {
            super.tick();
            if (entity.getAlphaPercent() > 0) {
                entity.setAlphaPercent(Math.max(entity.getAlphaPercent() - 3, 0));
            }
        }



        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.RAISE_DEAD_START.get();
        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundEvents.ENDERMAN_TELEPORT;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.START;
        }
    }

    class BreakGeoGoal extends GeoActGoal {

        private BreakGeoGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        @Override
        public boolean canUse() {
            return entity.getPreActType() == ActType.BREAK;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTick > 0 ;
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastWarmupTime() {return 0;}


        @Override
        protected void castSpell() {
        }

        @Override
        public void start() {
            super.start();
        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.SHOCKWAVE_PREPARE.get();
        }


        @Override
        protected ActType getActType()
        {
            return ActType.BREAK;
        }
    }

    class MiniShotSpellGoal extends GeoActGoal {

        private MiniShotSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 15;
        }

        @Override
        protected int getCastWarmupTime() {return 15;}

        @Override
        protected void castSpell() {

        }

        @Override
        public void tick(){
            super.tick();
            if (this.spellTick == 2 || this.spellTick == 6 || this.spellTick == 10) {
                LivingEntity target = entity.getTarget();

                if (target == null || !target.isAlive()) {
                    return;
                }

                Vec3 eyePos = entity.position().add(0, entity.getEyeHeight() * 0.8, 0);
                Vec3 forward = entity.getForward().normalize();
                Vec3 StarPos = eyePos.add(forward.scale(1.5f));
                Vec3 EndPos = target.getBoundingBox().getCenter();

                Vec3 shootVec = StarPos.vectorTo(EndPos);
                if (shootVec.dot(forward) < 0) {
                    shootVec = forward;
                }
                Vec3 finalDir = shootVec.normalize();

                HailStone arrow = new HailStone(entity.level(), entity);
                if (StarPos.distanceTo(EndPos) < 1.5f) {
                    var entities = level().getEntities(target,new AABB(target.blockPosition()).inflate(1));
                    for (Entity entity1 : entities) {
                        double distanceToSurface = entity1.getBoundingBox().distanceToSqr(EndPos);
                        if ((distanceToSurface <= 3.0 || entity1 == target) && !(entity1 instanceof NamelessWizardsEntity)) {
                            if (entity1 instanceof LivingEntity entity2) {
                                entity.setHealthAttack(1f, entity2);
                                DamageSources.applyDamage(entity1, entity.getSpellDamage(5),RegistrySpell.BLIZZARD.get().getDamageSource(arrow, entity));
                            }
                        }
                    }
                    Vec3 eyepos = target.getEyePosition();
                    arrow.impactParticles(eyepos.x,entity.getEyeHeight(),eyepos.z);
                    arrow.discard();
                } else {
                    arrow.setDamage(entity.getSpellDamage(5));
                    arrow.setPos(StarPos.x, StarPos.y, StarPos.z);
                    arrow.shoot(finalDir.x, finalDir.y, finalDir.z, 1.5F, 0.0F);
                    arrow.setExplosionRadius(1.5f);

                    entity.level().addFreshEntity(arrow);
                    entity.playSound(SoundRegistry.ICE_CAST.get(), 4.0F, 1.0F);
                }
            }
        }


        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected ActType getActType()
        {
            return ActType.SHOT_M;
        }
    }

    class CloneSpellGoal extends GeoActGoal {

        private CloneSpellGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        public boolean canUse() {
            return super.canUse();
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastWarmupTime() {return 45;}

        @Override
        protected void castSpell() {
            ServerLevel serverLevel = (ServerLevel)entity.level();
            List<@NotNull NamelessWizardsEntity> EntList = new ArrayList<>(List.of());
            for (int i = 0; i < 2; ++i) {
                LivingEntity target = entity.getTarget();
                BlockPos canUsePose = getRandomSpawnPositionWithoutSameType();
                if (canUsePose != null) {
                    NamelessWizardsEntity illusion = RegistryEntity.NAMELESS.get().create(entity.level());
                    if (illusion != null) {
                        illusion.setIsPhase2(entity.getIsPhase2());
                        illusion.finalizeSpawn(serverLevel, entity.level().getCurrentDifficultyAt(illusion.getNowPos()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                        illusion.setNowPos(canUsePose);
                        illusion.setHomePos(entity.getHomePos());
                        if(target != null ) {
                            illusion.setTarget(target);
                        }
                        illusion.setCustomBossIdentity(entity.getCustomBossIdentity());
                        illusion.setPreActType(ActType.START);
                        illusion.setExistence(entity.getExistence());
                        illusion.setIsIllusion(true);
                        illusion.setAlphaPercent(100);
                        EntList.add(illusion);
                        serverLevel.addFreshEntityWithPassengers(illusion);
                    }
                }
            }

            entity.setDamagecooldown(120);
            if (!EntList.isEmpty()) {
                EntList.add(entity);
                int index = entity.random.nextInt(EntList.size());
                if (!EntList.get(index).equals(entity)) {
                    entity.changeBody(EntList.get(index));
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            entity.setPreActType(ActType.TP);
        }

        @Override
        public SoundEvent getTickSpellPrepareSound() {
            return SoundRegistry.ABYSSAL_SHROUD.get();
        }

        @Override
        public int getTickPrepareSoundTick(){
            return 6;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTick > 0 && cancelCastAnimation == null && entity.getActType() == getActType();
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        public SoundEvent getSoundOnCast() {
            return SoundRegistry.SHOCKWAVE_CAST.get();
        }

        @Override
        protected ActType getActType()
        {
            return ActType.ILLUSION;
        }
    }

    class BreakPhaseGoal extends GeoActGoal {

        private BreakPhaseGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        @Override
        public boolean canUse() {
            return entity.getPreActType() == ActType.BREAK2;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTick > 0 && entity.getActType() == ActType.BREAK2 ;
        }

        @Override
        protected int getCastingTime()
        {
            return 31;
        }

        @Override
        protected int getCastWarmupTime() {return 31;}


        @Override
        protected void castSpell() {
            var p = entity.getEyePosition();
            MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, p.x, p.y, p.z, 25, 0.5, 0.5, 0.5, .18, true);
            entity.setNowPos(entity.getHomePos());
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 4.0F, 1.0F);
            entity.getAllIllusion().forEach(NamelessWizardsEntity::IllusionExplode);
        }

        @Override
        public void stop() {
            super.stop();
            cancelCastAnimation = ActType.PHASE;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.spellTick == 2) {
                entity.setWhiteDown(5);
            }
        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.SHOCKWAVE_PREPARE.get();
        }

        @Override
        protected ActType getActType()
        {
            return ActType.BREAK2;
        }
    }

    class ChangePhaseGoal extends GeoActGoal {
        private static final int[] NUMBERS = {3, 8, 11,15};
        private static final Random RANDOM = new Random();
        public static int[] getTwoDistinctNumbers() {
            int firstIndex = RANDOM.nextInt(NUMBERS.length);
            int secondIndex;
            do {
                secondIndex = RANDOM.nextInt(NUMBERS.length);
            } while (secondIndex == firstIndex);
            return new int[]{NUMBERS[firstIndex], NUMBERS[secondIndex]};
        }

        private ChangePhaseGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        @Override
        public boolean canUse() {
            return entity.getPreActType() == ActType.PHASE;
        }

        public boolean canContinueToUse() {
            return this.spellTick > 0 ;
        }

        @Override
        protected int getCastingTime()
        {
            return 230;
        }

        @Override
        protected int getCastWarmupTime() {return 230;}


        @Override
        protected void castSpell() {
            entity.setIsPhase2(true);
            ItemStack helmetStack = entity.getItemBySlot(EquipmentSlot.HEAD);
            entity.bossEvent.setName(entity.getBossName());
            if (!helmetStack.isEmpty()) {
                entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                entity.spawnAtLocation(helmetStack);
            }
        }

        @Override
        public void tick() {
            super.tick();
            Level level = entity.level();
            Vec3 start = entity.position().add(0, entity.getEyeHeight() * 2, 0);
            if (this.spellTick > 10 && this.spellTick % 3 == 0) {
                entity.setAlphaPercent(entity.getAlphaPercent() + 1);
            }
            if (this.spellTick  == 140){
                entity.playSound(SoundRegistry.BLACK_HOLE_LOOP.get(), 4.0F, 1.0F);
            }
            if (this.spellTick % 5 == 0){
                entity.setExistence((float) Math.max(0,entity.getExistence() - entity.getMaxHealth() * 0.005));
            }

            if (this.spellTick % 20 == 0 && entity.getTarget() != null) {

                Vec3 Pos = entity.getTarget().position();
                FireworkWarnEntity ent1 = new FireworkWarnEntity(level,entity,entity.getSpellDamage(10),2);
                ent1.setPos(Pos);
                level.addFreshEntity(ent1);
            }
            if (this.spellTick % 2 == 0){

                double line =  (double) Math.min((230 - this.spellTick),200) / 15;
                int fullSegments = (int) line;
                double partial = line - fullSegments;

                List<BlockPos> positions = entity.getPositionsAroundHome();
                int maxIndex = positions.size() - 1;

                for (int i = 0; i < fullSegments && i <= maxIndex; i++) {
                    Vec3 targetPos = positions.get(i).getCenter();
                    Vec3 end = BlockPos.containing(targetPos).getCenter();
                    EntityUtil.particleLine(start, end, (ServerLevel) level(), 40, ParticleTypes.ENCHANT);
                    MagicManager.spawnParticles(entity.level(), ParticleTypes.PORTAL, end.x, end.y, end.z, 20, 0, 1, 0, .3, false);
                }

                if (this.spellTick < 200 && entity.getTarget() != null) {
                    Entity target = entity.getTarget();
                    if (target.position().distanceTo(entity.getHomePos().getCenter()) > 25){
                        int rnd = RANDOM.nextInt(positions.size());
                        Vec3 targetPos = positions.get(rnd).getCenter();
                        target.setPos(targetPos);
                        EchoingStrikeEntity echo = new EchoingStrikeEntity(entity.level(), entity, entity.getSpellDamage(35), 6f);
                        echo.setPos(targetPos.subtract(0, echo.getBbHeight() * .5f, 0));
                        entity.level().addFreshEntity(echo);
                    }

                }

                if (this.spellTick % 20 == 0) {
                    Vec3 targetPos = positions.get(RANDOM.nextInt(positions.size())).getCenter();
                    Vec3 end = BlockPos.containing(targetPos).getCenter();
                    spawnVex(level, entity, end,entity.getTarget());
                    entity.playSound(SoundEvents.VEX_CHARGE, 4.0F, 1.0F);
                }

                if (partial > 0 && fullSegments < positions.size()) {
                    Vec3 nextTarget = positions.get(fullSegments).getCenter();
                    Vec3 nextEnd = BlockPos.containing(nextTarget).getCenter();
                    Vec3 direction = nextEnd.subtract(start);
                    Vec3 partialEnd = start.add(direction.scale(partial));
                    EntityUtil.particleLine(start, partialEnd, (ServerLevel) level(), (int) Math.floor(40 * partial), ParticleTypes.ENCHANT);
                }

                if (partial == 0){

                    entity.playSound(SoundEvents.EVOKER_CELEBRATE, 4.0F, 1.0F);
                    for (int r : getTwoDistinctNumbers()) {
                        float tentacles = r * 2;
                        for (int i = 0; i < tentacles; i++) {
                            Vec3 random = new Vec3(Utils.getRandomScaled(2), 0, Utils.getRandomScaled(2));
                            Vec3 spawn = entity.position().add(new Vec3(0, 0, 1.3 * (r + 1)).yRot(((6.281f / tentacles) * i))).add(random);
                            FireworkWarnEntity ent = new FireworkWarnEntity(level,entity,entity.getSpellDamage(20),2);
                            ent.setPos(spawn);
                            level.addFreshEntity(ent);
                        }
                    }

                    for (int i = 0; i < fullSegments && i <= maxIndex; i++) {
                        Vec3 targetPos = positions.get(i).getCenter();
                        spawnVex(level, entity, targetPos,entity.getTarget());
                    }

                    MagicManager.spawnParticles(entity.level(), ParticleTypes.FLASH, start.x, start.y, start.z, 1,0,0,0, 0, false);

                }

                double clusterRadius = Math.min(this.spellTick * 0.0075, 1.5);

                int particleDensity = (int) (clusterRadius * 55) + 10;

                MagicManager.spawnParticles(entity.level(), ParticleTypes.WITCH, start.x, start.y, start.z, particleDensity, clusterRadius, clusterRadius, clusterRadius, 0, false);

            }

            if (this.spellTick == 30) {
                EvocationBurstEntity echo = new EvocationBurstEntity(entity.level(), entity, entity.getSpellDamage(20), 13f);
                echo.setPos(start.subtract(0, echo.getBbHeight() * .5f, 0));
                level.addFreshEntity(echo);
            }
        }

        @Override
        public void stop() {
            super.stop();
            entity.setAlphaPercent(0);
        }


        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.BLACK_HOLE_LOOP.get();
        }

        @Override
        protected ActType getActType()
        {
            return ActType.PHASE;
        }
    }

    class DeadGoal extends GeoActGoal {

        private DeadGoal(NamelessWizardsEntity ent,double globalCoolDown) {
            super(ent,globalCoolDown);
        }

        @Override
        public boolean canUse() {
            return entity.getPreActType() == ActType.DEAD;
        }

        public boolean canContinueToUse() {
            return true ;
        }

        @Override
        protected int getCastingTime()
        {
            return 230;
        }

        @Override
        protected int getCastWarmupTime() {return 70;}


        @Override
        protected void castSpell() {
            entity.setCanKill(true);
            Vec3 pos = entity.position();
            MagicManager.spawnParticles(entity.level(),ParticleTypes.SOUL,pos.x,pos.y,pos.z,40,0.5,1,0.5,0,true);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.spellTick > 10 && entity.getAlphaPercent() < 70) {
                entity.setAlphaPercent(entity.getAlphaPercent() + 1);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.SHOCKWAVE_PREPARE.get();
        }


        @Override
        protected ActType getActType()
        {
            return ActType.DEAD;
        }
    }

}
