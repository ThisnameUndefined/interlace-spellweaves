package org.xszb.interlace_spellweaves.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class BossbarManager {
    public record BossbarSprite(ResourceLocation spriteLocation, int width, int height, int buffer, int yBarOffset) {}

    private static final Map<UUID, BossbarSprite> CUSTOM_BARS = new HashMap<>();

    public static void startTracking(UUID uuid, BossbarSprite sprite) {
        CUSTOM_BARS.put(uuid, sprite);
    }

    public static void stopTracking(UUID uuid) {
        CUSTOM_BARS.remove(uuid);
    }

    @SubscribeEvent
    public static void renderCustomBossbar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        BossbarSprite customSprite = CUSTOM_BARS.get(event.getBossEvent().getId());
        if (customSprite != null) {
            var guiGraphics = event.getGuiGraphics();

            int width = customSprite.width();
            int height = customSprite.height();
            int buffer = customSprite.buffer();
            int yBarOffset = customSprite.yBarOffset();

            int x = (guiGraphics.guiWidth() - width) / 2;
            int y = event.getY() + yBarOffset;

            RenderSystem.enableBlend();

            ResourceLocation sprite = customSprite.spriteLocation().withPrefix("textures/gui/sprites/").withSuffix(".png");

            int texWidth = width;
            int texHeight = height * 2;

            guiGraphics.blit(sprite, x, y, 0.0F, 0.0F, width, height, texWidth, texHeight);

            float rawProgress = event.getBossEvent().getProgress();

            int usableWidth = width - (buffer * 2);

            int progressWidth = Math.round(rawProgress * usableWidth);


            if (progressWidth > 0) {
                guiGraphics.blit(
                        sprite,
                        x + buffer,
                        y,
                        (float)buffer,
                        (float)height,
                        progressWidth,
                        height,
                        texWidth,
                        texHeight
                );
            }

            RenderSystem.disableBlend();


            Component component = event.getBossEvent().getName();
            int fontWidth = Minecraft.getInstance().font.width(component);
            int textX = guiGraphics.guiWidth() / 2 - fontWidth / 2;
            int textY = y - 9 - yBarOffset; // 修正了 Y 轴偏移，防止文字随血条下移

            guiGraphics.drawString(Minecraft.getInstance().font, component, textX, textY, 0xFFFFFF);

            event.setIncrement(event.getIncrement() - 5 + height + yBarOffset);
            event.setCanceled(true);
        }
    }
}