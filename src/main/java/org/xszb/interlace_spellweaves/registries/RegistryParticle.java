package org.xszb.interlace_spellweaves.registries;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class RegistryParticle {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, InterlaceSpellWeaves.MODID);

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

}
