package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameTestBlockHighlightRenderer {
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final float PADDING = 0.02F;
    private final Map<BlockPos, GameTestBlockHighlightRenderer.Marker> markers = Maps.newHashMap();

    public void highlightPos(BlockPos p_423694_, BlockPos p_426252_) {
        String s = p_426252_.toShortString();
        this.markers.put(p_423694_, new GameTestBlockHighlightRenderer.Marker(-2147418368, s, Util.getMillis() + 10000L));
    }

    public void clear() {
        this.markers.clear();
    }

    public void render(PoseStack p_429674_, MultiBufferSource p_429255_) {
        long i = Util.getMillis();
        this.markers.entrySet().removeIf(p_427590_ -> i > p_427590_.getValue().removeAtTime);
        this.markers.forEach((p_425086_, p_425769_) -> this.renderMarker(p_429674_, p_429255_, p_425086_, p_425769_));
    }

    private void renderMarker(PoseStack p_425127_, MultiBufferSource p_429105_, BlockPos p_430532_, GameTestBlockHighlightRenderer.Marker p_426048_) {
        DebugRenderer.renderFilledBox(
            p_425127_, p_429105_, p_430532_, 0.02F, p_426048_.getR(), p_426048_.getG(), p_426048_.getB(), p_426048_.getA() * 0.75F
        );
        if (!p_426048_.text.isEmpty()) {
            double d0 = p_430532_.getX() + 0.5;
            double d1 = p_430532_.getY() + 1.2;
            double d2 = p_430532_.getZ() + 0.5;
            DebugRenderer.renderFloatingText(p_425127_, p_429105_, p_426048_.text, d0, d1, d2, -1, 0.01F, true, 0.0F, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    record Marker(int color, String text, long removeAtTime) {
        public float getR() {
            return ARGB.redFloat(this.color);
        }

        public float getG() {
            return ARGB.greenFloat(this.color);
        }

        public float getB() {
            return ARGB.blueFloat(this.color);
        }

        public float getA() {
            return ARGB.alphaFloat(this.color);
        }
    }
}