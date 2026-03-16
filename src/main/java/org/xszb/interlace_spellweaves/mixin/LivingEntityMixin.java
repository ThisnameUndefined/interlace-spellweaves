package org.xszb.interlace_spellweaves.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xszb.interlace_spellweaves.api.magic.IMagicDataExtension;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    //雾隐之术
    @Inject(method = "updateInvisibilityStatus", at = @At(value = "TAIL"))
    public void updateInvisibilityStatus(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(RegistryEffect.RIME_VEIL.get()))
            self.setInvisible(true);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At(value = "RETURN"), cancellable = true)
    public void isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof NamelessWizardsEntity ent && ent.getIsIllusion()) {
            cir.setReturnValue(false);
        }
    }

    //机巧用尽，方化腐朽为神奇
    @ModifyVariable(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I"
            ),
            ordinal = 0
    )
    private int modifyEnchantmentProtection(int k) {
        LivingEntity self = (LivingEntity) (Object) this;
        int bonus = (int) Math.floor(self.getAttributeValue(RegistryAttribute.EX_PROTECT_LEVEL.get()));
        return k + bonus;
    }

    //山水速疾来去易，襄樊镇固永难开
    @Inject(
            method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceArmorCalculation(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        ItemStack helmetStack = self.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasHead = !helmetStack.isEmpty() && helmetStack.getItem() instanceof NamelessArmorItem && ((NamelessArmorItem) helmetStack.getItem()).getType().getSlot() == EquipmentSlot.HEAD;
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && hasHead) {
            amount = CombatRules.getDamageAfterAbsorb(amount, (float)self.getArmorValue(), (float)self.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
            cir.setReturnValue(amount);
        }
    }



    //一人用护盾
    private static EntityDataAccessor<Float> HEALTH_KEY = LivingEntityAccessor.HEALTH();
    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void nameless$shieldDamageReduction(float newHealth, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (newHealth == 0) return;
        float originalNewHealth = newHealth;
        float oldHealth = entity.getHealth();
        if (oldHealth < newHealth && entity.getAttributeValue(RegistryAttribute.HEAL_MUL.get()) != 1){
            double value = newHealth - oldHealth;
            value = value * entity.getAttributeValue(RegistryAttribute.HEAL_MUL.get());
            newHealth = (float) (oldHealth + value);
        }
        //甲来!
        if (entity instanceof Player && newHealth < oldHealth) {
            MagicData magicData = MagicData.getPlayerMagicData(entity);
            if (magicData instanceof IMagicDataExtension ext && ext.arcane_nemeses$isWearingFullNamelessSet()) {
                float damage = oldHealth - newHealth;
                float reducedDamage = Math.max(Math.min(1,damage),damage - entity.getArmorValue() / 4f);
                newHealth = Math.min(oldHealth - reducedDamage, oldHealth);
                if (!entity.level().isClientSide) {
                    Vec3 location = entity.getEyePosition();
                    MagicManager.spawnParticles(entity.level(), ParticleTypes.ELECTRIC_SPARK, location.x, location.y, location.z, 30, .4, .4, .4, .5, false);
                    entity.level().playSound(null, location.x, location.y, location.z, SoundRegistry.FORCE_IMPACT.get(), SoundSource.NEUTRAL, .8f, 1f);
                }
            }
        }

        if (originalNewHealth != newHealth) {
            newHealth = Mth.clamp(newHealth, 0.0F, entity.getMaxHealth());
            entity.getEntityData().set(HEALTH_KEY, newHealth);
            ci.cancel();
        }
    }


    //叠甲,过!
    @Inject(method = "getArmorValue" , at = {@At("RETURN")}, cancellable = true)
    private void newArmor(CallbackInfoReturnable<Integer> cir){
        LivingEntity entity = (LivingEntity) (Object) this;
        double reduceArmor = entity.getAttributeValue(RegistryAttribute.REDUCE_ARMOR.get());
        if (reduceArmor != 0) {
            cir.setReturnValue(Math.max((int) (cir.getReturnValue() + reduceArmor),0));
        }
    }
}
