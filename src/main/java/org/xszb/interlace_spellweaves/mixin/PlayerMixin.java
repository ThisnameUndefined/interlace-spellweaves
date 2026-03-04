package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;

@Mixin(Player.class)
public abstract class PlayerMixin {

    //砲石飞空，坚垣难存
    @ModifyArg(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;",
                    remap = false
            ),
            index = 2
    )
    private boolean forceCriticalHitParameter(boolean originalIsCritical) {
        Player player = (Player)(Object)this;
        if (player.hasEffect(RegistryEffect.RIME_VEIL.get())) {
            return true;
        }
        return originalIsCritical;
    }

    @ModifyArg(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;",
                    remap = false
            ),
            index = 3
    )
    private float forceCriticalDamageModifier(float originalModifier) {
        Player player = (Player)(Object)this;
        if (player.hasEffect(RegistryEffect.RIME_VEIL.get())) {
            player.removeEffect(RegistryEffect.RIME_VEIL.get());
            return 1.5F;
        }
        return originalModifier;
    }


    @Inject(method = "mayBuild", at = @At("HEAD"), cancellable = true)
    private void limitBuild(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player.level().dimension().location().toString().equals(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "pocket_dimension").toString())) {
            if (!player.isCreative()) {
                cir.setReturnValue(false);
            }
        }
    }

}
