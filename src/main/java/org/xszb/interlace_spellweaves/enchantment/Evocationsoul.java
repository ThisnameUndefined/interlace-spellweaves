package org.xszb.interlace_spellweaves.enchantment;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.registries.RegistryEnchantments;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Evocationsoul extends Enchantment implements IConflictsEnchantment {


    public Evocationsoul() {
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

    private static final UUID BASE_UUID = UUID.fromString("29277bc9-5d69-b071-6f0b-6725d04e2747");

    @Override
    public UUID getBaseUuid() {
        return BASE_UUID;
    }

    @Override
    public List<Enchantment> getConflicts() {
        return List.of(RegistryEnchantments.WILD_GROW.get());
    }

    @Override
    public Map<Attribute, AttributeConfig> getAttributeConfigs() {
        return Map.of(
                AttributeRegistry.EVOCATION_SPELL_POWER.get(), new AttributeConfig(0.03D, 1, AttributeModifier.Operation.MULTIPLY_BASE),
                RegistryAttribute.ARROW_MUL.get(), new AttributeConfig(0.03D, 2, AttributeModifier.Operation.MULTIPLY_BASE)
        );
    }
}