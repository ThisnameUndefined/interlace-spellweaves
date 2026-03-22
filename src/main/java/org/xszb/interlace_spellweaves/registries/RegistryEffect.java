package org.xszb.interlace_spellweaves.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.effect.RimeVeilEffect;
import org.xszb.interlace_spellweaves.effect.SpellEmpowermentEffect;

public class RegistryEffect {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, InterlaceSpellWeaves.MODID);

    public static final RegistryObject<MobEffect> RIME_VEIL = MOB_EFFECT_DEFERRED_REGISTER.register("rime_veil", () -> new RimeVeilEffect(MobEffectCategory.BENEFICIAL, 0x5A6C81).addAttributeModifier(Attributes.MOVEMENT_SPEED,"324b7cc0-2b1c-f247-ceb9-985fc8f36197",.20, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> SPELL_EMPOWERMENT = MOB_EFFECT_DEFERRED_REGISTER.register("spell_improve", () -> new SpellEmpowermentEffect(MobEffectCategory.BENEFICIAL, 0x5A6C81));

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }

}
