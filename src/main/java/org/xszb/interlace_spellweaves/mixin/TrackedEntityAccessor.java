package org.xszb.interlace_spellweaves.mixin;

import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public interface TrackedEntityAccessor {
    @Invoker("broadcast")
    void invokeBroadcast(Packet<?> packet);
}