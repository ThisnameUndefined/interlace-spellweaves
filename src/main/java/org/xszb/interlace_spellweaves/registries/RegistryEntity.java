package org.xszb.interlace_spellweaves.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.entity.mobs.frostbone.FrostboneEntity;
import org.xszb.interlace_spellweaves.entity.mobs.polorbear.RideablePolarBear;
import org.xszb.interlace_spellweaves.entity.spells.HailStone;
import org.xszb.interlace_spellweaves.entity.spells.IceBurstEntity;
import org.xszb.interlace_spellweaves.entity.spells.creeper_chain.CreeperChainEntiy;
import org.xszb.interlace_spellweaves.entity.spells.evocation_strike.EvocationBurstEntity;
import org.xszb.interlace_spellweaves.entity.spells.fireflys.FireflysEntity;
import org.xszb.interlace_spellweaves.entity.spells.firework_warning.FireworkWarnEntity;
import org.xszb.interlace_spellweaves.entity.spells.gust.DamageGustCollider;
import org.xszb.interlace_spellweaves.entity.spells.ice_strike.IceStrikeEntity;
import org.xszb.interlace_spellweaves.entity.spells.rite_entity.TotemRiteEntity;
import org.xszb.interlace_spellweaves.entity.spells.rushvex.RushVexEntity;
import org.xszb.interlace_spellweaves.entity.spells.small_magic_arrow.noGravityMagicArrow;
import org.xszb.interlace_spellweaves.entity.spells.stake.StakeEntity;
import org.xszb.interlace_spellweaves.entity.spells.tracking_arrow.TrackMagicArrow;
import org.xszb.interlace_spellweaves.entity.utils.SummonNamelessWizards;

public class RegistryEntity {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, InterlaceSpellWeaves.MODID);

    //大抵是生物实体
    public static final RegistryObject<EntityType<FrostboneEntity>> FROSTBONE =
            ENTITIES.register("frost_remains", () -> EntityType.Builder.of(FrostboneEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "frost_remains").toString()));

    //应该算是boss
    public static final RegistryObject<EntityType<NamelessWizardsEntity>> NAMELESS =
            ENTITIES.register("nameless_wizard", () -> EntityType.Builder.<NamelessWizardsEntity>of(NamelessWizardsEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "nameless_wizard").toString()));


    //法术实体
    public static final RegistryObject<EntityType<IceBurstEntity>> ICE_BURST =
            ENTITIES.register("ice_burst", () -> EntityType.Builder.<IceBurstEntity>of(IceBurstEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "ice_burst").toString()));

    public static final RegistryObject<EntityType<IceStrikeEntity>> ICE_SLASH =
            ENTITIES.register("ice_slash", () -> EntityType.Builder.<IceStrikeEntity>of(IceStrikeEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "ice_slash").toString()));

    public static final RegistryObject<EntityType<HailStone>> HAIL_STONE =
            ENTITIES.register("hail_stone", () -> EntityType.Builder.<HailStone>of(HailStone::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "hail_stone").toString()));

    public static final RegistryObject<EntityType<RideablePolarBear>> SUMMONED_POLAR_BEAR =
            ENTITIES.register("summoned_polar_bear", () -> EntityType.Builder.<RideablePolarBear>of(RideablePolarBear::new, MobCategory.CREATURE)
                    .immuneTo(Blocks.POWDER_SNOW)
                    .sized(1.4F, 1.4F)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "summoned_polar_bear").toString()));

    public static final RegistryObject<EntityType<StakeEntity>> STAKE_ENTITY =
            ENTITIES.register("wood_stake", () -> EntityType.Builder.<StakeEntity>of(StakeEntity::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "wood_stake").toString()));

    public static final RegistryObject<EntityType<noGravityMagicArrow>> SMALL_MAGIC_ARROW =
            ENTITIES.register("small_magic_arrow", () -> EntityType.Builder.<noGravityMagicArrow>of(noGravityMagicArrow::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "small_magic_arrow").toString()));

    public static final RegistryObject<EntityType<TrackMagicArrow>> TRACK_MAGIC_ARROW =
            ENTITIES.register("track_magic_arrow", () -> EntityType.Builder.<TrackMagicArrow>of(TrackMagicArrow::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "track_magic_arrow").toString()));


    public static final RegistryObject<EntityType<TotemRiteEntity>> TOTEM_RITE =
            ENTITIES.register("totem_rite", () -> EntityType.Builder.<TotemRiteEntity>of(TotemRiteEntity::new, MobCategory.MISC)
                    .sized(4f, .8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "totem_rite").toString()));

    public static final RegistryObject<EntityType<FireflysEntity>> FIRE_FLYS =
            ENTITIES.register("fire_flys", () -> EntityType.Builder.<FireflysEntity>of(FireflysEntity::new, MobCategory.MISC)
                    .sized(4f, .4f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "fire_flys").toString()));

    public static final RegistryObject<EntityType<RushVexEntity>> VEX =
            ENTITIES.register("vex", () -> EntityType.Builder.<RushVexEntity>of(RushVexEntity::new, MobCategory.MISC)
                    .sized(.3f, .3f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "vex").toString()));

    public static final RegistryObject<EntityType<DamageGustCollider>> GUST_COLLIDER =
            ENTITIES.register("gust", () -> EntityType.Builder.<DamageGustCollider>of(DamageGustCollider::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "gust").toString()));

    public static final RegistryObject<EntityType<FireworkWarnEntity>> FIREWORK_BURST =
            ENTITIES.register("firework_burst", () -> EntityType.Builder.<FireworkWarnEntity>of(FireworkWarnEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "firework_burst").toString()));

    public static final RegistryObject<EntityType<EvocationBurstEntity>> EVOCATION_BURST =
            ENTITIES.register("evocation_burst", () -> EntityType.Builder.<EvocationBurstEntity>of(EvocationBurstEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "evocation_burst").toString()));

    public static final RegistryObject<EntityType<CreeperChainEntiy>> CREEPER_HEAD_PROJECTILE =
            ENTITIES.register("creeper_chain", () -> EntityType.Builder.<CreeperChainEntiy>of(CreeperChainEntiy::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "creeper_head").toString()));

    //功能性实体
    public static final RegistryObject<EntityType<SummonNamelessWizards>> SUMMON_NAMELESS =
            ENTITIES.register("summon_nameless", () -> EntityType.Builder.<SummonNamelessWizards>of(SummonNamelessWizards::new, MobCategory.MISC)
                    .sized(4f, .8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "summon_nameless").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

}
