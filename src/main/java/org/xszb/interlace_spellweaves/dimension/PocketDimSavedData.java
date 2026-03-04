package org.xszb.interlace_spellweaves.dimension;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class PocketDimSavedData extends SavedData {
    private boolean generated = false;

    public static PocketDimSavedData load(CompoundTag nbt) {
        PocketDimSavedData data = new PocketDimSavedData();
        data.generated = nbt.getBoolean("Generated");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean("Generated", generated);
        return nbt;
    }

    public boolean isGenerated() { return generated; }
    public void setGenerated(boolean val) { this.generated = val; setDirty(); }

    public static PocketDimSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PocketDimSavedData::load, PocketDimSavedData::new, "island_status");
    }
}