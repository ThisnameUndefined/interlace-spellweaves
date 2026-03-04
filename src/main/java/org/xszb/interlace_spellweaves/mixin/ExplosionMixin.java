package org.xszb.interlace_spellweaves.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;


@Mixin(Explosion.class)
public abstract  class ExplosionMixin {
    @Shadow @Final private ObjectArrayList<BlockPos> toBlow;
    @Shadow @Final private Level level;

    @Inject(method = "explode", at = @At("TAIL"))
    private void noBlockAdd(CallbackInfo ci) {
        if (this.level.dimension().location().toString().equals(POCKET_DIM)) {
            this.toBlow.clear();
        }
    }
}
