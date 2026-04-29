package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xszb.interlace_spellweaves.entity.boss.UnRemoveBossEntity;

import static org.xszb.interlace_spellweaves.util.EntityUtil.canDiscard;

@Mixin(PersistentEntitySectionManager.class)
public abstract class EntityManagerMixin<T extends EntityAccess> {

    //君岂不闻，山皆有其愚公乎？
    @Inject(method = "stopTracking", at = @At("HEAD"), cancellable = true)
    private void preventStopTracking(T entity, CallbackInfo ci) {
        if (entity instanceof UnRemoveBossEntity ent && !ent.getCanKill()) {
            if (canDiscard(ent)){
                return;
            }
            ((EntityAccessor) ent).setRemovalReason(null);
            ci.cancel();
        }
    }

    //衰桐凤不栖，昆山玉已碎！
    @Inject(method = "stopTicking", at = @At("HEAD"), cancellable = true)
    private void preventStopTicking(T entity, CallbackInfo ci) {
        if (entity instanceof UnRemoveBossEntity ent && !ent.getCanKill()) {
            if (canDiscard(ent)){
                return;
            }
            ((EntityAccessor) ent).setRemovalReason(null);
            ci.cancel();
        }
    }
}
