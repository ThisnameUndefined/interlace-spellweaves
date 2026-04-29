package org.xszb.interlace_spellweaves.dimension;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class PocketDimSavedData extends SavedData {
    private boolean generated = false;
    private int deathCount = 0;

    public static PocketDimSavedData load(CompoundTag nbt) {
        PocketDimSavedData data = new PocketDimSavedData();
        data.generated = nbt.getBoolean("Generated");
        data.deathCount = nbt.getInt("DeathCount");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean("Generated", generated);
        nbt.putInt("DeathCount", deathCount);
        return nbt;
    }

    public boolean isGenerated() { return generated; }
    public void setGenerated(boolean val) { this.generated = val; setDirty(); }

    public int getDeathCount() { return deathCount; }
    public void addDeath() { this.deathCount++; setDirty(); }
    public void resetDeathCount() { this.deathCount = 0; setDirty(); }

    public static PocketDimSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PocketDimSavedData::load, PocketDimSavedData::new, "island_status");
    }
}