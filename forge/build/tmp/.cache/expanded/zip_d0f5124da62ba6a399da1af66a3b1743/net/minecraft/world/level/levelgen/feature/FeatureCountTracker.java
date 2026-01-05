package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LoadingCache<ServerLevel, FeatureCountTracker.LevelData> data = CacheBuilder.newBuilder()
        .weakKeys()
        .expireAfterAccess(5L, TimeUnit.MINUTES)
        .build(new CacheLoader<ServerLevel, FeatureCountTracker.LevelData>() {
            public FeatureCountTracker.LevelData load(ServerLevel p_190902_) {
                return new FeatureCountTracker.LevelData(Object2IntMaps.synchronize(new Object2IntOpenHashMap<>()), new MutableInt(0));
            }
        });

    public static void chunkDecorated(ServerLevel p_190882_) {
        try {
            data.get(p_190882_).chunksWithFeatures().increment();
        } catch (Exception exception) {
            LOGGER.error("Failed to increment chunk count", (Throwable)exception);
        }
    }

    public static void featurePlaced(ServerLevel p_190884_, ConfiguredFeature<?, ?> p_190885_, Optional<PlacedFeature> p_190886_) {
        try {
            data.get(p_190884_)
                .featureData()
                .computeInt(new FeatureCountTracker.FeatureData(p_190885_, p_190886_), (p_190891_, p_190892_) -> p_190892_ == null ? 1 : p_190892_ + 1);
        } catch (Exception exception) {
            LOGGER.error("Failed to increment feature count", (Throwable)exception);
        }
    }

    public static void clearCounts() {
        data.invalidateAll();
        LOGGER.debug("Cleared feature counts");
    }

    public static void logCounts() {
        LOGGER.debug("Logging feature counts:");
        data.asMap()
            .forEach(
                (p_422231_, p_422232_) -> {
                    String s = p_422231_.dimension().location().toString();
                    boolean flag = p_422231_.getServer().isRunning();
                    Registry<PlacedFeature> registry = p_422231_.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
                    String s1 = (flag ? "running" : "dead") + " " + s;
                    Integer integer = p_422232_.chunksWithFeatures().getValue();
                    LOGGER.debug("{} total_chunks: {}", s1, integer);
                    p_422232_.featureData()
                        .forEach(
                            (p_422229_, p_422230_) -> LOGGER.debug(
                                "{} {} {} {} {} {}",
                                s1,
                                String.format(Locale.ROOT, "%10d", p_422230_),
                                String.format(Locale.ROOT, "%10f", (double)p_422230_.intValue() / integer.intValue()),
                                p_422229_.topFeature().flatMap(registry::getResourceKey).map(ResourceKey::location),
                                p_422229_.feature().feature(),
                                p_422229_.feature()
                            )
                        );
                }
            );
    }

    record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
    }

    record LevelData(Object2IntMap<FeatureCountTracker.FeatureData> featureData, MutableInt chunksWithFeatures) {
    }
}