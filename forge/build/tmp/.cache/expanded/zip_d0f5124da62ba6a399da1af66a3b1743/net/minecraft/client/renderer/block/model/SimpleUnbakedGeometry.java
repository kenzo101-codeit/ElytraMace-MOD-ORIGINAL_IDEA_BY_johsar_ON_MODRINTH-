package net.minecraft.client.renderer.block.model;

import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record SimpleUnbakedGeometry(List<BlockElement> elements) implements UnbakedGeometry {
    @Override
    public QuadCollection bake(TextureSlots p_397805_, ModelBaker p_395314_, ModelState p_394240_, ModelDebugName p_392934_) {
        return bake(this.elements, p_397805_, p_395314_.sprites(), p_394240_, p_392934_);
    }

    public static QuadCollection bake(
        List<BlockElement> p_393173_, TextureSlots p_395401_, SpriteGetter p_391624_, ModelState p_397074_, ModelDebugName p_393473_
    ) {
        QuadCollection.Builder quadcollection$builder = new QuadCollection.Builder();

        for (BlockElement blockelement : p_393173_) {
            blockelement.faces()
                .forEach(
                    (p_392025_, p_394051_) -> {
                        TextureAtlasSprite textureatlassprite = p_391624_.resolveSlot(p_395401_, p_394051_.texture(), p_393473_);
                        if (p_394051_.cullForDirection() == null) {
                            quadcollection$builder.addUnculledFace(bakeFace(blockelement, p_394051_, textureatlassprite, p_392025_, p_397074_));
                        } else {
                            quadcollection$builder.addCulledFace(
                                Direction.rotate(p_397074_.transformation().getMatrix(), p_394051_.cullForDirection()),
                                bakeFace(blockelement, p_394051_, textureatlassprite, p_392025_, p_397074_)
                            );
                        }
                    }
                );
        }

        return quadcollection$builder.build();
    }

    private static BakedQuad bakeFace(
        BlockElement p_396933_, BlockElementFace p_396910_, TextureAtlasSprite p_394381_, Direction p_395496_, ModelState p_393937_
    ) {
        return FaceBakery.bakeQuad(
            p_396933_.from(),
            p_396933_.to(),
            p_396910_,
            p_394381_,
            p_395496_,
            p_393937_,
            p_396933_.rotation(),
            p_396933_.shade(),
            p_396933_.lightEmission()
        );
    }
}