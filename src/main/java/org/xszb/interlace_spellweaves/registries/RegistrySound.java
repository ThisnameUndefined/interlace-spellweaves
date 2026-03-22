package org.xszb.interlace_spellweaves.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class RegistrySound {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, InterlaceSpellWeaves.MODID);


    public static RegistryObject<SoundEvent> NAMELESS_WIZARD_LOOP = registerSoundEvent("entity.nameless_wizard.music.loop");
    public static RegistryObject<SoundEvent> NAMELESS_WIZARD_INTRO = registerSoundEvent("entity.nameless_wizard.music.intro");


    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
