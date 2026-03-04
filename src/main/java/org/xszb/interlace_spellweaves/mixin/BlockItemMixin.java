package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void stopBlockPlacement(BlockPlaceContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (level.dimension().location().toString().equals(POCKET_DIM)) {
            if (player != null && !player.isCreative()) {
                player.displayClientMessage(Component.translatable("msg.iss_cws.nameless").withStyle(ChatFormatting.WHITE),true);
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
