package org.xszb.interlace_spellweaves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.xszb.interlace_spellweaves.registries.RegistryItem;

import javax.annotation.Nullable;
import java.util.List;

public class MagicCookiesItem extends Item {

    public static final FoodProperties DEFAULT_COOKIE = new FoodProperties.Builder()
            .nutrition(0).saturationMod(0).fast().build();

    public MagicCookiesItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("MagicNutrition")) {
            int nutrition = nbt.getInt("MagicNutrition");
            float saturation = nbt.getFloat("MagicSaturation");

            return new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationMod(saturation)
                    .alwaysEat()
                    .fast()
                    .build();
        }

        return DEFAULT_COOKIE;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("MagicNutrition")) {
            int nutrition = nbt.getInt("MagicNutrition");
            tooltip.add(Component.translatable("tooltip.iss_csw.magic_cookies.nutrition", nutrition)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(Component.translatable("tooltip.iss_csw.magic_cookies.no_magic")
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public static ItemStack createCustomCookie(int nutrition, float saturation) {
        ItemStack stack = new ItemStack(RegistryItem.MAGIC_COOKIES.get());
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("MagicNutrition", nutrition);
        nbt.putFloat("MagicSaturation", saturation);
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
