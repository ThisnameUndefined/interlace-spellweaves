package org.xszb.interlace_spellweaves.util.reflect;

import java.util.HashMap;
import java.util.Map;

public final class ObfuscationMapping {

    public static final String VERSION = "1.20.1";

    private static final Map<String, Map<String, String>> FIELD_MAPPINGS = new HashMap<>();

    static {
        initFieldMappings();
    }

    private static void initFieldMappings() {
        Map<String, String> fields = new HashMap<>();

        // Entity
        fields.put("Entity.entityData", "f_19804_");
        fields.put("Entity.position", "f_19825_");
        fields.put("Entity.xOld", "f_19790_");
        fields.put("Entity.yOld", "f_19791_");
        fields.put("Entity.zOld", "f_19792_");
        fields.put("Entity.bb", "f_19828_");
        fields.put("Entity.removalReason", "f_146795_");

        // LivingEntity
        fields.put("LivingEntity.hurtTime", "f_20915_");
        fields.put("LivingEntity.deathTime", "f_20919_");
        fields.put("LivingEntity.dead", "f_20890_");

        // SynchedEntityData
        fields.put("SynchedEntityData.itemsById", "f_135345_");
        fields.put("SynchedEntityData.isDirty", "f_135348_");

        // SynchedEntityData.DataItem
        fields.put("DataItem.value", "f_135391_");
        fields.put("DataItem.dirty", "f_135392_");

        // ServerLevel
        fields.put("ServerLevel.players", "f_8546_");
        fields.put("ServerLevel.chunkSource", "f_8547_");
        fields.put("ServerLevel.entityTickList", "f_143243_");
        fields.put("ServerLevel.entityManager", "f_143244_");
        fields.put("ServerLevel.navigatingMobs", "f_143246_");

        // EntityTickList
        fields.put("EntityTickList.active", "f_156903_");
        fields.put("EntityTickList.passive", "f_156904_");
        fields.put("EntityTickList.iterated", "f_156905_");

        // ServerChunkCache
        fields.put("ServerChunkCache.chunkMap", "f_8325_");

        // ChunkMap
        fields.put("ChunkMap.entityMap", "f_140150_");

        // PersistentEntitySectionManager
        fields.put("PersistentEntitySectionManager.visibleEntityStorage", "f_157494_");
        fields.put("PersistentEntitySectionManager.knownUuids", "f_157491_");
        fields.put("PersistentEntitySectionManager.sectionStorage", "f_157495_");

        // EntityLookup
        fields.put("EntityLookup.byUuid", "f_156808_");
        fields.put("EntityLookup.byId", "f_156807_");

        // EntitySectionStorage
        fields.put("EntitySectionStorage.sections", "f_156852_");

        // EntitySection
        fields.put("EntitySection.storage", "f_156827_");

        // ClassInstanceMultiMap
        fields.put("ClassInstanceMultiMap.byClass", "f_13527_");

        // ClientLevel
        fields.put("ClientLevel.tickingEntities", "f_171630_");
        fields.put("ClientLevel.entityStorage", "f_171631_");
        fields.put("ClientLevel.players", "f_104566_");

        // TransientEntitySectionManager
        fields.put("TransientEntitySectionManager.entityStorage", "f_157637_");
        fields.put("TransientEntitySectionManager.sectionStorage", "f_157638_");

        FIELD_MAPPINGS.put(VERSION, fields);
    }


    public static String getFieldMapping(String fieldKey) {
        Map<String, String> mappings = FIELD_MAPPINGS.get(VERSION);
        return mappings.get(fieldKey);
    }



    private ObfuscationMapping() {}
}
