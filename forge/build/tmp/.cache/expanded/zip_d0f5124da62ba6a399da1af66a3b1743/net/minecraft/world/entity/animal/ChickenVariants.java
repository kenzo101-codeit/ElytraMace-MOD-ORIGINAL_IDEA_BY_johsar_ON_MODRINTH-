package net.minecraft.world.entity.animal;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public class ChickenVariants {
    public static final ResourceKey<ChickenVariant> TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
    public static final ResourceKey<ChickenVariant> WARM = createKey(TemperatureVariants.WARM);
    public static final ResourceKey<ChickenVariant> COLD = createKey(TemperatureVariants.COLD);
    public static final ResourceKey<ChickenVariant> DEFAULT = TEMPERATE;

    private static ResourceKey<ChickenVariant> createKey(ResourceLocation p_397439_) {
        return ResourceKey.create(Registries.CHICKEN_VARIANT, p_397439_);
    }

    public static void bootstrap(BootstrapContext<ChickenVariant> p_392192_) {
        register(p_392192_, TEMPERATE, ChickenVariant.ModelType.NORMAL, "temperate_chicken", SpawnPrioritySelectors.fallback(0));
        register(p_392192_, WARM, ChickenVariant.ModelType.NORMAL, "warm_chicken", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
        register(p_392192_, COLD, ChickenVariant.ModelType.COLD, "cold_chicken", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
    }

    private static void register(
        BootstrapContext<ChickenVariant> p_392537_,
        ResourceKey<ChickenVariant> p_397508_,
        ChickenVariant.ModelType p_393651_,
        String p_391527_,
        TagKey<Biome> p_392941_
    ) {
        HolderSet<Biome> holderset = p_392537_.lookup(Registries.BIOME).getOrThrow(p_392941_);
        register(p_392537_, p_397508_, p_393651_, p_391527_, SpawnPrioritySelectors.single(new BiomeCheck(holderset), 1));
    }

    private static void register(
        BootstrapContext<ChickenVariant> p_393977_,
        ResourceKey<ChickenVariant> p_396101_,
        ChickenVariant.ModelType p_397210_,
        String p_395280_,
        SpawnPrioritySelectors p_397348_
    ) {
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("entity/chicken/" + p_395280_);
        p_393977_.register(p_396101_, new ChickenVariant(new ModelAndTexture<>(p_397210_, resourcelocation), p_397348_));
    }
}