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

            // 注：由于 BossbarSprite 是 record 类型，标准获取属性的方法是带括号的 .width()
            // 如果你的 IDE 报错，请改回 customSprite.width
            int width = customSprite.width();
            int height = customSprite.height();
            int buffer = customSprite.buffer();
            int yBarOffset = customSprite.yBarOffset();

            int x = (guiGraphics.guiWidth() - width) / 2;
            int y = event.getY() + yBarOffset;

            RenderSystem.enableBlend();

            // 1. 动态构建路径：这会自动补齐 "textures/gui/sprites/" 和 ".png"
            ResourceLocation sprite = customSprite.spriteLocation().withPrefix("textures/gui/sprites/").withSuffix(".png");

            // 2. 动态计算贴图总尺寸（这解决了你的黑紫网格问题！）
            // 引擎假设你的 PNG 图片被精准裁剪：长 = width，宽 = height 的两倍
            int texWidth = width;
            int texHeight = height * 2;

            // 3. 绘制背景层 (从 V = 0 开始切)
            guiGraphics.blit(sprite, x, y, 0.0F, 0.0F, width, height, texWidth, texHeight);

            // 4. 计算进度条宽度 (引入了 Mth.lerpInt，动画更平滑)
            float rawProgress = event.getBossEvent().getProgress();
// 真正能动的部分 = 总宽 - 左边框 - 右边框
            int usableWidth = width - (buffer * 2);
// 实际要画出来的进度宽度
            int progressWidth = Math.round(rawProgress * usableWidth);

// 5. 绘制填充层 (关键修改点)
            if (progressWidth > 0) {
                guiGraphics.blit(
                        sprite,
                        x + buffer,             // 【偏移1】渲染起点向右移，空出左边框
                        y,
                        (float)buffer,          // 【偏移2】UV读取起点向右移，不读取背景贴图的左边框部分
                        (float)height,          // V轴依然是 height (第二层)
                        progressWidth,
                        height,
                        texWidth,
                        texHeight
                );
            }

            RenderSystem.disableBlend();

            // 6. 渲染名字
            Component component = event.getBossEvent().getName();
            int fontWidth = Minecraft.getInstance().font.width(component);
            int textX = guiGraphics.guiWidth() / 2 - fontWidth / 2;
            int textY = y - 9 - yBarOffset; // 修正了 Y 轴偏移，防止文字随血条下移

            guiGraphics.drawString(Minecraft.getInstance().font, component, textX, textY, 0xFFFFFF);

            // 7. 处理多血条排版并拦截原版渲染
            event.setIncrement(event.getIncrement() - 5 + height + yBarOffset);
            event.setCanceled(true);
        }
    }
}