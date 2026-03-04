package org.xszb.interlace_spellweaves.entity.spells.rushvex;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RushVexRenderer extends GeoEntityRenderer<RushVexEntity> {
    public static final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/vex.png");

    public RushVexRenderer (EntityRendererProvider.Context renderManager) {
        super(renderManager, new RushVexModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(RushVexEntity animatable) {
        return textureLocation;
    }

    @Override
    public RenderType getRenderType(RushVexEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
