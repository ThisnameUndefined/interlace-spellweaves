package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.renderlayer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsRenderer.RED_SHIELD_TEXTURE;

public class NameLessWizardLayer {
    public static final ResourceLocation EVOKE_TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/swir_layer/evoke.png");

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE/* = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/evasion.png")*/;
        private final NamelessWizardsEntity.ActType ActType;

        public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture, NamelessWizardsEntity.ActType ActType) {
            super(entityRendererIn);
            this.TEXTURE = texture;
            this.ActType = ActType;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (NameLessWizardLayer.shouldRender(animatable,ActType)) {
                float f = (float) animatable.tickCount + partialTick;
                var renderType = NameLessWizardLayer.getRenderType(TEXTURE, f);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
                poseStack.pushPose();
                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (bone.getName().equals("book")) {
                            bone.updateScale(1.1f, 1.1f, 1.1f);
                        }else  {
                            bone.setHidden(true);
                        }
                    });
                });

                this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                        packedLight, OverlayTexture.NO_OVERLAY, .5f, .5f, .5f, 1f);


                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (bone.getName().equals("book")) {
                            bone.updateScale(1, 1, 1);
                        }else  {
                            bone.setHidden(false);
                        }
                    });
                });
                poseStack.popPose();
            }
        }

    }

    public static class Shield extends GeoRenderLayer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE/* = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/evasion.png")*/;
        private final ResourceLocation TEXTURE2 = RED_SHIELD_TEXTURE;

        public Shield(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture) {
            super(entityRendererIn);
            this.TEXTURE = texture;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (animatable instanceof NamelessWizardsEntity ent && ent.getDamagecooldown() > 0) {
                float f = (float) animatable.tickCount + partialTick;
                ResourceLocation texture = TEXTURE;
                if (ent.getIsPhase2()) texture = TEXTURE2;
                var renderType = NameLessWizardLayer.getRenderType(texture, f);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
                poseStack.pushPose();
                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1.1f, 1.1f, 1.1f);
                        }else  {
                            bone.setHidden(true);
                        }
                    });
                });

                this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                        packedLight, OverlayTexture.NO_OVERLAY, .5f, .5f, .5f, 1f);


                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1, 1, 1);
                        }else  {
                            bone.setHidden(false);
                        }
                    });
                });
                poseStack.popPose();
            }
        }

    }

    public static class Shield2 extends GeoRenderLayer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE/* = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/evasion.png")*/;

        public Shield2(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture) {
            super(entityRendererIn);
            this.TEXTURE = texture;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (animatable instanceof NamelessWizardsEntity ent && ent.getIsPhase2() && !ent.getIsAntiCheatMode() && ent.getActType() != NamelessWizardsEntity.ActType.DEAD) {
                float f = (float) animatable.tickCount + partialTick;
                var renderType = NameLessWizardLayer.getRenderType(TEXTURE, f);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
                poseStack.pushPose();
                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1.05f, 1.05f, 1.05f);
                        }else  {
                            bone.setHidden(true);
                        }
                    });
                });

                this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                        packedLight, OverlayTexture.NO_OVERLAY, .2f, .2f, .2f, 0.2f);


                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1, 1, 1);
                        }else  {
                            bone.setHidden(false);
                        }
                    });
                });
                poseStack.popPose();
            }
        }

    }

    public static class Shield3 extends GeoRenderLayer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE/* = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/evasion.png")*/;

        public Shield3(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture) {
            super(entityRendererIn);
            this.TEXTURE = texture;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (animatable instanceof NamelessWizardsEntity ent && ent.getIsAntiCheatMode() && ent.getActType() != NamelessWizardsEntity.ActType.DEAD) {
                float f = (float) animatable.tickCount + partialTick;
                var renderType = NameLessWizardLayer.getRenderType(TEXTURE, f);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
                poseStack.pushPose();
                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1.1f, 1.1f, 1.1f);
                        }else  {
                            bone.setHidden(true);
                        }
                    });
                });

                this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                        packedLight, OverlayTexture.NO_OVERLAY, 1.2f, .2f, .2f, 0.2f);


                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (!bone.getName().equals("book")) {
                            bone.updateScale(1, 1, 1);
                        }else  {
                            bone.setHidden(false);
                        }
                    });
                });
                poseStack.popPose();
            }
        }

    }

    public static class White extends GeoRenderLayer<AbstractSpellCastingMob> {
        private static final ResourceLocation WHITE_TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "white_dynamic");

        static {
            NativeImage image = new NativeImage(1, 1, false);
            image.setPixelRGBA(0, 0, 0xFFFFFFFF);

            DynamicTexture dynamicTexture = new DynamicTexture(image);
            Minecraft.getInstance().getTextureManager().register(WHITE_TEXTURE, dynamicTexture);
        }

        public White(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (animatable instanceof NamelessWizardsEntity ent && ent.getWhiteDown() > 0) {
                poseStack.pushPose();

                RenderType whiteRenderType = RenderType.entityTranslucent(WHITE_TEXTURE);

                VertexConsumer whiteBuffer = bufferSource.getBuffer(whiteRenderType);

                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (bone.getName().equals("book")) {
                            bone.setHidden(true);
                        }
                    });
                });

                this.getRenderer().actuallyRender(
                        poseStack,
                        animatable,
                        bakedModel,
                        whiteRenderType,
                        bufferSource,
                        whiteBuffer,
                        true,
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0f, 1.0f, 1.0f, 1.0f
                );

                bakedModel.getBone("body").ifPresent((rootBone) -> {
                    rootBone.getChildBones().forEach(bone -> {
                        if (bone.getName().equals("book")) {
                            bone.setHidden(false);
                        }
                    });
                });

                poseStack.popPose();
            }
        }

    }


    public static class Illusion extends GeoRenderLayer<AbstractSpellCastingMob> {
        private final ResourceLocation TEXTURE;

        public Illusion(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture) {
            super(entityRendererIn);
            this.TEXTURE = texture;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            double distSqr = 0;
            if (Minecraft.getInstance().player == null) {
               return;
            }
            if (animatable instanceof NamelessWizardsEntity ent && (ent.getWhiteDown() > 0 || ent.getAlphaPercent()  > 0)) {
                return;
            }

            if (Minecraft.getInstance().player.isCreative()) return;
            distSqr = Minecraft.getInstance().player.distanceToSqr(animatable);
            if (distSqr < 64.0) return;

            float currentDist = (float) Math.sqrt(distSqr);

            float maxRadius = 0.25f;
            float baseRadius = (float) Math.min((currentDist - 8.0) * 0.03, maxRadius);

            float illusionAlpha = (baseRadius / maxRadius) * 0.25f;

            float gameTime = (animatable.tickCount + partialTick) * 0.1f;
            RenderType ghostType = RenderType.entityTranslucent(this.TEXTURE);
            int cloneCount = 4;

            if (illusionAlpha > 0.01f) {
                setBoneHidden(bakedModel, "book", true);

                for (int i = 0; i < cloneCount; i++) {
                    poseStack.pushPose();

                    float freqX = 1.0f + i * 0.15f;
                    float freqZ = 0.8f + i * 0.22f;
                    float freqY = 1.2f + i * 0.1f;

                    float jitter = (float) Math.sin(gameTime * 5.0f + i) * 0.02f;

                    double offsetX = Math.cos(gameTime * freqX + i) * baseRadius + jitter;
                    double offsetZ = Math.sin(gameTime * freqZ + i) * baseRadius + jitter;
                    double offsetY = Math.sin(gameTime * freqY + i) * 0.15D; // Y轴上下漂浮

                    poseStack.translate(offsetX, offsetY, offsetZ);

                    this.getRenderer().actuallyRender(
                            poseStack, animatable, bakedModel, ghostType, bufferSource, bufferSource.getBuffer(ghostType),
                            true, partialTick, packedLight, packedOverlay,
                            1.2f, 0.4f, 0.4f, illusionAlpha
                    );

                    poseStack.popPose();
                }

                setBoneHidden(bakedModel, "book", false);
            }
        }
    }


    private static void setBoneHidden(BakedGeoModel model, String boneName, boolean hidden) {
        model.getBone("body").ifPresent(root -> {
            root.getChildBones().forEach(bone -> {
                if (bone.getName().equals(boneName)) {
                    bone.setHidden(hidden);
                }
            });
        });
    }

    private static RenderType getRenderType(ResourceLocation texture, float f) {
        return RenderType.energySwirl(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
    }

    private static boolean shouldRender(LivingEntity entity, NamelessWizardsEntity.ActType  ActType) {
        if (entity instanceof NamelessWizardsEntity ent) {
            return ent.getActType() == ActType;
        }
        return false;
    }
}

