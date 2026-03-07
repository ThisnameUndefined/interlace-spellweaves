package org.xszb.interlace_spellweaves.api.registry;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.util.ModTags;

import static io.redspace.ironsspellbooks.api.registry.SchoolRegistry.SCHOOL_REGISTRY_KEY;

public class RegistrySchool {
    private static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, InterlaceSpellWeaves.MODID);

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
    }

    private static RegistryObject<SchoolType> registerSchool(SchoolType schoolType) {
        return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }


    public static final ResourceLocation FUSION_RESOURCE = InterlaceSpellWeaves.id("fusion");

    public static final RegistryObject<SchoolType> FUSION = registerSchool(new SchoolType(
            FUSION_RESOURCE,
            ModTags.FUSION_FOCUS,
            Component.translatable("school.iss_csw.fusion").withStyle(ChatFormatting.GOLD),
            null,
            null,
            LazyOptional.of(SoundRegistry.EVOCATION_CAST::get),
            null
    ));
}
