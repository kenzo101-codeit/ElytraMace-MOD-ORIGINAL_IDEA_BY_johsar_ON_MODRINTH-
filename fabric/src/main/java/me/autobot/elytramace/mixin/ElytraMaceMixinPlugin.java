package me.autobot.elytramace.mixin;

import net.fabricmc.loader.api.VersionParsingException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import java.util.List;
import java.util.Set;

public class ElytraMaceMixinPlugin implements IMixinConfigPlugin {

    private static boolean HAS_FALL_DISTANCE;

    @Override
    public void onLoad(String mixinPackage) {
        Version mc = FabricLoader.getInstance().getModContainer("minecraft")
                .orElseThrow()
                .getMetadata()
                .getVersion();

        // 1.21.5+ logic
        try {
            HAS_FALL_DISTANCE = mc.compareTo(Version.parse("1.21.5")) >= 0;
        } catch (VersionParsingException e) {
            // Fail SAFE, not hard
            HAS_FALL_DISTANCE = false;
        }
    }
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("_MaceAttack_OLD")) {
            return !HAS_FALL_DISTANCE;
        }

        if (mixinClassName.endsWith("_MaceAttack_NEW")) {
            return HAS_FALL_DISTANCE;
        }

        return true;
    }

    // ---- boilerplate ----
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
