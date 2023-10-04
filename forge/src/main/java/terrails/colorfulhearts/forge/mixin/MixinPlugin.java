package terrails.colorfulhearts.forge.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    // add a way to filter classes if more mixins are added later on
    @Override
    public boolean shouldApplyMixin(String targetName, String mixinName) {
        return LoadingModList.get().getModFileById("farmersdelight") != null;
    }

    // unneeded
    @Override
    public void onLoad(String s) { }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) { }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) { }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) { }
}
