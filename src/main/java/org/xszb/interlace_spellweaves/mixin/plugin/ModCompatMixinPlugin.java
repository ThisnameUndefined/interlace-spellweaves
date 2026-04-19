package org.xszb.interlace_spellweaves.mixin.plugin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ModCompatMixinPlugin implements IMixinConfigPlugin {

    private static final String Apotheosis = "apotheosis";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".compat.apotheosis")) {
            return isModLoaded(Apotheosis);
        }
        return true;
    }

    private boolean isModLoaded(String modid) {
        try {
            return LoadingModList.get().getModFileById(modid) != null;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }


    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}