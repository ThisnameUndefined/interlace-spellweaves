package org.xszb.interlace_spellweaves.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.xszb.interlace_spellweaves.api.magic.IMagicDataExtension;
import org.xszb.interlace_spellweaves.api.magic.SyncedEffectData;

@Mixin(value = MagicData.class, remap = false)
public abstract class MagicDataMixin implements IMagicDataExtension {
    @Shadow
    public abstract SyncedSpellData getSyncedData();

    @Unique
    private boolean arcane_nemeses$wearingFullNamelessSet;

    @Override
    public boolean arcane_nemeses$isWearingFullNamelessSet() {
        return this.arcane_nemeses$wearingFullNamelessSet;
    }

    @Override
    public void arcane_nemeses$setWearingFullNamelessSet(boolean active) {
        this.arcane_nemeses$wearingFullNamelessSet = active;
        SyncedSpellData syncedData = this.getSyncedData();
        if (active) {
            syncedData.addEffects(SyncedEffectData.NAMELESS_SET_SHIELD);
        } else {
            syncedData.removeEffects(SyncedEffectData.NAMELESS_SET_SHIELD);
        }
    }
}
