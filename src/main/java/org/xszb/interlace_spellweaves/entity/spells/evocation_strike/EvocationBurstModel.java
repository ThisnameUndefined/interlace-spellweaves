package org.xszb.interlace_spellweaves.entity.spells.evocation_strike;

import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.spells.firework_warning.FireworkWarnEntity;
import software.bernie.geckolib.model.GeoModel;

public class EvocationBurstModel extends GeoModel<EvocationBurstEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/warning.png");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "geo/warning.geo.json");
    public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "animations/warnig.animation.json");

    public EvocationBurstModel() {
    }

    @Override
    public ResourceLocation getTextureResource(EvocationBurstEntity object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(EvocationBurstEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(EvocationBurstEntity animatable) {
        return ANIMS;
    }
}