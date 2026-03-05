package org.xszb.interlace_spellweaves.item;

import com.google.common.collect.ImmutableMultimap;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.api.magic.IMagicDataExtension;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;

import static org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem.hasFullSet;

public class HighEvokerSpellBook extends UniqueSpellBook {

    public HighEvokerSpellBook() {
        super(SpellRarity.LEGENDARY, SpellDataRegistryHolder.of(
                new SpellDataRegistryHolder(RegistrySpell.BARRAGE_VOLLEY, 10),
                new SpellDataRegistryHolder(RegistrySpell.GUST_PRO,10),
                new SpellDataRegistryHolder(RegistrySpell.CREPER_CHAIN,10)
        ), 7, () -> {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(UUID.fromString("7eefd1c7-4c52-54c7-a2c4-d9231efd3ba6"), "Weapon modifier", .15, AttributeModifier.Operation.MULTIPLY_BASE));
            builder.put(AttributeRegistry.MAX_MANA.get(), new AttributeModifier(UUID.fromString("f867fa6f-ce2d-6abc-d4ef-a46c167ae4e8"), "Weapon modifier", 250, AttributeModifier.Operation.ADDITION));
            builder.put(RegistryAttribute.EX_SPELL_LEVEL.get(), new AttributeModifier(UUID.fromString("1fe76b21-afef-48e5-17c6-1e68394a0b45"), "Weapon modifier", 3, AttributeModifier.Operation.ADDITION));

            return builder.build();
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, level, lines, flag);
        lines.add(Component.translatable("tooltip.iss_cws.nameless_set.name").withStyle(ChatFormatting.GREEN));
        lines.add(Component.translatable("tooltip.iss_cws.nameless_set.desc").withStyle(ChatFormatting.WHITE));

    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        super.initializeSpellContainer(itemStack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player){
            MagicData magicData = MagicData.getPlayerMagicData(player);
            if (magicData instanceof IMagicDataExtension extension) {
                boolean isFull = hasFullSet(player);
                extension.arcane_nemeses$setWearingFullNamelessSet(isFull);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onUnequip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player){
            MagicData magicData = MagicData.getPlayerMagicData(player);
            if (magicData instanceof IMagicDataExtension extension) {
                boolean isFull = hasFullSet(player);
                extension.arcane_nemeses$setWearingFullNamelessSet(isFull);
            }
        }
    }
}
