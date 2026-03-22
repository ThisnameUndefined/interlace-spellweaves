package org.xszb.interlace_spellweaves.mixin;

import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xszb.interlace_spellweaves.item.EnchancedPearlItem;

import java.util.*;

@Mixin(value = ApothEnchantmentMenu.class, priority = 3000)
public abstract class ApothEnchantmentMenuMixin {

    @Inject(
            method = "m_39471_(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void upgradeApothEnchantments(ItemStack stack, int enchantSlot, int level, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {

        EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
        ItemStack fuelStack = menu.getSlot(1).getItem();
        if (fuelStack.getItem() instanceof EnchancedPearlItem item) {
            List<EnchantmentInstance> originalList = cir.getReturnValue();
            if (originalList == null) return;

            List<EnchantmentInstance> upgradedList = new ArrayList<>();

            EnchantmentMenuAccessor accessor = (EnchantmentMenuAccessor) menu;
            int playerSeed = accessor.getEnchantmentSeed().get();
            int seed = Objects.hash(playerSeed, stack.getItem(), level, enchantSlot);
            Random random = new Random(seed);

            for (EnchantmentInstance inst : originalList) {
                upgradedList.add(new EnchantmentInstance(inst.enchantment, inst.level + item.getBonusLevel()));
            }

            Set<Enchantment> rememberedTypes = item.getStaticEnchantments(fuelStack);
            if (rememberedTypes != null) {
                for (Enchantment ench : rememberedTypes) {
                    if (ench.canEnchant(stack) || stack.is(Items.BOOK)) {
                        boolean alreadyIn = upgradedList.stream().anyMatch(i -> i.enchantment == ench);
                        if (!alreadyIn) {
                            int dynamicLevel = Math.max(1, level / 20);
                            upgradedList.add(new EnchantmentInstance(ench, dynamicLevel + item.getBonusLevel()));
                        }
                    }
                }
            }

            if (item.isHasCurse()) {
                var registry = ForgeRegistries.ENCHANTMENTS;
                List<Enchantment> curses = new ArrayList<>();
                registry.forEach(e -> {
                    if (e.isCurse()) curses.add(e);
                });

                if (!curses.isEmpty()) {
                    Enchantment randomCurse = curses.get(random.nextInt(curses.size()));
                    if (upgradedList.stream().noneMatch(i -> i.enchantment == randomCurse)) {
                        upgradedList.add(new EnchantmentInstance(randomCurse, 1));
                    }
                }
            }

            Collections.shuffle(upgradedList, random);
            if (!upgradedList.isEmpty()) {
                cir.setReturnValue(upgradedList);
            }
        }
    }
}