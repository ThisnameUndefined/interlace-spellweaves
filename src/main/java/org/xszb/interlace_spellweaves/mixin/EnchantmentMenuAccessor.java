package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EnchantmentMenu.class)
public interface EnchantmentMenuAccessor {
    @Accessor("enchantSlots")
    Container getEnchantSlots();

    @Accessor("enchantmentSeed")
    DataSlot getEnchantmentSeed();
}