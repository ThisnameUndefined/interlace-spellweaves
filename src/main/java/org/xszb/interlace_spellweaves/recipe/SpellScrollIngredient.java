package org.xszb.interlace_spellweaves.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpellScrollIngredient extends AbstractIngredient {

    private final ResourceLocation spellId;
    private final @Nullable Integer minlevel;
    private final @Nullable Integer setlevel;
    private ItemStack[] matchingStacks = null;

    public SpellScrollIngredient(ResourceLocation spellId,@Nullable Integer minlevel,@Nullable Integer setlevel) {
        this.spellId = spellId;
        this.minlevel = minlevel;
        this.setlevel = setlevel;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof Scroll scrollItem) {
            return Objects.equals(ISpellContainer.get(stack).getSpellAtIndex(0).getSpell().getSpellId(), spellId.toString());
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack[] getItems() {
        if (matchingStacks == null) {
            List<ItemStack> stacks = new ArrayList<>();
            var spell = SpellRegistry.getSpell(spellId);
            if (setlevel != null) {
                var scrollItem = ItemRegistry.SCROLL.get();
                ItemStack stack = new ItemStack(scrollItem);
                ISpellContainer spellContainer = ISpellContainer.createScrollContainer(spell, setlevel, stack);
                spellContainer.save(stack);
                stacks.add(stack);
            }else {
                for (int level = Math.max(1,minlevel == null? 1:minlevel); level <= spell.getMaxLevel(); level++) {
                    var scrollItem = ItemRegistry.SCROLL.get();
                    ItemStack stack = new ItemStack(scrollItem);
                    ISpellContainer spellContainer = ISpellContainer.createScrollContainer(spell, level, stack);
                    spellContainer.save(stack);
                    stacks.add(stack);
                }
            }
            matchingStacks = stacks.toArray(new ItemStack[0]);
        }
        return matchingStacks;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", InterlaceSpellWeaves.MODID + ":spell_scroll");
        json.addProperty("spell_id", spellId.toString());

        if (minlevel != null) {
            json.addProperty("min_level", minlevel);
        }

        if (setlevel != null) {
            json.addProperty("set_level", setlevel);
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<SpellScrollIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public SpellScrollIngredient parse(@Nonnull JsonObject json) {
            String spellId = json.get("spell_id").getAsString();
            Integer minlevel = null;
            if (json.has("min_level")) {
                minlevel = json.get("min_level").getAsInt();
            }

            Integer setlevel = null;
            if (json.has("set_level")) {
                setlevel = json.get("set_level").getAsInt();
            }

            return new SpellScrollIngredient(ResourceLocation.parse(spellId),minlevel,setlevel);
        }

        @Nonnull
        @Override
        public SpellScrollIngredient parse(@Nonnull FriendlyByteBuf buffer) {
            ResourceLocation spellId = buffer.readResourceLocation();

            Integer minlevel = null;
            if (buffer.readBoolean()) {
                minlevel = buffer.readInt();
            }
            Integer setlevel = null;
            if (buffer.readBoolean()) {
                setlevel = buffer.readInt();
            }

            return new SpellScrollIngredient(spellId, minlevel, setlevel);
        }

        @Override
        public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull SpellScrollIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.spellId);
            if (ingredient.minlevel != null) {
                buffer.writeBoolean(true);
                buffer.writeInt(ingredient.minlevel);
            } else {
                buffer.writeBoolean(false);
            }
            if (ingredient.setlevel != null) {
                buffer.writeBoolean(true);
                buffer.writeInt(ingredient.setlevel);
            } else {
                buffer.writeBoolean(false);
            }
        }
    }
}