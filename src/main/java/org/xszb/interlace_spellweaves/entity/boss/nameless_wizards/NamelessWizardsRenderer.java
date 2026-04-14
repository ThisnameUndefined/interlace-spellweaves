package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.renderlayer.NameLessWizardLayer;
import software.bernie.geckolib.core.object.Color;

import static io.redspace.ironsspellbooks.render.EnergySwirlLayer.EVASION_TEXTURE;
import static org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.renderlayer.NameLessWizardLayer.EVOKE_TEXTURE;

@OnlyIn(Dist.CLIENT)
public class NamelessWizardsRenderer extends AbstractSpellCastingMobRenderer {
    public static final ResourceLocation SHIELD_TEXTURE = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/shield/shield_overlay.png");
    public static final ResourceLocation RED_SHIELD_TEXTURE = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/swir_layer/shield_overlay.png");

    public static final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/nameless_wizards/nameless_wizards.png");
    public static final ResourceLocation textureLocation2 = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/entity/nameless_wizards/nameless_wizards2.png");

    public NamelessWizardsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NamelessWizardsModel());

        addRenderLayer(new NameLessWizardLayer.Geo(this,EVASION_TEXTURE, NamelessWizardsEntity.ActType.BLAST));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVASION_TEXTURE, NamelessWizardsEntity.ActType.SHOOT));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVASION_TEXTURE, NamelessWizardsEntity.ActType.TP));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVASION_TEXTURE, NamelessWizardsEntity.ActType.ILLUSION));

        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.WIND));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.VEX));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.CREEPER));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.FIREWORK));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.SHOT_M));
        addRenderLayer(new NameLessWizardLayer.Geo(this,EVOKE_TEXTURE, NamelessWizardsEntity.ActType.FANG));


        addRenderLayer(new NameLessWizardLayer.Shield(this,SHIELD_TEXTURE));
        addRenderLayer(new NameLessWizardLayer.Shield2(this,SHIELD_TEXTURE));
        addRenderLayer(new NameLessWizardLayer.Shield3(this,RED_SHIELD_TEXTURE));
        addRenderLayer(new NameLessWizardLayer.White(this));
        addRenderLayer(new NameLessWizardLayer.Illusion(this,textureLocation2));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSpellCastingMob animatable) {
        return (animatable instanceof NamelessWizardsEntity ent && ent.getIsPhase2())? textureLocation2:textureLocation;
    }

    @Override
    public RenderType getRenderType(AbstractSpellCastingMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public int getPackedOverlay(AbstractSpellCastingMob animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public Color getRenderColor(AbstractSpellCastingMob animatable, float partialTick, int packedLight) {

        if (animatable instanceof NamelessWizardsEntity entity) {
            float alpha = entity.getAlphaPercent() > 0 ? (100 - entity.getAlphaPercent()) / 100f : 1.0f;

            if (Minecraft.getInstance().player != null && entity.getAlphaPercent() == 0) {
                double distSqr = Minecraft.getInstance().player.distanceToSqr(animatable);
                if (distSqr > 64.0 && !Minecraft.getInstance().player.isCreative()) {
                    float currentDist = (float) Math.sqrt(distSqr);
                    float fadeFactor = Math.min((currentDist - 8.0f) * 0.085f, 0.85f);
                    alpha -= fadeFactor;
                }
            }

            if (alpha < 1.0f) {
                alpha = Math.max(0.1f, Math.min(1.0f, alpha));
                return Color.ofRGBA(1, 1, 1, alpha);
            }
        }
        return Color.WHITE;
    }

    @Override
    public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (entity instanceof NamelessWizardsEntity ent && ent.getActType().handparticle != null) {

            this.getGeoModel().getBone("body").ifPresent((rootBone) -> {
                rootBone.getChildBones().forEach(bone -> {
                    if (bone.getName().equals("right_arm") || bone.getName().equals("left_arm")) {
                        if ((Math.abs(bone.getRotationVector().x) + Math.abs(bone.getRotationVector().y) + Math.abs(bone.getRotationVector().z)) < 0.5) return;
                        bone.getChildBones().forEach(bone2->{
                            if (bone2.getName().equals("rightItem") || bone2.getName().equals("bipedHandLeft")) {
                                Vector3d pos = bone2.getWorldPosition();
                                double d0 = ent.getRandom().nextGaussian() * 0.07D;
                                double d1 = ent.getRandom().nextGaussian() * 0.07D;
                                double d2 = ent.getRandom().nextGaussian() * 0.07D;

                                entity.level().addParticle(ent.getActType().handparticle,true, pos.x ,pos.y ,pos.z,  d0, d1, d2);
                            }
                        });
                    }
                });
            });
        }

    }

    @Override
    protected void applyRotations(AbstractSpellCastingMob animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.deathTime > 0) {
            float f = ((float)animatable.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            poseStack.mulPose(Axis.ZP.rotationDegrees(-f * 90.0F));
        }
    }
}