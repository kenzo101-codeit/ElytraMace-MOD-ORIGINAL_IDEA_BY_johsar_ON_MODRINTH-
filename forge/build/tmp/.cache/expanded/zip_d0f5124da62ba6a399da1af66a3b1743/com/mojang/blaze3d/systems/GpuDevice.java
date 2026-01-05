package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@DontObfuscate
public interface GpuDevice {
    CommandEncoder createCommandEncoder();

    GpuTexture createTexture(
        @Nullable Supplier<String> p_394357_, int p_395623_, TextureFormat p_395807_, int p_395802_, int p_396157_, int p_408810_, int p_407070_
    );

    /** Forge: same as {@link #createTexture(Supplier, int, TextureFormat, int, int, int)} but with stencil support */
    default GpuTexture createTexture(@Nullable Supplier<String> p_394357_, int p_395623_, TextureFormat p_395807_, int p_395802_, int p_396157_, int p_408810_, int p_407070_, boolean stencil) {
        return this.createTexture(p_394357_, p_395623_, p_395807_, p_395802_, p_396157_, p_408810_, p_407070_);
    }

    GpuTexture createTexture(@Nullable String p_391798_, int p_391800_, TextureFormat p_393333_, int p_395600_, int p_394065_, int p_408732_, int p_407125_);

    /** Forge: same as {@link #createTexture(String, int, TextureFormat, int, int, int)} but with stencil support */
    default GpuTexture createTexture(@Nullable String p_391798_, int p_391800_, TextureFormat p_393333_, int p_395600_, int p_394065_, int p_408732_, int p_407125_, boolean stencil) {
        return this.createTexture(p_391798_, p_391800_, p_393333_, p_395600_, p_394065_, p_408732_, p_407125_);
    }

    GpuTextureView createTextureView(GpuTexture p_409045_);

    GpuTextureView createTextureView(GpuTexture p_405974_, int p_409184_, int p_408424_);

    GpuBuffer createBuffer(@Nullable Supplier<String> p_395857_, int p_407630_, int p_407088_);

    GpuBuffer createBuffer(@Nullable Supplier<String> p_395215_, int p_397054_, ByteBuffer p_408298_);

    String getImplementationInformation();

    List<String> getLastDebugMessages();

    boolean isDebuggingEnabled();

    String getVendor();

    String getBackendName();

    String getVersion();

    String getRenderer();

    int getMaxTextureSize();

    int getUniformOffsetAlignment();

    default CompiledRenderPipeline precompilePipeline(RenderPipeline p_394764_) {
        return this.precompilePipeline(p_394764_, null);
    }

    CompiledRenderPipeline precompilePipeline(RenderPipeline p_391891_, @Nullable BiFunction<ResourceLocation, ShaderType, String> p_392310_);

    void clearPipelineCache();

    List<String> getEnabledExtensions();

    void close();
}
