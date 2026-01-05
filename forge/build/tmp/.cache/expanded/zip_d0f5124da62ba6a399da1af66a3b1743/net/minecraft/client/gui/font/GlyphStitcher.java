package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlyphStitcher implements AutoCloseable {
    private final TextureManager textureManager;
    private final ResourceLocation texturePrefix;
    private final List<FontTexture> textures = new ArrayList<>();

    public GlyphStitcher(TextureManager p_422485_, ResourceLocation p_427218_) {
        this.textureManager = p_422485_;
        this.texturePrefix = p_427218_;
    }

    public void reset() {
        int i = this.textures.size();
        this.textures.clear();

        for (int j = 0; j < i; j++) {
            this.textureManager.release(this.textureName(j));
        }
    }

    @Override
    public void close() {
        this.reset();
    }

    @Nullable
    public BakedSheetGlyph stitch(GlyphInfo p_431180_, GlyphBitmap p_429310_) {
        for (FontTexture fonttexture : this.textures) {
            BakedSheetGlyph bakedsheetglyph = fonttexture.add(p_431180_, p_429310_);
            if (bakedsheetglyph != null) {
                return bakedsheetglyph;
            }
        }

        int i = this.textures.size();
        ResourceLocation resourcelocation = this.textureName(i);
        boolean flag = p_429310_.isColored();
        GlyphRenderTypes glyphrendertypes = flag ? GlyphRenderTypes.createForColorTexture(resourcelocation) : GlyphRenderTypes.createForIntensityTexture(resourcelocation);
        FontTexture fonttexture1 = new FontTexture(resourcelocation::toString, glyphrendertypes, flag);
        this.textures.add(fonttexture1);
        this.textureManager.register(resourcelocation, fonttexture1);
        return fonttexture1.add(p_431180_, p_429310_);
    }

    private ResourceLocation textureName(int p_427918_) {
        return this.texturePrefix.withSuffix("/" + p_427918_);
    }
}