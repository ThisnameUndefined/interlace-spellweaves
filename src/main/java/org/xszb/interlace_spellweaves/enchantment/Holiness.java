package org.xszb.interlace_spellweaves.enchantment;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.xszb.interlace_spellweaves.registries.RegistryEnchantments;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Holiness extends Enchantment implements IConflictsEnchantment {


    public Holiness() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    private static final UUID BASE_UUID = UUID.fromString("e2a5fc51-8806-4eea-d02d-73ed934faa92");

    @Override
    public UUID getBaseUuid() {
        return BASE_UUID;
    }

    @Override
    public List<Enchantment> getConflicts() {
        return List.of(RegistryEnchantments.BLOOD_LINE.get());
    }

    @Override
    public Map<Attribute, AttributeConfig> getAttributeConfigs() {
        return Map.of(
                AttributeRegistry.HOLY_SPELL_POWER.get(), new AttributeConfig(0.03D, 1, AttributeModifier.Operation.MULTIPLY_BASE),
                Attributes.ATTACK_DAMAGE, new AttributeConfig(1D, 2, AttributeModifier.Operation.ADDITION)
        );
    }
}