package net.minecraft.gametest.framework;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record GeneratedTest(
    Map<ResourceLocation, TestData<ResourceKey<TestEnvironmentDefinition>>> tests,
    ResourceKey<Consumer<GameTestHelper>> functionKey,
    Consumer<GameTestHelper> function
) {
    public GeneratedTest(
        Map<ResourceLocation, TestData<ResourceKey<TestEnvironmentDefinition>>> p_392853_, ResourceLocation p_394604_, Consumer<GameTestHelper> p_394808_
    ) {
        this(p_392853_, ResourceKey.create(Registries.TEST_FUNCTION, p_394604_), p_394808_);
    }

    public GeneratedTest(ResourceLocation p_395068_, TestData<ResourceKey<TestEnvironmentDefinition>> p_391775_, Consumer<GameTestHelper> p_391901_) {
        this(Map.of(p_395068_, p_391775_), p_395068_, p_391901_);
    }
}