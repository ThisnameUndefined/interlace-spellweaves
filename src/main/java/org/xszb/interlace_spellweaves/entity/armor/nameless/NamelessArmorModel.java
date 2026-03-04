package org.xszb.interlace_spellweaves.entity.armor.nameless;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PumpkinArmorItem;
import net.minecraft.resources.ResourceLocation;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.item.armor.NamelessArmorItem;
import software.bernie.geckolib.model.GeoModel;

public class NamelessArmorModel extends GeoModel<NamelessArmorItem> {

    public NamelessArmorModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(NamelessArmorItem object) {
        return ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "geo/nameless_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NamelessArmorItem object) {
        return ResourceLocation.fromNamespaceAndPath(InterlaceSpellWeaves.MODID, "textures/models/armor/nameless.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NamelessArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}