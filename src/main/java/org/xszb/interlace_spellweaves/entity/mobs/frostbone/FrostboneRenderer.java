package org.xszb.interlace_spellweaves.entity.mobs.frostbone;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrostboneRenderer extends AbstractSpellCastingMobRenderer {

    public FrostboneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrostboneModel());
    }
}
