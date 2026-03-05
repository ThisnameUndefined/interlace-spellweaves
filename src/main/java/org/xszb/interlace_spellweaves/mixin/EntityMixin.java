package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.xszb.interlace_spellweaves.entity.boss.UnRemoveBossEntity;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;

import static org.xszb.interlace_spellweaves.util.EntityUtil.canDiscard;

@Mixin(Entity.class)
public abstract class EntityMixin {

    //凤雏岂能消而?
    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    private void onSetRemoved(Entity.RemovalReason reason,CallbackInfo ci)
    {
        Entity self = (Entity) (Object) this;
        if (self instanceof UnRemoveBossEntity ent && !ent.getCanKill() ) {
            if (canDiscard(ent)){
                return;
            }
            ent.setAntiCheatMode(true);
            ci.cancel();
        }
    }

    @Inject(method = "isNoGravity", at = @At(value = "RETURN"), cancellable = true)
    public void setNoGravity(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player ent && ent.isCrouching()) {
            ItemStack helmetStack = ent.getItemBySlot(EquipmentSlot.LEGS);
            boolean hasLeg = !helmetStack.isEmpty() && helmetStack.getItem() instanceof NamelessArmorItem && ((NamelessArmorItem) helmetStack.getItem()).getType().getSlot() == EquipmentSlot.LEGS;
            if (hasLeg) cir.setReturnValue(true);
        }
    }
}
