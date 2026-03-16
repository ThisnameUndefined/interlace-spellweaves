package org.xszb.interlace_spellweaves.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.xszb.interlace_spellweaves.util.EnchantmentUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IConflictsEnchantment {
    UUID getBaseUuid();

    Map<Attribute, AttributeConfig> getAttributeConfigs();

    List<Enchantment> getConflicts();

    record AttributeConfig(double amountPerLevel, int uuidOffset, AttributeModifier.Operation operation) {}

    default double getEffectiveLevel(ItemStack stack, int originalLevel) {
        double effectiveLevel = (double) originalLevel;
        Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.getEnchantments(stack);

        for (Enchantment conflict : getConflicts()) {
            effectiveLevel -= itemEnchants.getOrDefault(conflict, 0);
        }
        return Math.max(0, effectiveLevel);
    }

    default void applyAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        int originalLevel = EnchantmentHelper.getTagEnchantmentLevel((Enchantment) this, stack);
        if (originalLevel <= 0) return;

        double finalLevel = getEffectiveLevel(stack, originalLevel);
        if (finalLevel <= 0) return;

        EquipmentSlot itemDefaultSlot = LivingEntity.getEquipmentSlotForItem(stack);
        EquipmentSlot currentSlot = event.getSlotType();
        if (currentSlot != itemDefaultSlot) {
            return;
        }

        getAttributeConfigs().forEach((attribute, config) -> {
            UUID finalUuid = EnchantmentUtils.getSlotSpecificUUID(getBaseUuid(), currentSlot, config.uuidOffset());

            AttributeModifier modifier = new AttributeModifier(
                    finalUuid,
                    "Enchantment Modifier: " + attribute.getDescriptionId(),
                    finalLevel * config.amountPerLevel(),
                    config.operation() // 自定义运算类型
            );
            event.addModifier(attribute, modifier);
        });
    }
}
