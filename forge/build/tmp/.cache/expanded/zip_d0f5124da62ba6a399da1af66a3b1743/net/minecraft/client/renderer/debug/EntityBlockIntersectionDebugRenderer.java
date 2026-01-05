package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugEntityBlockIntersection;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityBlockIntersectionDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final float PADDING = 0.02F;

    @Override
    public void render(
        PoseStack p_424769_, MultiBufferSource p_425308_, double p_427743_, double p_422332_, double p_428478_, DebugValueAccess p_429428_, Frustum p_427455_
    ) {
        p_429428_.forEachBlock(DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, (p_425399_, p_422566_) -> {
            float f = ARGB.redFloat(p_422566_.color());
            float f1 = ARGB.greenFloat(p_422566_.color());
            float f2 = ARGB.blueFloat(p_422566_.color());
            float f3 = ARGB.alphaFloat(p_422566_.color());
            DebugRenderer.renderFilledBox(p_424769_, p_425308_, p_425399_, 0.02F, f, f1, f2, f3);
        });
    }
}