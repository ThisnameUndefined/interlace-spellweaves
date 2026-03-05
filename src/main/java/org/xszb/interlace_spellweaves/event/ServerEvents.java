package org.xszb.interlace_spellweaves.event;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.magic.IMagicDataExtension;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.config.MainConfig;
import org.xszb.interlace_spellweaves.dimension.PocketDimGenerator;
import org.xszb.interlace_spellweaves.dimension.PocketDimSavedData;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;
import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIMENSION;
import static org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem.hasFullSet;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onModifySpell(ModifySpellLevelEvent event) {
        LivingEntity ent = event.getEntity();
        MobEffect spellEffect = RegistryEffect.SPELL_EMPOWERMENT.get();
        if (ent != null) {
            if (ent.hasEffect(spellEffect) && !MainConfig.no_spell_empowerment.contains(event.getSpell().getSpellId())){
                event.addLevels(ent.getEffect(spellEffect).getAmplifier() + 1);
            }
            int num = (int) Math.floor(ent.getAttributeValue(RegistryAttribute.EX_SPELL_LEVEL.get()));
            if (num > 0) {
                event.addLevels(num);
            }
        }
    }

    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        LivingEntity ent = event.getEntity();
        MobEffect spellEffect = RegistryEffect.SPELL_EMPOWERMENT.get();
        if (ent != null && ent.hasEffect(spellEffect) && !MainConfig.no_spell_empowerment.contains(event.getSpellId())) {
            ent.removeEffect(spellEffect);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (level.dimension().location().toString().equals(POCKET_DIM)) {
                PocketDimSavedData data = PocketDimSavedData.get(level);
                if (!data.isGenerated()) {
                    level.setChunkForced(0, 0, true);
                    level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, level.getServer());
                    level.setDayTime(6000);
                    PocketDimGenerator.generateMainIsland(level);
                    level.setDefaultSpawnPos(new BlockPos(0, 32, 0), 0);
                    data.setGenerated(true);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onImpactDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean isImpact = event.getSource().is(DamageTypes.FALL) || event.getSource().is(DamageTypes.FLY_INTO_WALL);

            if (isImpact && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof NamelessArmorItem item && item.getType().getSlot() == EquipmentSlot.FEET) {
                float damageAmount = event.getAmount();
                MagicData magicData = MagicData.getPlayerMagicData(player);

                float manaToRecover = damageAmount * 25.0f;
                magicData.setMana(Math.min(magicData.getMana() + manaToRecover, (float)player.getAttributeValue(AttributeRegistry.MAX_MANA.get())));
                Messages.sendToPlayer(new ClientboundSyncMana(magicData), player);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onArmorChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            MagicData magicData = MagicData.getPlayerMagicData(player);
            if (magicData instanceof IMagicDataExtension extension) {
                boolean isFull = hasFullSet(player);
                extension.arcane_nemeses$setWearingFullNamelessSet(isFull);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceKey<Level> currentDim = player.level().dimension();
            if (currentDim.equals(POCKET_DIMENSION)) {
                event.setCanceled(true);

                player.setHealth(player.getMaxHealth());
                player.getFoodData().setFoodLevel(20);
                player.removeAllEffects();
                player.setDeltaMovement(0, 0, 0);

                BlockPos spawnPos = player.getRespawnPosition();
                ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());

                if (spawnPos == null || respawnLevel == null) {
                    respawnLevel = player.server.overworld();
                    spawnPos = respawnLevel.getSharedSpawnPos();
                }

                player.teleportTo(respawnLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());

                player.displayClientMessage(
                        Component.translatable("ui.iss_cws.nameless")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
                        true
                );

            }
        }
    }
}
