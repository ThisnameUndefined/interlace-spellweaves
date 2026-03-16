package org.xszb.interlace_spellweaves.setup;

import io.redspace.ironsspellbooks.entity.spells.creeper_head.CreeperHeadRenderer;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.api.magic.SyncedEffectData;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsRenderer;
import org.xszb.interlace_spellweaves.entity.mobs.frostbone.FrostboneRenderer;
import org.xszb.interlace_spellweaves.entity.mobs.polorbear.RideablePolarBearRenderer;
import org.xszb.interlace_spellweaves.entity.spells.evocation_strike.EvocationBurstRenderer;
import org.xszb.interlace_spellweaves.entity.spells.firework_warning.FireworkWarnRenderer;
import org.xszb.interlace_spellweaves.entity.spells.gust.DamageGustRenderer;
import org.xszb.interlace_spellweaves.entity.spells.ice_strike.IceStrikeRenderer;
import org.xszb.interlace_spellweaves.entity.spells.rite_entity.TotemRiteRenderer;
import org.xszb.interlace_spellweaves.entity.spells.rushvex.RushVexRenderer;
import org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow.noGravityMagicArrowRenderer;
import org.xszb.interlace_spellweaves.entity.spells.stake.StakeRenderer;
import org.xszb.interlace_spellweaves.entity.spells.tracking_arrow.TrackMagicArrowRenderer;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;
import org.xszb.interlace_spellweaves.registries.RegistryEntity;
import org.xszb.interlace_spellweaves.registries.RegistryItem;
import org.xszb.interlace_spellweaves.render.EnergySwirlLayer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsRenderer.SHIELD_TEXTURE;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {


        event.registerEntityRenderer(RegistryEntity.ICE_SLASH.get(), IceStrikeRenderer::new);
        event.registerEntityRenderer(RegistryEntity.ICE_BURST.get(), NoopRenderer::new);
        event.registerEntityRenderer(RegistryEntity.STAKE_ENTITY.get(), StakeRenderer::new);
        event.registerEntityRenderer(RegistryEntity.SMALL_MAGIC_ARROW.get(), noGravityMagicArrowRenderer::new);
        event.registerEntityRenderer(RegistryEntity.TOTEM_RITE.get(), TotemRiteRenderer::new);
        event.registerEntityRenderer(RegistryEntity.FIRE_FLYS.get(), NoopRenderer::new);
        event.registerEntityRenderer(RegistryEntity.VEX.get(), RushVexRenderer::new);
        event.registerEntityRenderer(RegistryEntity.GUST_COLLIDER.get(), DamageGustRenderer::new);
        event.registerEntityRenderer(RegistryEntity.FIREWORK_BURST.get(), FireworkWarnRenderer::new);
        event.registerEntityRenderer(RegistryEntity.EVOCATION_BURST.get(), EvocationBurstRenderer::new);
        event.registerEntityRenderer(RegistryEntity.CREEPER_HEAD_PROJECTILE.get(), CreeperHeadRenderer::new);
        event.registerEntityRenderer(RegistryEntity.SUMMON_NAMELESS.get(), NoopRenderer::new);
        event.registerEntityRenderer(RegistryEntity.TRACK_MAGIC_ARROW.get(), TrackMagicArrowRenderer::new);



        event.registerEntityRenderer(RegistryEntity.FROSTBONE.get(), FrostboneRenderer::new);
        event.registerEntityRenderer(RegistryEntity.SUMMONED_POLAR_BEAR.get(), RideablePolarBearRenderer::new);

        event.registerEntityRenderer(RegistryEntity.NAMELESS.get(), NamelessWizardsRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(StakeRenderer.MODEL_LAYER_LOCATION, StakeRenderer::createBodyLayer);
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RegistryBlock.SPELL_FORGE_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RegistryBlock.ALTAR_OF_NAMELESE.get(), RenderType.cutout());

            RegistryItem.getItems().stream().filter(item -> item.get() instanceof SpellBook).forEach((item) -> CuriosRendererRegistry.register(item.get(), SpellBookCurioRenderer::new));
        });
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.AddLayers event) {
        addLayerToPlayerSkin(event, "default");
        addLayerToPlayerSkin(event, "slim");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName) {
        EntityRenderer<? extends Player> render = event.getSkin(skinName);
        if (render instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new EnergySwirlLayer.Vanilla(livingRenderer, SHIELD_TEXTURE, SyncedEffectData.NAMELESS_SET_SHIELD));
        }
    }



}
