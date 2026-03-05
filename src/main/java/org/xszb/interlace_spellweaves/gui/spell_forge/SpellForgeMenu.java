package org.xszb.interlace_spellweaves.gui.spell_forge;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.recipe.SpellMixRecipe;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;
import org.xszb.interlace_spellweaves.registries.RegistryMenu;
import org.xszb.interlace_spellweaves.registries.RegistryRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpellForgeMenu extends ItemCombinerMenu {
    public SpellForgeMenu(int pContainerId, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(RegistryMenu.SPELL_FORGE_MENU.get(), pContainerId, inventory, containerLevelAccess);
    }
    public SpellForgeMenu(int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    private final List<ItemStack> additionalDrops = new ArrayList<>();
    private SpellMixRecipe currentRecipe;

    @Override
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack resultStack) {

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = Objects.requireNonNull(serverPlayer.getServer()).getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID,"main/mix_spell"));
            if (advancement != null) {
                AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) {
                    for (String criteria : progress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(advancement, criteria);
                    }
                }
            }
        }

        this.inputSlots.getItem(0).shrink(1);
        this.inputSlots.getItem(1).shrink(1);
        this.inputSlots.getItem(2).shrink(1);

        for (ItemStack drop : additionalDrops) {
            if (!player.getInventory().add(drop.copy())) {
                player.drop(drop.copy(), false);
            }
        }
        additionalDrops.clear();

        this.createResult();

        this.broadcastChanges();
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(RegistryBlock.SPELL_FORGE_BLOCK.get());
    }

    @Override
    public void createResult() {
        Level level = this.player.level();

        if (level.isClientSide()) {
            return;
        }

        ItemStack mainSpell = this.inputSlots.getItem(1);
        ItemStack addition1 = this.inputSlots.getItem(0);
        ItemStack addition2 = this.inputSlots.getItem(2);

        SimpleContainer tempContainer = new SimpleContainer(3) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return true;
            }
        };
        tempContainer.setItem(0, mainSpell);
        tempContainer.setItem(1, addition1);
        tempContainer.setItem(2, addition2);

        Optional<SpellMixRecipe> recipeOptional = level.getRecipeManager()
                .getRecipeFor(RegistryRecipe.SPELL_MIX_RECIPE.get(), tempContainer, level);

        if (recipeOptional.isPresent()) {
            currentRecipe = recipeOptional.get();
            ItemStack result = currentRecipe.assemble(tempContainer, level.registryAccess());
            this.resultSlots.setItem(0, result);

        } else {
            currentRecipe = null;
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            additionalDrops.clear();
        }

        this.broadcastChanges();
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        super.slotsChanged(container);
        if (container == this.inputSlots) {
            this.createResult();
        }
    }



    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        //copied from anvil for 1.19.4
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 55, 18, (p_266635_) -> {
                    return true;
                })
                .withSlot(1, 78, 18, (p_266634_) -> {
                    return true;
                })
                .withSlot(2, 101, 18, (p_266635_) -> {
                    return true;
                })
                .withResultSlot(3, 78, 47).build();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultSlots && super.canTakeItemForPickAll(pStack, pSlot);
    }
}
