package org.xszb.interlace_spellweaves.mixin;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;

@Mixin(MagicManager.class)
public abstract class MagicManagerMixin {
    @Inject(
            method = "addCooldown",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void nameless$cancelCooldown(ServerPlayer serverPlayer, AbstractSpell spell, CastSource castSource, CallbackInfo ci) {
        ItemStack chest = serverPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof NamelessArmorItem item && item.getType().getSlot() == EquipmentSlot.CHEST) {
            if (serverPlayer.getRandom().nextDouble() < 0.30) {
                MagicManager.spawnParticles(serverPlayer.level(),
                        ParticleTypes.GLOW,
                        serverPlayer.getX(), serverPlayer.getY() + serverPlayer.getEyeHeight(), serverPlayer.getZ(),
                        30, 0.4, 0.4, 0.4, 0.05, false);
                ci.cancel();
            }
        }
    }
}
