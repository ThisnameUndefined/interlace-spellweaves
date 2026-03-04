package org.xszb.interlace_spellweaves.gui.spell_forge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class SpellForgeScreen  extends ItemCombinerScreen<SpellForgeMenu> {
    private static final ResourceLocation FORGE_LOCATION = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/gui/spell_forge.png");

    public SpellForgeScreen(SpellForgeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, FORGE_LOCATION);
        this.titleLabelX = 10;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiHelper, float pPartialTick, int pX, int pY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, ANVIL_LOCATION);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        guiHelper.blit(FORGE_LOCATION, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        // X over arrow
        if (((this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem() && this.menu.getSlot(2).hasItem()) && !this.menu.getSlot(3).hasItem())) {
            guiHelper.blit(FORGE_LOCATION, leftPos + 77, topPos + 35, this.imageWidth, 0, 28, 21);
        }

    }

    @Override
    protected void renderErrorIcon(GuiGraphics p_281990_, int p_266822_, int p_267045_) {
    }
}
