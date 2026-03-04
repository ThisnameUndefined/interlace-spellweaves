package org.xszb.interlace_spellweaves.api.spells;

import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class SpellAnimations {
    public static ResourceLocation ANIMATION_RESOURCE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "animations/casting_animations.json");


    public static final AnimationHolder ANIMATION_ICE_SLASH = new AnimationHolder("iss_csw:ice_slash", true);

    public static final AnimationHolder IN_FOG = new AnimationHolder("iss_csw:in_fog", true);

}
