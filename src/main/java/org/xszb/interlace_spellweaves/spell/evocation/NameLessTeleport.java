package org.xszb.interlace_spellweaves.spell.evocation;


import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.spells.AbstractMixSpell;
import org.xszb.interlace_spellweaves.entity.spells.rite_entity.TotemRiteEntity;
import org.xszb.interlace_spellweaves.registries.RegistryItem;

import java.util.List;
import java.util.Optional;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.*;

@AutoSpellConfig
public class NameLessTeleport extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "tp");


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.iss_csw.tp" )
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(3)
            .build();

    public NameLessTeleport() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 120;
        this.baseManaCost = 800;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.EVOCATION_CAST.get());
    }


    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (entity instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null) return;
            if (serverPlayer.level().dimension().location().toString().equals(POCKET_DIM)) {
                ResourceKey<Level> respawnDimKey = serverPlayer.getRespawnDimension();
                if (respawnDimKey == null) {
                    respawnDimKey = Level.OVERWORLD;
                }

                ServerLevel targetLevel = server.getLevel(respawnDimKey);
                if (targetLevel == null) targetLevel = server.overworld();
                BlockPos respawnPos = serverPlayer.getRespawnPosition();
                boolean forcedSpawn = serverPlayer.isRespawnForced(); // 是否为重生锚或强制点
                if (respawnPos == null) {
                    respawnPos = targetLevel.getSharedSpawnPos();
                } else {
                    Optional<Vec3> seatPos = Player.findRespawnPositionAndUseSpawnBlock(targetLevel, respawnPos, serverPlayer.getRespawnAngle(), forcedSpawn, true);
                    if (seatPos.isPresent()) {
                        serverPlayer.teleportTo(targetLevel, seatPos.get().x, seatPos.get().y, seatPos.get().z, serverPlayer.getYRot(), serverPlayer.getXRot());
                        return;
                    }
                    respawnPos = targetLevel.getSharedSpawnPos();
                }

                serverPlayer.teleportTo(targetLevel, respawnPos.getX() + 0.5, respawnPos.getY(), respawnPos.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
            } else {
                ServerLevel destinationLevel = server.getLevel(POCKET_DIMENSION);

                if (destinationLevel != null) {

                    serverPlayer.teleportTo(destinationLevel,0,35, 0,
                            serverPlayer.getYRot(), serverPlayer.getXRot());
                }
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }


}