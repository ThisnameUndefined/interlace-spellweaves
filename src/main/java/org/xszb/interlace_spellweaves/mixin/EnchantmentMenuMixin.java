package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xszb.interlace_spellweaves.item.EnchancedPearlItem;

import java.util.*;

@Mixin(value = EnchantmentMenu.class, priority = 2000)
public abstract class EnchantmentMenuMixin {
    @Shadow @Final private Container enchantSlots;
    @Shadow @Final public int[] costs;
    @Shadow @Final private DataSlot enchantmentSeed;

    @Inject(
            method = "getEnchantmentList",
            at = @At("RETURN"),
            cancellable = true
    )
    private void upgradeFinalEnchantments(ItemStack stack, int slotIndex, int level, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        ItemStack fuelStack = this.enchantSlots.getItem(1);

        if (fuelStack.getItem() instanceof EnchancedPearlItem item) {
            List<EnchantmentInstance> originalList = cir.getReturnValue();
            List<EnchantmentInstance> upgradedList = new ArrayList<>();


            int playerSeed = this.enchantmentSeed.get();
            int seed = Objects.hash(playerSeed, stack.getItem(), level, slotIndex);
            Random random = new Random(seed);

            if (originalList != null && !originalList.isEmpty()) {
                for (EnchantmentInstance inst : originalList) {
                    int newLevel = inst.level + item.getBonusLevel();
                    upgradedList.add(new EnchantmentInstance(inst.enchantment, newLevel));
                }
            }

            Set<Enchantment> rememberedTypes = item.getStaticEnchantments(fuelStack);
            if (rememberedTypes != null) {
                for (Enchantment ench : rememberedTypes) {
                    boolean canApply = ench.canEnchant(stack) || stack.is(Items.BOOK);
                    if (canApply) {
                        boolean alreadyIn = upgradedList.stream().anyMatch(i -> i.enchantment == ench);
                        if (!alreadyIn) {
                            int dynamicLevel = 1;
                            for (int l = ench.getMaxLevel(); l >= ench.getMinLevel(); --l) {
                                if (level >= ench.getMinCost(l)) {
                                    dynamicLevel = l;
                                    break;
                                }
                            }
                            upgradedList.add(new EnchantmentInstance(ench, dynamicLevel + item.getBonusLevel()));
                        }
                    }
                }
            }

            if (item.isHasCurse()) {
                var registry = ForgeRegistries.ENCHANTMENTS;
                List<Enchantment> curses = new ArrayList<>();

                for (Enchantment enchantment : registry) {
                    if (enchantment.isCurse()) {
                        curses.add(enchantment);
                    }
                }

                if (!curses.isEmpty()) {
                    Enchantment randomCurse = curses.get(random.nextInt(curses.size()));
                    boolean alreadyHasCurse = upgradedList.stream().anyMatch(i -> i.enchantment == randomCurse);
                    if (!alreadyHasCurse) {
                        upgradedList.add(new EnchantmentInstance(randomCurse, 1));
                    }
                }
            }

            Collections.shuffle(upgradedList,random);
            if (!upgradedList.isEmpty()) {
                cir.setReturnValue(upgradedList);
            }
        }
    }
}