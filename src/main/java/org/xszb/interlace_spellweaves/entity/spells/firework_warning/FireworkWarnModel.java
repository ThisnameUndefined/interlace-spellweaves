package org.xszb.interlace_spellweaves.entity.spells.firework_warning;

import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import software.bernie.geckolib.model.GeoModel;

public class FireworkWarnModel extends GeoModel<FireworkWarnEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/warning.png");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "geo/warning.geo.json");
    public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "animations/warnig.animation.json");

    public FireworkWarnModel() {
    }

    @Override
    public ResourceLocation getTextureResource(FireworkWarnEntity object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(FireworkWarnEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(FireworkWarnEntity animatable) {
        return ANIMS;
    }
}