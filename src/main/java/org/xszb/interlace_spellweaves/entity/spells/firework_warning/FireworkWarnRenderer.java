package org.xszb.interlace_spellweaves.entity.spells.firework_warning;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireworkWarnRenderer extends GeoEntityRenderer<FireworkWarnEntity> {
    public FireworkWarnRenderer(EntityRendererProvider.Context context) {
        super(context, new FireworkWarnModel());
    }

    public static final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/warning.png");


    @Override
    public ResourceLocation getTextureLocation(FireworkWarnEntity animatable) {
        return textureLocation;
    }

    @Override
    public RenderType getRenderType(FireworkWarnEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public Color getRenderColor(FireworkWarnEntity animatable, float partialTick, int packedLight) {
        return Color.RED;
    }

    @Override
    public void render(FireworkWarnEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getRadius() * 2;
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

}
