package org.xszb.interlace_spellweaves.entity.spells.rushvex;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RushVexModel extends GeoModel<RushVexEntity> {
    public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/wisp.geo.json");
    public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/wisp/wisp.png");
    public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wisp.animation.json");


    @Override
    public ResourceLocation getModelResource(RushVexEntity object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(RushVexEntity object) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(RushVexEntity animatable) {
        return animationResource;
    }
}
