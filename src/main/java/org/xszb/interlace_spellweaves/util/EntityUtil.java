package org.xszb.interlace_spellweaves.util;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.xszb.interlace_spellweaves.mixin.LivingEntityAccessor;
import org.xszb.interlace_spellweaves.network.client_remove.ClientRemovePacket;
import org.xszb.interlace_spellweaves.network.NetworkHandler;
import org.xszb.interlace_spellweaves.util.reflect.ObfuscationMapping;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {
    public static <T extends ParticleOptions> void particleLine(Vec3 start, Vec3 end, ServerLevel serverLevel, int steps, T particleTypes) {
        Vec3 diff = end.subtract(start);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 pos = start.add(diff.scale(t));
            serverLevel.sendParticles(
                    particleTypes,
                    pos.x, pos.y, pos.z,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static <T extends ParticleOptions> void clientParticleLine(Vec3 start, Vec3 end, Level level, int steps, T particleOptions) {
        Vec3 diff = end.subtract(start);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 pos = start.add(diff.scale(t));

            level.addParticle(
                    particleOptions,
                    pos.x, pos.y, pos.z,
                    0, 0, 0
            );
        }
    }

    //非常规区域

    //无来无去，不悔不怨
    static EntityDataAccessor<Float> HEALTH_KEY = LivingEntityAccessor.HEALTH();
    private static final Map<Class<?>, List<EntityDataAccessor<?>>> NEAR_ACCESSOR_CACHE = new ConcurrentHashMap<>();

    private static final Set<String> HEALTH_BLACKLIST_KEYWORDS = ImmutableSet.of(
            "ai", "goal", "target", "brain", "memory", "sensor", "skill", "ability", "spell", "cast",
            "animation", "swing", "cooldown", "duration", "delay", "timer", "tick", "time",
            "age", "lifetime", "deathtime", "hurttime", "invulnerabletime", "hurt"
    );


    public static void setHealth(LivingEntity entity, float hp) {
        entity.getEntityData().set(HEALTH_KEY, hp);
        if (entity instanceof Player ) return;
        setHealthAccessors(entity, hp);
    }

    private static void setHealthAccessors(LivingEntity entity, float hp) {
        try {
            List<EntityDataAccessor<?>> nearbyAccessors = FindNumAccessors(entity);
            for (EntityDataAccessor<?> acc : nearbyAccessors) {
                setAccessorValue(entity, acc, hp);
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private static void setAccessorValue(LivingEntity entity, EntityDataAccessor<?> accessor, float expectedHealth) {
        try {
            EntityDataSerializer<?> serializer = accessor.getSerializer();
            SynchedEntityData entityData = entity.getEntityData();
            boolean success = false;
            if (serializer == EntityDataSerializers.FLOAT) {
                entityData.set((EntityDataAccessor<Float>) accessor, expectedHealth);
                success = true;
            } else if (serializer == EntityDataSerializers.INT) {
                entityData.set((EntityDataAccessor<Integer>) accessor, (int) expectedHealth);
                success = true;
            }
            if (success) {
                entity.onSyncedDataUpdated(accessor);
            }
        } catch (Exception ignored) {
        }
    }


    private static List<EntityDataAccessor<?>> FindNumAccessors(LivingEntity entity) {
        Class<?> entityClass = entity.getClass();
        List<EntityDataAccessor<?>> cached = NEAR_ACCESSOR_CACHE.get(entityClass);
        if (cached != null) {
            return cached;
        }

        List<EntityDataAccessor<?>> result = new ArrayList<>();
        float entityHealth = entity.getHealth();

        for (Class<?> clazz = entityClass; clazz != null && clazz != Entity.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (matchesHealthBlacklist(field.getName())) continue;
                    if (!EntityDataAccessor.class.isAssignableFrom(field.getType())) continue;
                    if (!Modifier.isStatic(field.getModifiers())) continue;
                    EntityDataAccessor<?> accessor = (EntityDataAccessor<?>) field.get(null);
                    if (accessor.getId() == HEALTH_KEY.getId()) continue;
                    Object value = entity.getEntityData().get(accessor);
                    if (value instanceof Number) {
                        float numericValue = ((Number) value).floatValue();
                        float diff = Math.abs(numericValue - entityHealth);
                        if (diff <= 4.0f) {
                            result.add(accessor);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        NEAR_ACCESSOR_CACHE.put(entityClass, result);
        return result;
    }

    private static boolean matchesHealthBlacklist(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return false;
        String lowerName = fieldName.toLowerCase();
        for (String keyword : HEALTH_BLACKLIST_KEYWORDS) {
            if (lowerName.contains(keyword)) return true;
        }
        return false;
    }

    //还不能清除
    public static boolean canDiscard(Entity ent) {
        MinecraftServer server = ent.getServer();
        if (server == null || !server.isRunning() || server.getPlayerCount() <= 0 ) {
            return true;
        }
        Player nearestPlayer = ent.level().getNearestPlayer(ent, 64.0);
        if (nearestPlayer == null) {
            return true;
        }
        if (nearestPlayer instanceof ServerPlayer sp) {
            return sp.hasDisconnected();
        }
        return false;
    }
    //战吼,将一个实体变成棍木
    private static VarHandle SERVER_LEVEL_CHUNK_SOURCE_HANDLE;
    private static VarHandle SERVER_LEVEL_ENTITY_TICK_LIST_HANDLE;
    private static VarHandle SERVER_LEVEL_ENTITY_MANAGER_HANDLE;
    private static VarHandle SERVER_LEVEL_NAVIGATING_MOBS_HANDLE;
    private static VarHandle ENTITY_TICK_LIST_ACTIVE_HANDLE;
    private static VarHandle ENTITY_TICK_LIST_PASSIVE_HANDLE;
    private static VarHandle ENTITY_TICK_LIST_ITERATED_HANDLE;
    private static VarHandle SERVER_CHUNK_CACHE_CHUNK_MAP_HANDLE;
    private static VarHandle CHUNK_MAP_ENTITY_MAP_HANDLE;
    private static VarHandle PERSISTENT_ENTITY_MANAGER_VISIBLE_STORAGE_HANDLE;
    private static VarHandle PERSISTENT_ENTITY_MANAGER_KNOWN_UUIDS_HANDLE;
    private static VarHandle PERSISTENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE;
    private static VarHandle ENTITY_LOOKUP_BY_UUID_HANDLE;
    private static VarHandle ENTITY_LOOKUP_BY_ID_HANDLE;
    private static VarHandle ENTITY_SECTION_STORAGE_SECTIONS_HANDLE;
    private static VarHandle ENTITY_SECTION_STORAGE_HANDLE;
    private static VarHandle CLASS_INSTANCE_MULTI_MAP_BY_CLASS_HANDLE;

    private static volatile boolean clientVarHandlesInitialized = false;
    private static VarHandle CLIENT_LEVEL_TICKING_ENTITIES_HANDLE;
    private static VarHandle CLIENT_LEVEL_ENTITY_STORAGE_HANDLE;
    private static VarHandle TRANSIENT_ENTITY_MANAGER_ENTITY_STORAGE_HANDLE;
    private static VarHandle TRANSIENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE;

    static {
        try {
            initServerRemovalVarHandles();
        } catch (Exception e) {

        }
    }

    private static void initServerRemovalVarHandles() throws Exception {
        Field serverLevelChunkSourceField = ObfuscationReflectionHelper.findField(ServerLevel.class, ObfuscationMapping.getFieldMapping("ServerLevel.chunkSource"));
        Field serverLevelEntityTickListField = ObfuscationReflectionHelper.findField(ServerLevel.class, ObfuscationMapping.getFieldMapping("ServerLevel.entityTickList"));
        Field serverLevelEntityManagerField = ObfuscationReflectionHelper.findField(ServerLevel.class, ObfuscationMapping.getFieldMapping("ServerLevel.entityManager"));
        Field serverLevelNavigatingMobsField = ObfuscationReflectionHelper.findField(ServerLevel.class, ObfuscationMapping.getFieldMapping("ServerLevel.navigatingMobs"));
        MethodHandles.Lookup serverLevelLookup = MethodHandles.privateLookupIn(ServerLevel.class, MethodHandles.lookup());
        SERVER_LEVEL_CHUNK_SOURCE_HANDLE = serverLevelLookup.unreflectVarHandle(serverLevelChunkSourceField);
        SERVER_LEVEL_ENTITY_TICK_LIST_HANDLE = serverLevelLookup.unreflectVarHandle(serverLevelEntityTickListField);
        SERVER_LEVEL_ENTITY_MANAGER_HANDLE = serverLevelLookup.unreflectVarHandle(serverLevelEntityManagerField);
        SERVER_LEVEL_NAVIGATING_MOBS_HANDLE = serverLevelLookup.unreflectVarHandle(serverLevelNavigatingMobsField);
        Field entityTickListActiveField = ObfuscationReflectionHelper.findField(EntityTickList.class, ObfuscationMapping.getFieldMapping("EntityTickList.active"));
        Field entityTickListPassiveField = ObfuscationReflectionHelper.findField(EntityTickList.class, ObfuscationMapping.getFieldMapping("EntityTickList.passive"));
        Field entityTickListIteratedField = ObfuscationReflectionHelper.findField(EntityTickList.class, ObfuscationMapping.getFieldMapping("EntityTickList.iterated"));
        MethodHandles.Lookup entityTickListLookup = MethodHandles.privateLookupIn(EntityTickList.class, MethodHandles.lookup());
        ENTITY_TICK_LIST_ACTIVE_HANDLE = entityTickListLookup.unreflectVarHandle(entityTickListActiveField);
        ENTITY_TICK_LIST_PASSIVE_HANDLE = entityTickListLookup.unreflectVarHandle(entityTickListPassiveField);
        ENTITY_TICK_LIST_ITERATED_HANDLE = entityTickListLookup.unreflectVarHandle(entityTickListIteratedField);
        Field serverChunkCacheChunkMapField = ObfuscationReflectionHelper.findField(ServerChunkCache.class, ObfuscationMapping.getFieldMapping("ServerChunkCache.chunkMap"));
        MethodHandles.Lookup serverChunkCacheLookup = MethodHandles.privateLookupIn(ServerChunkCache.class, MethodHandles.lookup());
        SERVER_CHUNK_CACHE_CHUNK_MAP_HANDLE = serverChunkCacheLookup.unreflectVarHandle(serverChunkCacheChunkMapField);
        Field chunkMapEntityMapField = ObfuscationReflectionHelper.findField(ChunkMap.class, ObfuscationMapping.getFieldMapping("ChunkMap.entityMap"));
        MethodHandles.Lookup chunkMapLookup = MethodHandles.privateLookupIn(ChunkMap.class, MethodHandles.lookup());
        CHUNK_MAP_ENTITY_MAP_HANDLE = chunkMapLookup.unreflectVarHandle(chunkMapEntityMapField);
        Field persistentEntityManagerVisibleStorageField = ObfuscationReflectionHelper.findField(PersistentEntitySectionManager.class, ObfuscationMapping.getFieldMapping("PersistentEntitySectionManager.visibleEntityStorage"));
        Field persistentEntityManagerKnownUuidsField = ObfuscationReflectionHelper.findField(PersistentEntitySectionManager.class, ObfuscationMapping.getFieldMapping("PersistentEntitySectionManager.knownUuids"));
        Field persistentEntityManagerSectionStorageField = ObfuscationReflectionHelper.findField(PersistentEntitySectionManager.class, ObfuscationMapping.getFieldMapping("PersistentEntitySectionManager.sectionStorage"));
        MethodHandles.Lookup persistentEntityManagerLookup = MethodHandles.privateLookupIn(PersistentEntitySectionManager.class, MethodHandles.lookup());
        PERSISTENT_ENTITY_MANAGER_VISIBLE_STORAGE_HANDLE = persistentEntityManagerLookup.unreflectVarHandle(persistentEntityManagerVisibleStorageField);
        PERSISTENT_ENTITY_MANAGER_KNOWN_UUIDS_HANDLE = persistentEntityManagerLookup.unreflectVarHandle(persistentEntityManagerKnownUuidsField);
        PERSISTENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE = persistentEntityManagerLookup.unreflectVarHandle(persistentEntityManagerSectionStorageField);
        Field entityLookupByUuidField = ObfuscationReflectionHelper.findField(EntityLookup.class, ObfuscationMapping.getFieldMapping("EntityLookup.byUuid"));
        Field entityLookupByIdField = ObfuscationReflectionHelper.findField(EntityLookup.class, ObfuscationMapping.getFieldMapping("EntityLookup.byId"));
        MethodHandles.Lookup entityLookupLookup = MethodHandles.privateLookupIn(EntityLookup.class, MethodHandles.lookup());
        ENTITY_LOOKUP_BY_UUID_HANDLE = entityLookupLookup.unreflectVarHandle(entityLookupByUuidField);
        ENTITY_LOOKUP_BY_ID_HANDLE = entityLookupLookup.unreflectVarHandle(entityLookupByIdField);
        Field entitySectionStorageSectionsField = ObfuscationReflectionHelper.findField(EntitySectionStorage.class, ObfuscationMapping.getFieldMapping("EntitySectionStorage.sections"));
        MethodHandles.Lookup entitySectionStorageLookup = MethodHandles.privateLookupIn(EntitySectionStorage.class, MethodHandles.lookup());
        ENTITY_SECTION_STORAGE_SECTIONS_HANDLE = entitySectionStorageLookup.unreflectVarHandle(entitySectionStorageSectionsField);
        Field entitySectionStorageField = ObfuscationReflectionHelper.findField(EntitySection.class, ObfuscationMapping.getFieldMapping("EntitySection.storage"));
        MethodHandles.Lookup entitySectionLookup = MethodHandles.privateLookupIn(EntitySection.class, MethodHandles.lookup());
        ENTITY_SECTION_STORAGE_HANDLE = entitySectionLookup.unreflectVarHandle(entitySectionStorageField);
        Field classInstanceMultiMapByClassField = ObfuscationReflectionHelper.findField(ClassInstanceMultiMap.class, ObfuscationMapping.getFieldMapping("ClassInstanceMultiMap.byClass"));
        MethodHandles.Lookup classInstanceMultiMapLookup = MethodHandles.privateLookupIn(ClassInstanceMultiMap.class, MethodHandles.lookup());
        CLASS_INSTANCE_MULTI_MAP_BY_CLASS_HANDLE = classInstanceMultiMapLookup.unreflectVarHandle(classInstanceMultiMapByClassField);
    }

    private static void initClientRemovalVarHandles() {
        if (clientVarHandlesInitialized) return;
        synchronized (EntityUtil.class) {
            if (clientVarHandlesInitialized) return;
            try {
                Field clientLevelTickingEntitiesField = ObfuscationReflectionHelper.findField(ClientLevel.class, ObfuscationMapping.getFieldMapping("ClientLevel.tickingEntities"));
                Field clientLevelEntityStorageField = ObfuscationReflectionHelper.findField(ClientLevel.class, ObfuscationMapping.getFieldMapping("ClientLevel.entityStorage"));
                MethodHandles.Lookup clientLevelLookup = MethodHandles.privateLookupIn(ClientLevel.class, MethodHandles.lookup());
                CLIENT_LEVEL_TICKING_ENTITIES_HANDLE = clientLevelLookup.unreflectVarHandle(clientLevelTickingEntitiesField);
                CLIENT_LEVEL_ENTITY_STORAGE_HANDLE = clientLevelLookup.unreflectVarHandle(clientLevelEntityStorageField);
                Field transientEntityManagerEntityStorageField = ObfuscationReflectionHelper.findField(TransientEntitySectionManager.class, ObfuscationMapping.getFieldMapping("TransientEntitySectionManager.entityStorage"));
                Field transientEntityManagerSectionStorageField = ObfuscationReflectionHelper.findField(TransientEntitySectionManager.class, ObfuscationMapping.getFieldMapping("TransientEntitySectionManager.sectionStorage"));
                MethodHandles.Lookup transientEntityManagerLookup = MethodHandles.privateLookupIn(TransientEntitySectionManager.class, MethodHandles.lookup());
                TRANSIENT_ENTITY_MANAGER_ENTITY_STORAGE_HANDLE = transientEntityManagerLookup.unreflectVarHandle(transientEntityManagerEntityStorageField);
                TRANSIENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE = transientEntityManagerLookup.unreflectVarHandle(transientEntityManagerSectionStorageField);
                clientVarHandlesInitialized = true;
            } catch (Exception e) {
            }
        }
    }

    public static void forceRemoveEntity(Level level, Entity entity) {
        if (level == null || entity == null) return;

        int entityId = entity.getId();
        UUID entityUUID = entity.getUUID();

        if (level instanceof ServerLevel serverLevel) {
            NetworkHandler.sendToTrackingClients(
                    new ClientRemovePacket(entity.getId()),
                    entity
            );
            removeChunkMapEntityMap(serverLevel, entityId);
            removeNavigatingMobs(serverLevel, entity);
            removeEntityLookup(serverLevel, entityId, entityUUID);
            removeKnownUuids(serverLevel, entityUUID);
            removeEntityTickList(serverLevel, entityId);
            removeEntitySectionStorage(serverLevel, entity);
        }
        entity.setRemoved(Entity.RemovalReason.KILLED);
    }

    @SuppressWarnings("unchecked")
    private static void removeEntityTickList(ServerLevel serverLevel, int entityId) {
        try {
            if (SERVER_LEVEL_ENTITY_TICK_LIST_HANDLE == null) return;

            Object entityTickList = SERVER_LEVEL_ENTITY_TICK_LIST_HANDLE.get(serverLevel);
            if (entityTickList == null) return;

            //获取 active、passive、iterated 字段
            Int2ObjectLinkedOpenHashMap<Entity> active = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_TICK_LIST_ACTIVE_HANDLE.get(entityTickList);
            Int2ObjectLinkedOpenHashMap<Entity> passive = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_TICK_LIST_PASSIVE_HANDLE.get(entityTickList);

            if (active == null || passive == null) return;

            Object iterated = ENTITY_TICK_LIST_ITERATED_HANDLE.get(entityTickList);

            if (iterated == active) {
                passive.clear();

                for (Int2ObjectMap.Entry<Entity> entry : active.int2ObjectEntrySet()) {
                    int id = entry.getIntKey();
                    if (id != entityId) {
                        passive.put(id, entry.getValue());
                    }
                }

                //交换 active 和 passive
                ENTITY_TICK_LIST_ACTIVE_HANDLE.set(entityTickList, passive);
                ENTITY_TICK_LIST_PASSIVE_HANDLE.set(entityTickList, active);
            } else {
                //未在迭代，直接删除
                active.remove(entityId);
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private static void removeEntityLookup(ServerLevel serverLevel, int entityId, UUID entityUUID) {
        try {
            if (SERVER_LEVEL_ENTITY_MANAGER_HANDLE == null) return;
            Object entityManager = SERVER_LEVEL_ENTITY_MANAGER_HANDLE.get(serverLevel);
            if (entityManager == null) return;
            Object visibleEntityStorage = PERSISTENT_ENTITY_MANAGER_VISIBLE_STORAGE_HANDLE.get(entityManager);
            if (visibleEntityStorage == null) return;
            Map<UUID, Entity> byUuid = (Map<UUID, Entity>) ENTITY_LOOKUP_BY_UUID_HANDLE.get(visibleEntityStorage);
            if (byUuid != null) {
                byUuid.remove(entityUUID);
            }
            Int2ObjectLinkedOpenHashMap<Entity> byId = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_LOOKUP_BY_ID_HANDLE.get(visibleEntityStorage);
            if (byId != null) {
                byId.remove(entityId);
            }
        } catch (Exception ignored) {}
    }
    @SuppressWarnings("unchecked")
    private static void removeEntitySectionStorage(ServerLevel serverLevel, Entity entity) {
        try {
            if (SERVER_LEVEL_ENTITY_MANAGER_HANDLE == null) return;
            Object entityManager = SERVER_LEVEL_ENTITY_MANAGER_HANDLE.get(serverLevel);
            if (entityManager == null) return;
            Object sectionStorage = PERSISTENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE.get(entityManager);
            if (sectionStorage == null) return;
            Long2ObjectMap<?> sections = (Long2ObjectMap<?>) ENTITY_SECTION_STORAGE_SECTIONS_HANDLE.get(sectionStorage);
            if (sections == null) return;
            for (Object section : sections.values()) {
                if (section == null) continue;
                Object storage = ENTITY_SECTION_STORAGE_HANDLE.get(section);
                if (storage == null) continue;
                Map<Class<?>, List<?>> byClass = (Map<Class<?>, List<?>>) CLASS_INSTANCE_MULTI_MAP_BY_CLASS_HANDLE.get(storage);
                if (byClass == null) continue;
                for (Map.Entry<Class<?>, List<?>> entry : byClass.entrySet()) {
                    Class<?> clazz = entry.getKey();
                    List<?> list = entry.getValue();

                    if (clazz.isInstance(entity)) {
                        list.remove(entity);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static void removeChunkMapEntityMap(ServerLevel serverLevel, int entityId) {
        try {
            if (SERVER_LEVEL_CHUNK_SOURCE_HANDLE == null) return;

            Object chunkSource = SERVER_LEVEL_CHUNK_SOURCE_HANDLE.get(serverLevel);
            if (chunkSource == null) return;

            Object chunkMap = SERVER_CHUNK_CACHE_CHUNK_MAP_HANDLE.get(chunkSource);
            if (chunkMap == null) return;

            Int2ObjectOpenHashMap<?> entityMap = (Int2ObjectOpenHashMap<?>) CHUNK_MAP_ENTITY_MAP_HANDLE.get(chunkMap);
            if (entityMap != null) {
                entityMap.remove(entityId);
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private static void removeKnownUuids(ServerLevel serverLevel, UUID entityUUID) {
        try {
            if (SERVER_LEVEL_ENTITY_MANAGER_HANDLE == null) return;

            Object entityManager = SERVER_LEVEL_ENTITY_MANAGER_HANDLE.get(serverLevel);
            if (entityManager == null) return;

            Set<UUID> knownUuids = (Set<UUID>) PERSISTENT_ENTITY_MANAGER_KNOWN_UUIDS_HANDLE.get(entityManager);
            if (knownUuids != null) {
                knownUuids.remove(entityUUID);
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private static void removeNavigatingMobs(ServerLevel serverLevel, Entity entity) {
        try {
            if (entity instanceof Mob) {
                ObjectOpenHashSet<Mob> navigatingMobs = (ObjectOpenHashSet<Mob>) SERVER_LEVEL_NAVIGATING_MOBS_HANDLE.get(serverLevel);
                if (navigatingMobs != null) {
                    navigatingMobs.remove(entity);
                }
            }
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    public static void removeClientContainers(ClientLevel clientLevel, Entity entity) {
        initClientRemovalVarHandles();
        int entityId = entity.getId();
        UUID entityUUID = entity.getUUID();
        try {
            Object entityStorage = CLIENT_LEVEL_ENTITY_STORAGE_HANDLE.get(clientLevel);
            if (entityStorage == null) return;
            if (CLIENT_LEVEL_TICKING_ENTITIES_HANDLE != null) {
                Object entityTickList = CLIENT_LEVEL_TICKING_ENTITIES_HANDLE.get(clientLevel);
                if (entityTickList != null) {
                    Int2ObjectLinkedOpenHashMap<Entity> active = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_TICK_LIST_ACTIVE_HANDLE.get(entityTickList);
                    if (active != null) {
                        active.remove(entityId);
                    }
                    Int2ObjectLinkedOpenHashMap<Entity> passive = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_TICK_LIST_PASSIVE_HANDLE.get(entityTickList);
                    if (passive != null) {
                        passive.remove(entityId);
                    }
                }
            }
            if (CLIENT_LEVEL_ENTITY_STORAGE_HANDLE != null) {
                Object entityLookup = TRANSIENT_ENTITY_MANAGER_ENTITY_STORAGE_HANDLE.get(entityStorage);
                if (entityLookup != null) {
                    Map<UUID, Entity> byUuid = (Map<UUID, Entity>) ENTITY_LOOKUP_BY_UUID_HANDLE.get(entityLookup);
                    if (byUuid != null) {
                        byUuid.remove(entityUUID);
                    }

                    Int2ObjectLinkedOpenHashMap<Entity> byId = (Int2ObjectLinkedOpenHashMap<Entity>) ENTITY_LOOKUP_BY_ID_HANDLE.get(entityLookup);
                    if (byId != null) {
                        byId.remove(entityId);
                    }
                }
            }

            if (CLIENT_LEVEL_ENTITY_STORAGE_HANDLE != null) {

                Object sectionStorage = TRANSIENT_ENTITY_MANAGER_SECTION_STORAGE_HANDLE.get(entityStorage);
                if (sectionStorage == null) return;
                Long2ObjectMap<?> sections = (Long2ObjectMap<?>) ENTITY_SECTION_STORAGE_SECTIONS_HANDLE.get(sectionStorage);
                if (sections == null) return;
                for (Object section : sections.values()) {
                    if (section == null) continue;
                    Object storage = ENTITY_SECTION_STORAGE_HANDLE.get(section);
                    if (storage == null) continue;
                    Map<Class<?>, List<?>> byClass = (Map<Class<?>, List<?>>) CLASS_INSTANCE_MULTI_MAP_BY_CLASS_HANDLE.get(storage);
                    if (byClass == null) continue;
                    for (Map.Entry<Class<?>, List<?>> entry : byClass.entrySet()) {
                        Class<?> clazz = entry.getKey();
                        List<?> list = entry.getValue();
                        if (clazz.isInstance(entity)) {
                            list.remove(entity);
                        }
                    }
                }
            }

        } catch (Exception e) {}
    }

}
