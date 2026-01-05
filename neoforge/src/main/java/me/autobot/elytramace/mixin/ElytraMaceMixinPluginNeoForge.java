package me.autobot.elytramace.mixin;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ElytraMaceMixinPluginNeoForge implements IMixinConfigPlugin {

    private static boolean HAS_FALL_DISTANCE;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            // NeoForge uses ModList.get() to access the mod list
            IModInfo minecraftMod = ModList.get()
                    .getMods()
                    .stream()
                    .filter(mod -> mod.getModId().equals("minecraft"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Minecraft mod not found"));

            ArtifactVersion mcVersion = minecraftMod.getVersion();
            ArtifactVersion targetVersion = new DefaultArtifactVersion("1.21.5");

            // 1.21.5+ logic
            HAS_FALL_DISTANCE = mcVersion.compareTo(targetVersion) >= 0;
        } catch (Exception e) {
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