package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.xszb.interlace_spellweaves.effect.IMobEffectSetgrade;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectSetgrade {

    @Shadow @Final
    private MobEffect effect;
    @Shadow
    private int amplifier;

    @Override
    public void interlace_spellweaves$setNewAmplifier(LivingEntity entity, int newAmplifier) {
        int oldAmplifier = this.amplifier;
        if (oldAmplifier == newAmplifier ) return;
        if (newAmplifier < 0) {
            entity.removeEffect(effect);
            return;
        }
        this.effect.removeAttributeModifiers(entity, entity.getAttributes(), oldAmplifier);
        this.amplifier = newAmplifier;
        this.effect.addAttributeModifiers(entity, entity.getAttributes(), this.amplifier);
        entity.forceAddEffect((MobEffectInstance)(Object)this, null);
    }
}
