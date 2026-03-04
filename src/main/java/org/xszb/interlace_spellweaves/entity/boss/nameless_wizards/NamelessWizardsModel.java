package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import software.bernie.geckolib.core.animation.AnimationState;

public class NamelessWizardsModel extends AbstractSpellCastingMobModel {
    public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "geo/nameless_wizards.geo.json");
    public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/nameless_wizards/nameless_wizards.png");
    public static final ResourceLocation textureResourcePhase2 = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/nameless_wizards/nameless_wizards2.png");
    public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "animations/wizards.animation.json");


    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        return (object instanceof NamelessWizardsEntity ent && ent.getIsPhase2())? textureResourcePhase2:textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
        return animationResource;
    }

    @Override
    public void setCustomAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState) {


    }




}