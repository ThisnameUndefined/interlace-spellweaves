package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void nameless$preventBreaking(BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        if (isProtectedDimension()) {
            if (!player.isCreative()) {
                player.displayClientMessage(Component.translatable("msg.iss_csw.nameless").withStyle(ChatFormatting.WHITE),true);
                cir.setReturnValue(false);
            }
        }
    }

    private boolean isProtectedDimension() {
        return player.level().dimension().location().toString().equals(POCKET_DIM);
    }
}
