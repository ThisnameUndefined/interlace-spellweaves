package org.xszb.interlace_spellweaves.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EnchantmentIngredient extends AbstractIngredient {

    private final Enchantment enchantment;
    private final int minLevel;

    public EnchantmentIngredient(Enchantment enchantment, int minLevel) {
        super(Stream.of(new EnchValue(enchantment, minLevel)));
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return EnchantmentHelper.getEnchantments(stack).getOrDefault(this.enchantment, 0) >= this.minLevel;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        // 注意：这里的 type 必须与你注册这个 Ingredient 时的 ID 一致！
        json.addProperty("type", "interlace_spellweaves:enchantment");

        ResourceLocation enchId = ForgeRegistries.ENCHANTMENTS.getKey(this.enchantment);
        json.addProperty("enchantment", enchId != null ? enchId.toString() : "");
        json.addProperty("min_level", this.minLevel);

        return json;
    }

    private static record EnchValue(Enchantment ench, int min) implements Value {
        @Override
        public Collection<ItemStack> getItems() {
            // 生成从 minLevel 到最大等级的所有附魔书
            return IntStream.rangeClosed(this.min, this.ench.getMaxLevel())
                    .mapToObj(i -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(this.ench, i)))
                    .toList();
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            ResourceLocation enchId = ForgeRegistries.ENCHANTMENTS.getKey(this.ench);
            json.addProperty("enchantment", enchId != null ? enchId.toString() : "");
            json.addProperty("min_level", this.min);
            return json;
        }
    }

    public static class Serializer implements IIngredientSerializer<EnchantmentIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public EnchantmentIngredient parse(FriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
            int minLevel = buffer.readVarInt();
            return new EnchantmentIngredient(enchantment, minLevel);
        }

        @Override
        public EnchantmentIngredient parse(JsonObject json) {
            String enchIdStr = GsonHelper.getAsString(json, "enchantment");
            ResourceLocation enchId = ResourceLocation.parse(enchIdStr);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchId);

            if (enchantment == null) {
                throw new JsonSyntaxException("Unknown enchantment '" + enchIdStr + "' in recipe!");
            }

            int minLevel = GsonHelper.getAsInt(json, "min_level", 1);
            return new EnchantmentIngredient(enchantment, minLevel);
        }

        @Override
        public void write(FriendlyByteBuf buffer, EnchantmentIngredient ingredient) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(ingredient.enchantment);
            buffer.writeResourceLocation(id != null ? id : ResourceLocation.parse("minecraft:empty"));
            buffer.writeVarInt(ingredient.minLevel);
        }
    }
}
