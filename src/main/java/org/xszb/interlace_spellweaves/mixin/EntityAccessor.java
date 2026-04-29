package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("removalReason")
    void setRemovalReason(@Nullable Entity.RemovalReason reason);

    @Accessor("levelCallback")
    EntityInLevelCallback getLevelCallback();

    @Accessor("chunkPosition")
    void setChunkPos(ChunkPos chunkPos);

    @Accessor(value = "isAddedToWorld",remap = false)
    void setIsAddedToWorld(boolean added);
}
