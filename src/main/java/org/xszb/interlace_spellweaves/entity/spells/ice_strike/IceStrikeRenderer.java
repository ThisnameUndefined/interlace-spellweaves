package org.xszb.interlace_spellweaves.entity.spells.ice_strike;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class IceStrikeRenderer extends EntityRenderer<IceStrikeEntity> {
    private static final ResourceLocation[] TEXTURES = {
            InterlaceSpellWeaves.id("textures/entity/ice_strike/ice_strike_1.png"),
            InterlaceSpellWeaves.id("textures/entity/ice_strike/ice_strike_2.png"),
            InterlaceSpellWeaves.id("textures/entity/ice_strike/ice_strike_3.png"),
            InterlaceSpellWeaves.id("textures/entity/ice_strike/ice_strike_4.png")
    };

    public IceStrikeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IceStrikeEntity entity) {
        int frame = (entity.tickCount / entity.ticksPerFrame) % TEXTURES.length;
        return TEXTURES[frame];
        //return TEXTURE;
    }

    @Override
    public void render(IceStrikeEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        PoseStack.Pose pose = poseStack.last();
        poseStack.mulPose(Axis.YP.rotationDegrees(90 - entity.getYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
        float randomZ = new Random(31L * entity.getId()).nextInt(5, 12);
        poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));

        drawSlash(pose, entity, bufferSource, entity.getBbWidth() * 3.5f, entity.isMirrored());

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private void drawSlash(PoseStack.Pose pose, IceStrikeEntity entity, MultiBufferSource bufferSource, float width, boolean mirrored) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        float halfWidth = width * .5f;
        float height = entity.getBbHeight() * .5f;
        //old color: 125, 0, 10
        consumer.vertex(poseMatrix, -halfWidth, height, -halfWidth).color(255, 255, 255, 255).uv(0f, mirrored ? 0f : 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, height, -halfWidth).color(255, 255, 255, 255).uv(1f, mirrored ? 0f : 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, height, halfWidth).color(255, 255, 255, 255).uv(1f, mirrored ? 1f : 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, height, halfWidth).color(255, 255, 255, 255).uv(0f, mirrored ? 1f : 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }


}
