package org.xszb.interlace_spellweaves.entity.spells.rite_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.xszb.interlace_spellweaves.registries.RegistryItem;

@OnlyIn(Dist.CLIENT)
public class TotemRiteRenderer extends EntityRenderer<TotemRiteEntity> {
    private final ItemRenderer itemRenderer;
    private final ItemStack renderStack1;
    private final ItemStack renderStack2;

    public TotemRiteRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();

        this.renderStack1 = new ItemStack(RegistryItem.TOTEMRITE_ITEM.get());
        this.renderStack2 = new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void render(TotemRiteEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Vec3 pos = entity.getPosition(partialTick);
        poseStack.translate(pos.x - entity.getX(), pos.y - entity.getY(), pos.z - entity.getZ());

        int time = entity.tickCount;
        float floatOffset = ((float) Math.abs(time % 40 - 20)) / 80;
        poseStack.translate(0, entity.getBbHeight() + 0.5 + floatOffset, 0);

        float angle = (time + partialTick) * 2 % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        poseStack.scale(1.0f, 1.0f, 1.0f);
        ItemStack renderStack = entity.isFinishCraft() ? this.renderStack2:this.renderStack1;

        itemRenderer.renderStatic(
                renderStack,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                0
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TotemRiteEntity entity) {
        return null;
    }
}