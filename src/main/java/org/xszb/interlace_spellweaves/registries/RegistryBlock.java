package org.xszb.interlace_spellweaves.registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.block.altar_of_nameless.AltarOfNamelessBlock;
import org.xszb.interlace_spellweaves.block.altar_of_nameless.AltarOfNamelessEntity;
import org.xszb.interlace_spellweaves.block.spell_forge.SpellForgeBlock;

public class RegistryBlock {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InterlaceSpellWeaves.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, InterlaceSpellWeaves.MODID);


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final RegistryObject<Block> SPELL_FORGE_BLOCK = BLOCKS.register("spell_forge", SpellForgeBlock::new);

    public static final RegistryObject<Block> ALTAR_OF_NAMELESE = BLOCKS.register("altar_of_nameless", AltarOfNamelessBlock::new);


    public static final RegistryObject<BlockEntityType<AltarOfNamelessEntity>> ALTAR_OF_NAMELESE_TILE = BLOCK_ENTITIES.register("altar_of_nameless", () -> BlockEntityType.Builder.of(AltarOfNamelessEntity::new, ALTAR_OF_NAMELESE.get()).build(null));
}
