package org.xszb.interlace_spellweaves.network.client_remove;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.xszb.interlace_spellweaves.util.EntityUtil;

import java.util.function.Supplier;

public class ClientRemovePacket {

    private final int entityId;

    public ClientRemovePacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ClientRemovePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static ClientRemovePacket decode(FriendlyByteBuf buf) {
        return new ClientRemovePacket(buf.readInt());
    }

    public static void handle(ClientRemovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel clientLevel = minecraft.level;
            if (clientLevel != null) {
                Entity entity = clientLevel.getEntity(msg.entityId);
                if (entity != null) {
                    entity.onClientRemoval();

                    EntityUtil.removeClientContainers(clientLevel, entity);
                } else {

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
