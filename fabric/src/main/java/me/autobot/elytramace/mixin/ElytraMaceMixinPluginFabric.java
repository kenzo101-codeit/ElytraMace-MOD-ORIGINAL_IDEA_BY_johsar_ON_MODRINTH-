package me.autobot.elytramace.mixin;

import net.fabricmc.loader.api.VersionParsingException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import java.util.List;
import java.util.Set;

public class ElytraMaceMixinPluginFabric implements IMixinConfigPlugin {

    private static boolean HAS_FALL_DISTANCE;

    @Override
    public void onLoad(String mixinPackage) {
        System.out.println("[ElytraMace] Mixin plugin loading...");

        try {
            Version mc = FabricLoader.getInstance().getModContainer("minecraft")
                    .orElseThrow(() -> new RuntimeException("Minecraft mod not found"))
                    .getMetadata()
                    .getVersion();

            System.out.println("[ElytraMace] Detected Minecraft version: " + mc.getFriendlyString());

            // 1.21.5+ logic
            Version targetVersion = Version.parse("1.21.5");
            HAS_FALL_DISTANCE = mc.compareTo(targetVersion) >= 0;

            System.out.println("[ElytraMace] Target version: 1.21.5");
            System.out.println("[ElytraMace] Using " + (HAS_FALL_DISTANCE ? "NEW" : "OLD") + " mixin");
            System.out.println("[ElytraMace] HAS_FALL_DISTANCE = " + HAS_FALL_DISTANCE);

        } catch (VersionParsingException e) {
            System.err.println("[ElytraMace] ERROR: Failed to parse version! Defaulting to OLD mixin");
            e.printStackTrace();
            // Fail SAFE, not hard
            HAS_FALL_DISTANCE = false;
        } catch (Exception e) {
            System.err.println("[ElytraMace] ERROR: Unexpected error during version detection! Defaulting to OLD mixin");
            e.printStackTrace();
            HAS_FALL_DISTANCE = false;
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean shouldApply = true;

        if (mixinClassName.endsWith("_MaceAttack_OLD")) {
            shouldApply = !HAS_FALL_DISTANCE;
            System.out.println("[ElytraMace] OLD mixin check: " + (shouldApply ? "APPLYING" : "SKIPPING"));
        } else if (mixinClassName.endsWith("_MaceAttack_NEW")) {
            shouldApply = HAS_FALL_DISTANCE;
            System.out.println("[ElytraMace] NEW mixin check: " + (shouldApply ? "APPLYING" : "SKIPPING"));
        }

        return shouldApply;
    }

    // ---- boilerplate ----
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}