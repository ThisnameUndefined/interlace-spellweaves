package org.xszb.interlace_spellweaves.spell.ice;

//public class SummonRideablePolarBear  extends AbstractMixSpell {
//    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "summon_polar_bear");
//
//    @Override
//    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
//        return List.of(
//                Component.translatable("ui.irons_spellbooks.hp", getBearHealth(spellLevel, null)),
//                Component.translatable("ui.irons_spellbooks.damage", getBearDamage(spellLevel, null))
//        );
//    }
//
//    private final DefaultConfig defaultConfig = new DefaultConfig()
//            .setMinRarity(SpellRarity.RARE)
//            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
//            .setMaxLevel(10)
//            .setCooldownSeconds(180)
//            .build();
//
//    public SummonRideablePolarBear() {
//        this.manaCostPerLevel = 10;
//        this.baseSpellPower = 4;
//        this.spellPowerPerLevel = 1;
//        this.castTime = 0;
//        this.baseManaCost = 50;
//    }
//
//    @Override
//    public CastType getCastType() {
//        return CastType.INSTANT;
//    }
//
//    @Override
//    public DefaultConfig getDefaultConfig() {
//        return defaultConfig;
//    }
//
//    @Override
//    public ResourceLocation getSpellResource() {
//        return spellId;
//    }
//
//    @Override
//    public Optional<SoundEvent> getCastStartSound() {
//        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
//    }
//
//    @Override
//    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
//        int summonTime = 20 * 60 * 10;
//
//        RideablePolarBear polarBear = new RideablePolarBear(world, entity);
//        polarBear.setPos(entity.position());
//
//        polarBear.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(getBearDamage(spellLevel, entity));
//        polarBear.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getBearHealth(spellLevel, entity));
//        setAttributes(polarBear,getSpellPower(spellLevel, entity));
//        polarBear.setHealth(polarBear.getMaxHealth());
//        world.addFreshEntity(polarBear);
//        if (entity instanceof Player player && polarBear.isAddedToWorld()) {
//            polarBear.doPlayerRide(player);
//        }
//
//        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
//    }
//
//    private float getBearHealth(int spellLevel, LivingEntity caster) {
//        return 20 + spellLevel * 4;
//    }
//
//    private float getBearDamage(int spellLevel, LivingEntity caster) {
//        return getSpellPower(spellLevel, caster);
//    }
//
//    private void setAttributes(RideablePolarBear bear, float power) {
//        int maxPower = baseSpellPower + (ServerConfigs.getSpellConfig(this).maxLevel() - 1) * spellPowerPerLevel;
//        float quality = power / (float) maxPower;
//        float minSpeed = .2f;
//        float maxSpeed = .45f;
//        bear.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(quality, minSpeed, maxSpeed));
//    }
//
//}
