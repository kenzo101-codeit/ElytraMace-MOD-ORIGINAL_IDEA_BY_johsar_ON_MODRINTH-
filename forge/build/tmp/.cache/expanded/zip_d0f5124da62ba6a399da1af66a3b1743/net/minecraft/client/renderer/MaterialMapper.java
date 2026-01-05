package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record MaterialMapper(ResourceLocation sheet, String prefix) {
    public Material apply(ResourceLocation p_395004_) {
        return new Material(this.sheet, p_395004_.withPrefix(this.prefix + "/"));
    }

    public Material defaultNamespaceApply(String p_395591_) {
        return this.apply(ResourceLocation.withDefaultNamespace(p_395591_));
    }
}