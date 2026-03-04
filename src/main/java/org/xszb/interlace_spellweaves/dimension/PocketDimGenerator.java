package org.xszb.interlace_spellweaves.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class PocketDimGenerator {
    public static final String POCKET_DIM = ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "pocket_dimension").toString();

    public static final ResourceKey<Level> POCKET_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(POCKET_DIM));

    public static void generateMainIsland(ServerLevel level) {

        BlockPos center = new BlockPos(-32, 8, -38);

        placeNBTStructure(level, ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "evockation_island"), center.above(),Mirror.NONE);
    }

    private static void placeNBTStructure(ServerLevel level, ResourceLocation structureRes, BlockPos pos,Mirror mirror) {
        level.getStructureManager().get(structureRes).ifPresent(template -> {
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setMirror(mirror)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);
            template.placeInWorld(level, pos, pos, settings, level.random, 2);
        });
    }
}
