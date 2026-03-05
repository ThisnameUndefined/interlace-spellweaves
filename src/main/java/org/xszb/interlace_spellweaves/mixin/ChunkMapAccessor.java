package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
    @Invoker("addEntity")
    void invokeAddEntity(Entity entity);

    @Invoker("getVisibleChunkIfPresent")
    ChunkHolder invokeGetVisibleChunkIfPresent(long p_140328_);
}
