package org.xszb.interlace_spellweaves.entity.mobs.frostbone;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class FrostboneModel extends AbstractSpellCastingMobModel {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/frost_remains.png");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }
    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return MODEL;
    }
}
