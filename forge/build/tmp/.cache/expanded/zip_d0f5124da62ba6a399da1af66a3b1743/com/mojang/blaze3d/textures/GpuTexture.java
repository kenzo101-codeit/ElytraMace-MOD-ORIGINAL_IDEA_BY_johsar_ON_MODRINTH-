package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@DontObfuscate
public abstract class GpuTexture implements AutoCloseable, net.minecraftforge.client.extensions.IForgeGpuTexture {
    public static final int USAGE_COPY_DST = 1;
    public static final int USAGE_COPY_SRC = 2;
    public static final int USAGE_TEXTURE_BINDING = 4;
    public static final int USAGE_RENDER_ATTACHMENT = 8;
    public static final int USAGE_CUBEMAP_COMPATIBLE = 16;
    private final TextureFormat format;
    private final int width;
    private final int height;
    private final int depthOrLayers;
    private final int mipLevels;
    private final int usage;
    private final String label;
    protected AddressMode addressModeU = AddressMode.REPEAT;
    protected AddressMode addressModeV = AddressMode.REPEAT;
    protected FilterMode minFilter = FilterMode.NEAREST;
    protected FilterMode magFilter = FilterMode.LINEAR;
    protected boolean useMipmaps = true;

    public GpuTexture(int p_393042_, String p_395679_, TextureFormat p_392008_, int p_394574_, int p_397229_, int p_406893_, int p_405806_) {
        this.usage = p_393042_;
        this.label = p_395679_;
        this.format = p_392008_;
        this.width = p_394574_;
        this.height = p_397229_;
        this.depthOrLayers = p_406893_;
        this.mipLevels = p_405806_;
    }

    public int getWidth(int p_397572_) {
        return this.width >> p_397572_;
    }

    public int getHeight(int p_394674_) {
        return this.height >> p_394674_;
    }

    public int getDepthOrLayers() {
        return this.depthOrLayers;
    }

    public int getMipLevels() {
        return this.mipLevels;
    }

    public TextureFormat getFormat() {
        return this.format;
    }

    public int usage() {
        return this.usage;
    }

    public void setAddressMode(AddressMode p_391531_) {
        this.setAddressMode(p_391531_, p_391531_);
    }

    public void setAddressMode(AddressMode p_396204_, AddressMode p_392726_) {
        this.addressModeU = p_396204_;
        this.addressModeV = p_392726_;
    }

    public void setTextureFilter(FilterMode p_393733_, boolean p_394281_) {
        this.setTextureFilter(p_393733_, p_393733_, p_394281_);
    }

    public void setTextureFilter(FilterMode p_396700_, FilterMode p_393522_, boolean p_396120_) {
        this.minFilter = p_396700_;
        this.magFilter = p_393522_;
        this.setUseMipmaps(p_396120_);
    }

    public void setUseMipmaps(boolean p_406752_) {
        this.useMipmaps = p_406752_;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public abstract void close();

    public abstract boolean isClosed();
}
