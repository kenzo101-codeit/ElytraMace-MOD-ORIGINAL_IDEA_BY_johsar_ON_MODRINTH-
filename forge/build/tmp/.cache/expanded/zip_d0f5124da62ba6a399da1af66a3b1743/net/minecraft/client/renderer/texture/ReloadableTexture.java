package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ReloadableTexture extends AbstractTexture {
    private final ResourceLocation resourceId;

    public ReloadableTexture(ResourceLocation p_378292_) {
        this.resourceId = p_378292_;
    }

    public ResourceLocation resourceId() {
        return this.resourceId;
    }

    public void apply(TextureContents p_376644_) {
        boolean flag = p_376644_.clamp();
        boolean flag1 = p_376644_.blur();

        try (NativeImage nativeimage = p_376644_.image()) {
            this.doLoad(nativeimage, flag1, flag);
        }
    }

    protected void doLoad(NativeImage p_378310_, boolean p_378225_, boolean p_378337_) {
        GpuDevice gpudevice = RenderSystem.getDevice();
        this.close();
        this.texture = gpudevice.createTexture(this.resourceId::toString, 5, TextureFormat.RGBA8, p_378310_.getWidth(), p_378310_.getHeight(), 1, 1);
        this.textureView = gpudevice.createTextureView(this.texture);
        this.setFilter(p_378225_, false);
        this.setClamp(p_378337_);
        gpudevice.createCommandEncoder().writeToTexture(this.texture, p_378310_);
    }

    public abstract TextureContents loadContents(ResourceManager p_378474_) throws IOException;
}