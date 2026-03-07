package org.xszb.interlace_spellweaves.spell.evocation;


import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
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

@AutoSpellConfig
public class TotemRite extends AbstractMixSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "totem_rite");


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.duration", Utils.timeFromTicks(getDuration(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(3)
            .build();

    public TotemRite() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.castTime = 20;
        this.baseManaCost = 15;
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (entity instanceof ServerPlayer serverPlayer) {
            Item REQUIRED_ITEM = RegistryItem.TOTEMRITE_ITEM.get();
            if (serverPlayer.getInventory().countItem(REQUIRED_ITEM) > 0){
                return true;
            }else {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.iss_csw.cast_error_totem").withStyle(ChatFormatting.RED)));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 spawn = null;
        if (!world.isClientSide) {
            if (entity instanceof Player player) {
                Item REQUIRED_ITEM = RegistryItem.TOTEMRITE_ITEM.get();
                if (player.getInventory().countItem(REQUIRED_ITEM) > 0) {
                    player.getInventory().items.stream().filter(itemStack -> itemStack.getItem() == REQUIRED_ITEM).findFirst().ifPresent(itemStack -> itemStack.shrink(1));
                }
            }
        }
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
            var target = castTargetingData.getTarget((ServerLevel) world);
            if (target != null)
                spawn = target.position();
        }
        if (spawn == null) {
            spawn = Utils.raycastForEntity(world, entity, 32, true, .15f).getLocation();
            spawn = Utils.moveToRelativeGroundLevel(world, spawn, 6);
        }

        int duration = getDuration(spellLevel, entity);
        float radius = getRadius(spellLevel, entity);


        TotemRiteEntity aoeEntity = new TotemRiteEntity(world,entity instanceof Player);
        aoeEntity.setOwner(entity);
        aoeEntity.setCircular();
        aoeEntity.setRadius(radius);
        aoeEntity.setDuration(duration);
        aoeEntity.setDamage(getDamage(spellLevel, entity));
        aoeEntity.setPos(spawn);
        world.addFreshEntity(aoeEntity);

        TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(world, spawn, radius, 0x95a0a0);
        visualEntity.setDuration(duration);
        visualEntity.setOwner(aoeEntity);
        visualEntity.setShouldFade(true);

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getRadius(int spellLevel, LivingEntity caster) {
        return 5;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return (float) (getSpellPower(spellLevel, caster) * 1.2);
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
        return 30;
    }


    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setIFrames(0);
    }


    private int getDuration(int spellLevel, LivingEntity sourceEntity) {
        return 200;
    }
}