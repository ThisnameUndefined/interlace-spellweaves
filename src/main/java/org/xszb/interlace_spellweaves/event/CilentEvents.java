package org.xszb.interlace_spellweaves.event;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.magic.IMagicDataExtension;
import org.xszb.interlace_spellweaves.api.registry.RegistryAttribute;
import org.xszb.interlace_spellweaves.config.Config;
import org.xszb.interlace_spellweaves.dimension.PocketDimGenerator;
import org.xszb.interlace_spellweaves.dimension.PocketDimSavedData;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardMusicManager;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;
import org.xszb.interlace_spellweaves.registries.RegistrySound;

import static org.xszb.interlace_spellweaves.dimension.PocketDimGenerator.POCKET_DIM;
import static org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem.hasFullSet;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CilentEvents {


}
