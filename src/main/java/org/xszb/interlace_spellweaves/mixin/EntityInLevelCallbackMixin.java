package org.xszb.interlace_spellweaves.mixin;


import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xszb.interlace_spellweaves.entity.boss.UnRemoveBossEntity;

import static org.xszb.interlace_spellweaves.util.EntityUtil.canDiscard;


@Mixin(targets = "net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback")
public abstract class EntityInLevelCallbackMixin {

    //歌一曲，酒一觞，邀君醉琼浆，东风盈暖房
    @Shadow(remap = false)
    @Final
    private Entity realEntity;

    @Inject(method = "onRemove", at = @At("HEAD"), cancellable = true)
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (this.realEntity instanceof UnRemoveBossEntity ent && !ent.getCanKill()){
            if (canDiscard(ent)){
                return;
            }
            ((EntityAccessor) ent).setRemovalReason(null);
            ent.setAntiCheatMode(true);
            ent.revive();
            if (ent.level() instanceof ServerLevel serverLevel ) {
                ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;
                ((ChunkMapAccessor) chunkMap).invokeAddEntity(ent);
            }

            ci.cancel();
        }
    }

}
