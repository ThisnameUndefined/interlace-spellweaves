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

public class ThunderFlash extends Enchantment implements IConflictsEnchantment {


    public ThunderFlash() {
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

    private static final UUID BASE_UUID = UUID.fromString("714696ec-7afc-04f7-9152-981b6e33c324");

    @Override
    public UUID getBaseUuid() {
        return BASE_UUID;
    }

    @Override
    public List<Enchantment> getConflicts() {
        return List.of(RegistryEnchantments.END_BREATH.get());
    }

    @Override
    public Map<Attribute, AttributeConfig> getAttributeConfigs() {
        return Map.of(
                AttributeRegistry.LIGHTNING_SPELL_POWER.get(), new AttributeConfig(0.03D, 1, AttributeModifier.Operation.MULTIPLY_BASE),
                Attributes.MOVEMENT_SPEED, new AttributeConfig(0.03D, 2, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }
}