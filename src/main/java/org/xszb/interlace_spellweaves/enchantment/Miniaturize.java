package org.xszb.interlace_spellweaves.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Miniaturize extends Enchantment implements IConflictsEnchantment {


    public Miniaturize() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    private static final UUID BASE_UUID = UUID.fromString("f8f67254-7756-dc2c-4518-f969319021cb");

    @Override
    public UUID getBaseUuid() {
        return BASE_UUID;
    }

    @Override
    public List<Enchantment> getConflicts() {
        return List.of();
    }

    @Override
    public Map<Attribute, AttributeConfig> getAttributeConfigs() {
        return Map.of(
                Attributes.ATTACK_DAMAGE, new AttributeConfig(-0.1D, 1, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }
}