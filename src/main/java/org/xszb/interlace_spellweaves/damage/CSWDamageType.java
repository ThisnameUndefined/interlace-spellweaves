package org.xszb.interlace_spellweaves.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class CSWDamageType {
    public static final ResourceKey<DamageType> FUSION_MAGIC = register("fusion_magic");

    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", name));
    }
}
