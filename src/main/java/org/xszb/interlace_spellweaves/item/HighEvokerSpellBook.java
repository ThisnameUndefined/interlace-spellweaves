package org.xszb.interlace_spellweaves.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;

import java.util.List;

public class HighEvokerSpellBook extends UniqueSpellBook {

    public HighEvokerSpellBook() {
        super(SpellDataRegistryHolder.of(
                new SpellDataRegistryHolder(RegistrySpell.BARRAGE_VOLLEY, 10),
                new SpellDataRegistryHolder(RegistrySpell.GUST_PRO,10),
                new SpellDataRegistryHolder(RegistrySpell.CREPER_CHAIN,10)
        ), 7);
        this.withSpellbookAttributes(new AttributeContainer[]{
                new AttributeContainer(AttributeRegistry.SPELL_POWER, (double).09F, AttributeModifier.Operation.MULTIPLY_TOTAL),
                new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, (double).09F, AttributeModifier.Operation.MULTIPLY_TOTAL),
                new AttributeContainer(AttributeRegistry.MAX_MANA, (double)200.0F, AttributeModifier.Operation.ADDITION),
                new AttributeContainer(RegistryAttribute.EX_SPELL_LEVEL, (double)2.0F, AttributeModifier.Operation.ADDITION)
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, level, lines, flag);
        lines.add(Component.translatable("tooltip.iss_csw.nameless_set.name").withStyle(ChatFormatting.GREEN));
        lines.add(Component.translatable("tooltip.iss_csw.nameless_set.desc").withStyle(ChatFormatting.WHITE));

    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        super.initializeSpellContainer(itemStack);
    }


}
