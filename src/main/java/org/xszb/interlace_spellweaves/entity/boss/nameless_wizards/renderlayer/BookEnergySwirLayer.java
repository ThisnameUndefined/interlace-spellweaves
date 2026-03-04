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
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BookEnergySwirLayer {
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
            if (BookEnergySwirLayer.shouldRender(animatable,ActType)) {
                float f = (float) animatable.tickCount + partialTick;
                var renderType = BookEnergySwirLayer.getRenderType(TEXTURE, f);
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

        public Shield(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture) {
            super(entityRendererIn);
            this.TEXTURE = texture;
        }

        @Override
        public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (animatable instanceof NamelessWizardsEntity ent && ent.getDamagecooldown() > 0) {
                float f = (float) animatable.tickCount + partialTick;
                var renderType = BookEnergySwirLayer.getRenderType(TEXTURE, f);
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
                var renderType = BookEnergySwirLayer.getRenderType(TEXTURE, f);
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
                var renderType = BookEnergySwirLayer.getRenderType(TEXTURE, f);
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

