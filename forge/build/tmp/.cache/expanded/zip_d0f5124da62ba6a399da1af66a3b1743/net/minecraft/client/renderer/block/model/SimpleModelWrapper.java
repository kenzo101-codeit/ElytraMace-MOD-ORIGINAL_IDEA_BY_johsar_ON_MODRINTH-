package net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon, net.minecraft.client.renderer.chunk.ChunkSectionLayer layer, net.minecraft.client.renderer.chunk.ChunkSectionLayer layerFast) implements BlockModelPart {
    public static SimpleModelWrapper bake(ModelBaker p_395631_, ResourceLocation p_396950_, ModelState p_396899_) {
        ResolvedModel resolvedmodel = p_395631_.getModel(p_396950_);
        TextureSlots textureslots = resolvedmodel.getTopTextureSlots();
        boolean flag = resolvedmodel.getTopAmbientOcclusion();
        TextureAtlasSprite textureatlassprite = resolvedmodel.resolveParticleSprite(textureslots, p_395631_);
        QuadCollection quadcollection = resolvedmodel.bakeTopGeometry(textureslots, p_395631_, p_396899_);
        var ctx = resolvedmodel.getContext();
        return new SimpleModelWrapper(quadcollection, flag, textureatlassprite, ctx.getRenderType().block(), ctx.getRenderTypeFast().block());
    }

    public SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) {
        this(quads, useAmbientOcclusion, particleIcon, null, null);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction p_395134_) {
        return this.quads.getQuads(p_395134_);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.useAmbientOcclusion;
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.particleIcon;
    }
}
