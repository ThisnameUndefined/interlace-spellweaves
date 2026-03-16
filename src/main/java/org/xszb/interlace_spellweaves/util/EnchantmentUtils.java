package org.xszb.interlace_spellweaves.util;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.UUID;

public class EnchantmentUtils {
    public static UUID getSlotSpecificUUID(UUID baseUuid, EquipmentSlot slot) {
        return getSlotSpecificUUID(baseUuid, slot, 0); // 默认偏移为 0
    }
    public static UUID getSlotSpecificUUID(UUID baseUuid, EquipmentSlot slot, int uuidOffset) {
        long mostSigBits = baseUuid.getMostSignificantBits();
        long leastSigBits = baseUuid.getLeastSignificantBits();

        long slotSuffix = switch (slot) {
            case HEAD -> 1L;
            case CHEST -> 2L;
            case LEGS -> 3L;
            case FEET -> 4L;
            default -> 0L;
        };

        // 清空最后 16 位 (0xFFFF)，然后填入 (偏移 << 8) 和 部位后缀
        long finalLsb = (leastSigBits & ~0xFFFFL) | ((long) uuidOffset << 8) | slotSuffix;

        return new UUID(mostSigBits, finalLsb);
    }
}