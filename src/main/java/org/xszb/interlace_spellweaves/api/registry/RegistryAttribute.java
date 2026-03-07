package org.xszb.interlace_spellweaves.api.registry;


import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryAttribute {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, InterlaceSpellWeaves.MODID);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static final RegistryObject<Attribute> EX_SPELL_LEVEL = ATTRIBUTES.register("extra_spell_level", () -> (new MagicRangedAttribute("attribute.iss_cws.extra_spell_level", 0.0, 0.0D, 1000000.0D).setSyncable(true)));
    public static final RegistryObject<Attribute> EX_PROTECT_LEVEL = ATTRIBUTES.register("extra_protect_level", () -> (new MagicRangedAttribute("attribute.iss_cws.extra_protect_level", 0.0D, 0.0D, 1000000.0D).setSyncable(true)));
    public static final RegistryObject<Attribute> REDUCE_ARMOR = ATTRIBUTES.register("reduce_armor", () ->
            new RangedAttribute("attribute.iss_cws.reduce_armor", 0.0D, -2048.0D, 2048.0D) {
                @Override
                public double sanitizeValue(double value) {
                    return Mth.clamp(value, -2048.0D, 2048.0D);
                }
            }.setSyncable(true)
    );
    public static final RegistryObject<Attribute> HEAL_MUL = ATTRIBUTES.register("heal_mul", () -> (new MagicRangedAttribute("attribute.iss_cws.heal_mul", 1.0D, 0.0D, 1000000.0D).setSyncable(true)));
    public static final RegistryObject<Attribute> ARROW_MUL = ATTRIBUTES.register("arrow_mul", () -> (new MagicRangedAttribute("attribute.iss_cws.arrow_mul", 1.0D, 0.0D, 1000000.0D).setSyncable(true)));




    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute.get())));
    }
}
