package org.xszb.interlace_spellweaves.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;

public class ModTags {
    //这种派系并不存在
    public static final TagKey<Item> FUSION_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "gunmu_focus"));


    private static TagKey<DamageType> create(String tag) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, tag));
    }

}
