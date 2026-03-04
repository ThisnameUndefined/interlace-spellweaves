package org.xszb.interlace_spellweaves.entity.spells.evocation_strike;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EvocationBurstRenderer extends GeoEntityRenderer<EvocationBurstEntity> {
    public EvocationBurstRenderer(EntityRendererProvider.Context context) {
        super(context, new EvocationBurstModel());
    }

    public static final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/warning.png");


    @Override
    public ResourceLocation getTextureLocation(EvocationBurstEntity animatable) {
        return textureLocation;
    }

    @Override
    public RenderType getRenderType(EvocationBurstEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public Color getRenderColor(EvocationBurstEntity animatable, float partialTick, int packedLight) {
        return Color.RED;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EvocationBurstEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        float scale = animatable.getRadius() * 3f;
        if (bone.getName().equals("sub")) {
            float progress = (float) (animatable.tickCount + partialTick) / animatable.waitTime * 16 * scale;
            bone.setScaleX(progress);
            bone.setScaleY(progress);
            bone.setScaleZ(progress);
        }
        if (bone.getName().equals("main")) {
            bone.setScaleX(scale);
            bone.setScaleY(scale);
            bone.setScaleZ(scale);
        }

        bone.setPosY(animatable.getBbHeight() * 4.0f);

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

