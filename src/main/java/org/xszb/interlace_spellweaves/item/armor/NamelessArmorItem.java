package org.xszb.interlace_spellweaves.item.armor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.pumpkin.PumpkinArmorModel;
import io.redspace.ironsspellbooks.entity.armor.pumpkin.PumpkinArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.entity.armor.nameless.NamelessArmorModel;
import org.xszb.interlace_spellweaves.entity.armor.nameless.NamelessArmorRenderer;
import org.xszb.interlace_spellweaves.registries.RegistryItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NamelessArmorItem extends ExtendedArmorItem {

    private static final String UUID_BASE = "cbbec899-66db-831d-962b-56c236793";
    private static final Map<Integer, double[]> SPELL_POWER_MAP = Map.of(
            1, new double[]{0.07, 101.0},
            2, new double[]{0.1, 102.0},
            3, new double[]{0.07, 103.0},
            4, new double[]{0.05, 104.0}
    );

    UUID protectLeve = UUID.fromString("460dc10e-6f9d-602d-67ee-1a8fb4ed9e78");
    UUID healthAdd = UUID.fromString("0b090681-2708-3b8c-9206-54e958066f25");
    UUID spellLevel = UUID.fromString("724d9657-4f6d-ed49-5c01-54ed17af6c23");
    UUID manaRegen = UUID.fromString("0068e3db-5488-af52-ec94-10f00290f800");

    public NamelessArmorItem(Type slot, Properties settings) {
        super(ArmorMaterials.NAMELESS, slot, settings);
    }

    @Override
    public boolean isDamageable(ItemStack stack){
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> parentModifiers = super.getAttributeModifiers(slot, stack);

        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create(parentModifiers);
        if (slot == this.getType().getSlot()) {
            if (this.getType() == Type.HELMET) {
                modifiers.put(RegistryAttribute.EX_PROTECT_LEVEL.get(), new AttributeModifier(protectLeve, "Protection modifier", 7.0, AttributeModifier.Operation.ADDITION));
            }
            if (this.getType() == Type.CHESTPLATE) {
                modifiers.put(RegistryAttribute.EX_SPELL_LEVEL.get(), new AttributeModifier(spellLevel, "SpellLevel modifier", 1.0, AttributeModifier.Operation.ADDITION));
            }
            if (this.getType() == Type.LEGGINGS) {
                modifiers.put(Attributes.MAX_HEALTH, new AttributeModifier(healthAdd, "Health modifier", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            if (this.getType() == Type.BOOTS) {
                modifiers.put(AttributeRegistry.MANA_REGEN.get(), new AttributeModifier(manaRegen, "CastTime modifier", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            int slotIndex = getSlotIndex(this.getType());

            if (SPELL_POWER_MAP.containsKey(slotIndex)) {
                double[] data = SPELL_POWER_MAP.get(slotIndex);
                double valA = data[0];
                int valB = (int) data[1];

                UUID dynamicUuid = UUID.fromString(UUID_BASE + String.format("%03d", valB));

                modifiers.put(AttributeRegistry.SPELL_POWER.get(),
                        new AttributeModifier(dynamicUuid, "Spell Power modifier", valA, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        return modifiers;
    }

    private int getSlotIndex(Type type) {
        return switch (type) {
            case HELMET -> 1;
            case CHESTPLATE -> 2;
            case LEGGINGS -> 3;
            case BOOTS -> 4;
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.iss_cws.nameless_set.name").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.iss_cws.nameless_set.desc").withStyle(ChatFormatting.WHITE));

        Type type = this.getType();
        switch (type) {
            case HELMET -> {
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_helmet_ability.name").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_helmet_ability").withStyle(ChatFormatting.WHITE));
            }
            case CHESTPLATE -> {
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_chestplate_ability.name").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_chestplate_ability").withStyle(ChatFormatting.WHITE));
            }
            case LEGGINGS -> {
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_leggings_ability.name").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_leggings_ability").withStyle(ChatFormatting.WHITE));
            }
            case BOOTS -> {
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_boots_ability.name").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.iss_cws.nameless_boots_ability").withStyle(ChatFormatting.WHITE));
            }
        }

    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new NamelessArmorRenderer(new NamelessArmorModel());
    }

    public static boolean hasFullSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        return !head.isEmpty() && head.getItem() instanceof NamelessArmorItem &&
                !chest.isEmpty() && chest.getItem() instanceof NamelessArmorItem &&
                !legs.isEmpty() && legs.getItem() instanceof NamelessArmorItem &&
                !feet.isEmpty() && feet.getItem() instanceof NamelessArmorItem &&
                CuriosApi.getCuriosInventory(player).map(inv ->
                        inv.findFirstCurio(stack ->
                                stack.is(RegistryItem.HIGH_EVOKER_SPELL_BOOK.get())
                        ).isPresent()
                ).orElse(false);

    }
}
