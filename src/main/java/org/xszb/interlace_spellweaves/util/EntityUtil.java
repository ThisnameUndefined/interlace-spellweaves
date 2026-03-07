package org.xszb.interlace_spellweaves.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.xszb.interlace_spellweaves.mixin.LivingEntityAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {
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
            if (sp.hasDisconnected()) {
                return true;
            }
        }
        return false;
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
                        if (diff <= 15.0f) {
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

}
