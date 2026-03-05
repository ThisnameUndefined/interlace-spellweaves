package org.xszb.interlace_spellweaves.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergySwirlLayer {
    public static class Vanilla extends RenderLayer<Player, HumanoidModel<Player>> {
        public static ModelLayerLocation ENERGY_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "energy_layer"), "main");
        private final HumanoidModel<Player> model;
        private final ResourceLocation TEXTURE;
        private final Long shouldRenderFlag;

        public Vanilla(RenderLayerParent pRenderer, ResourceLocation texture, Long shouldRenderFlag) {
            super(pRenderer);
            this.model = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ENERGY_LAYER));
            this.TEXTURE = texture;
            this.shouldRenderFlag = shouldRenderFlag;
        }

        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, Player pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (EnergySwirlLayer.shouldRender(pLivingEntity, shouldRenderFlag)) {
                pMatrixStack.pushPose();
                float scale = 1.1F;
                pMatrixStack.scale(scale, 1, scale);

                pMatrixStack.translate(0.0D, -0.01D, 0.0D);

                float f = (float) pLivingEntity.tickCount + pPartialTicks;
                HumanoidModel<Player> entitymodel = this.model();
                VertexConsumer vertexconsumer = pBuffer.getBuffer(EnergySwirlLayer.getRenderType(TEXTURE, f));

                this.getParentModel().copyPropertiesTo(entitymodel);
                entitymodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 0.8F, 0.8F, 0.8F, 1.0F);

                pMatrixStack.popPose();
            }
        }

        protected HumanoidModel<Player> model() {
            return model;
        }

        protected boolean shouldRender(Player entity) {
            return true;
        }
    }

    private static RenderType getRenderType(ResourceLocation texture, float f) {
        return RenderType.energySwirl(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
    }

    private static boolean shouldRender(LivingEntity entity, Long shouldRenderFlag) {
        return ClientMagicData.getSyncedSpellData(entity).hasEffect(shouldRenderFlag);
    }
}
