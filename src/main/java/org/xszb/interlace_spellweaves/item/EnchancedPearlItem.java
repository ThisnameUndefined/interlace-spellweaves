package org.xszb.interlace_spellweaves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class EnchancedPearlItem extends Item {
    private final int bonusLevel;
    private final Supplier<List<Enchantment>> staticEnchantmentsProvider;
    private final boolean hasCurse;

    public EnchancedPearlItem(Properties properties, int bonusLevel, @Nullable List<Enchantment> staticEnchantments, boolean hasCurse) {
        super(properties);
        this.bonusLevel = bonusLevel;
        this.staticEnchantmentsProvider = () -> staticEnchantments != null ? staticEnchantments : Collections.emptyList();
        this.hasCurse = hasCurse;
    }

    public EnchancedPearlItem(Properties properties, int bonusLevel, Supplier<List<Enchantment>> staticEnchantmentsProvider, boolean hasCurse) {
        super(properties);
        this.bonusLevel = bonusLevel;
        this.staticEnchantmentsProvider = staticEnchantmentsProvider;
        this.hasCurse = hasCurse;
    }

    public EnchancedPearlItem(Properties properties, int bonusLevel,boolean hasCurse) {
        this(properties, bonusLevel, Collections::emptyList,hasCurse);
    }

    public int getBonusLevel() {
        return this.bonusLevel;
    }

    public boolean isHasCurse() {
        return this.hasCurse;
    }

    public Set<Enchantment> getStaticEnchantments(ItemStack pearlStack) {
        Set<Enchantment> types = new HashSet<>(this.staticEnchantmentsProvider.get());
        types.addAll(EnchantmentHelper.getEnchantments(pearlStack).keySet());
        return types;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 22;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.iss_csw.enhanced_pearl_transfer")
                .withStyle(ChatFormatting.YELLOW));

        if (this.bonusLevel > 0) {
            tooltip.add(Component.translatable("tooltip.iss_csw.enhanced_pearl_prefix")
                    .append(Component.literal("+" + bonusLevel))
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("tooltip.iss_csw.enhanced_pearl_desc")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        } else {
            tooltip.add(Component.translatable("tooltip.iss_csw.enhanced_pearl_desc2")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        }

        Map<Enchantment, Integer> nbtEnchants = EnchantmentHelper.getEnchantments(stack);
        List<Enchantment> staticEnchants = this.staticEnchantmentsProvider.get();

        Set<Enchantment> allEnchants = new LinkedHashSet<>(staticEnchants);
        allEnchants.addAll(nbtEnchants.keySet());

        if (!allEnchants.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
            tooltip.add(Component.translatable("tooltip.iss_csw.extra_enchantments_header").withStyle(ChatFormatting.GRAY));

            MutableComponent enchantLine = Component.empty();
            Iterator<Enchantment> iterator = allEnchants.iterator();

            while (iterator.hasNext()) {
                Enchantment enchantment = iterator.next();
                enchantLine.append(Component.translatable(enchantment.getDescriptionId()).withStyle(ChatFormatting.GREEN));

                if (iterator.hasNext()) {
                    enchantLine.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                }
            }
            tooltip.add(enchantLine);
        }

        if (this.hasCurse) {
            tooltip.add(Component.translatable("tooltip.iss_csw.enhanced_pearl_desc3")
                    .withStyle(ChatFormatting.RED));
        }
    }

}