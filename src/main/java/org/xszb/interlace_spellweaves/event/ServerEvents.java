package org.xszb.interlace_spellweaves.event;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowProjectile;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
import org.xszb.interlace_spellweaves.enchantment.IConflictsEnchantment;
import org.xszb.interlace_spellweaves.item.EnchancedPearlItem;
import org.xszb.interlace_spellweaves.item.ShrivingStone;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;
import org.xszb.interlace_spellweaves.registries.RegistrySpell;
import org.xszb.interlace_spellweaves.spell.evocation.MarkedShot;

import java.util.*;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;
import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIMENSION;
import static org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem.hasFullSet;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    //奥术天赋，启迪着我们
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

    //火焰天赋,强化着我们
    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        LivingEntity ent = event.getEntity();
        MobEffect spellEffect = RegistryEffect.SPELL_EMPOWERMENT.get();
        if (ent != null && ent.hasEffect(spellEffect) && !MainConfig.no_spell_empowerment.contains(event.getSpellId())) {
            ent.removeEffect(spellEffect);
        }
    }

    //硬编码这块
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (level.dimension().location().toString().equals(POCKET_DIM)) {
                PocketDimSavedData data = PocketDimSavedData.get(level);
                if (!data.isGenerated()) {
                    level.setChunkForced(0, 0, true);
                    level.setDayTime(6000);
                    PocketDimGenerator.generateMainIsland(level);
                    level.setDefaultSpawnPos(new BlockPos(0, 32, 0), 0);
                    data.setGenerated(true);
                }
            }
        }
    }


    //受伤这块
    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        //耐摔
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

        //力饮强弓百斤,矢出贯手灼焚
        if (event.getSource().getDirectEntity() instanceof AbstractArrow &&event.getSource().getEntity() instanceof LivingEntity ent) {
            double arrowmul = ent.getAttributeValue(RegistryAttribute.ARROW_MUL.get());
            if (arrowmul != 1){
                event.setAmount((float) (event.getAmount() * arrowmul));
            }
        }
    }

    //套装效果
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

    //肘回去了这块
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

    //附魔这块
    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        EnchantmentHelper.getEnchantments(event.getItemStack()).forEach((enchantment, level) -> {
            if (enchantment instanceof IConflictsEnchantment conflictsEnchantment) {
                conflictsEnchantment.applyAttributeModifiers(event);
            }
        });
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.getItem() instanceof EnchancedPearlItem && right.getItem() instanceof EnchantedBookItem) {
            Map<Enchantment, Integer> enchants1 = EnchantmentHelper.getEnchantments(left);
            Map<Enchantment, Integer> enchants2 = EnchantmentHelper.getEnchantments(right);

            Map<Enchantment, Integer> finalEnchants = new HashMap<>(enchants1);

            enchants2.forEach((enchantment, level) -> {
                if (finalEnchants.containsKey(enchantment)) {
                    int existingLevel = finalEnchants.get(enchantment);
                    if (existingLevel == level) {
                        if (existingLevel < enchantment.getMaxLevel()) {
                            finalEnchants.put(enchantment, existingLevel + 1);
                        }
                    } else {
                        finalEnchants.put(enchantment, Math.max(existingLevel, level));
                    }
                } else {
                    finalEnchants.put(enchantment, level);
                }
            });

            int uniqueCount = finalEnchants.size();
            int enchCost = uniqueCount + left.getCount() + 3;

            ItemStack output = left.copy();
            EnchantmentHelper.setEnchantments(finalEnchants, output);

            event.setCost(enchCost);
            event.setOutput(output);
        }

        if (right.getItem() instanceof ShrivingStone) {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(left);

            if (!enchants.isEmpty()) {
                List<Map.Entry<Enchantment, Integer>> enchantList = new ArrayList<>(enchants.entrySet());

                enchantList.remove(enchantList.size() - 1);

                Map<Enchantment, Integer> finalEnchants = new LinkedHashMap<>();
                for (Map.Entry<Enchantment, Integer> entry : enchantList) {
                    finalEnchants.put(entry.getKey(), entry.getValue());
                }

                ItemStack output = left.copy();

                if (output.hasTag()) {
                    output.getTag().remove("Enchantments");
                    output.getTag().remove("StoredEnchantments");
                }

                if (finalEnchants.isEmpty()) {
                    if (output.is(Items.ENCHANTED_BOOK)) {
                        output = new ItemStack(Items.BOOK); // 强制变回普通书
                    }
                } else {
                    if (output.is(Items.ENCHANTED_BOOK)) {
                        for (Map.Entry<Enchantment, Integer> entry : finalEnchants.entrySet()) {
                            EnchantedBookItem.addEnchantment(output, new EnchantmentInstance(entry.getKey(), entry.getValue()));
                        }
                    } else {
                        EnchantmentHelper.setEnchantments(finalEnchants, output);
                    }
                }

                if (left.hasCustomHoverName()) {
                    output.setHoverName(left.getHoverName());
                }
                int currentCost = left.getBaseRepairCost();
                output.setRepairCost(Math.max(0, (currentCost - 1) / 2));

                event.setOutput(output);
                event.setCost(5);
                event.setMaterialCost(1);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity  entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof Projectile arrow && (arrow instanceof AbstractArrow || arrow instanceof MagicArrowProjectile)  && arrow.getOwner() instanceof LivingEntity shooter) {

            MagicData magicData = MagicData.getPlayerMagicData(shooter);
            if (magicData == null) {
                return;
            }
            var recasts = magicData.getPlayerRecasts();
            var spell = RegistrySpell.MARK_SHOT.get();

            if (recasts.hasRecastForSpell(spell)) {
                var recast = recasts.getRecastInstance(spell.getSpellId());
                if (recast != null && recast.getCastData() instanceof MultiTargetEntityCastData multiTargetData) {
                    List<UUID> targets = multiTargetData.getTargets();
                    if (!targets.isEmpty()) {
                        UUID uuid = targets.get(0);

                        if (uuid != null && arrow.level() instanceof ServerLevel serverLevel) {
                            Entity targetEntity = serverLevel.getEntity(uuid);
                            if (targetEntity instanceof LivingEntity target && target.isAlive()) {
                                if (spell instanceof MarkedShot markedShot) {
                                    double damage = (arrow instanceof AbstractArrow a) ? a.getBaseDamage() : ((MagicArrowProjectile) arrow).getDamage() / 10;
                                    markedShot.trackShoot(target, shooter, recast.getSpellLevel(), damage);
                                }
                            }
                        }
                    }
                }
            }
        }
    }



}
