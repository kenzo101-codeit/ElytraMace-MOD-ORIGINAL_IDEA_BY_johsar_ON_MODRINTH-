package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3x2f;

@OnlyIn(Dist.CLIENT)
public record ColoredRectangleRenderState(
    RenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2f pose,
    int x0,
    int y0,
    int x1,
    int y1,
    int col1,
    int col2,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public ColoredRectangleRenderState(
        RenderPipeline p_405816_,
        TextureSetup p_407489_,
        Matrix3x2f p_408671_,
        int p_410490_,
        int p_409325_,
        int p_410181_,
        int p_409649_,
        int p_406253_,
        int p_409090_,
        @Nullable ScreenRectangle p_408385_
    ) {
        this(
            p_405816_,
            p_407489_,
            p_408671_,
            p_410490_,
            p_409325_,
            p_410181_,
            p_409649_,
            p_406253_,
            p_409090_,
            p_408385_,
            getBounds(p_410490_, p_409325_, p_410181_, p_409649_, p_408671_, p_408385_)
        );
    }

    @Override
    public void buildVertices(VertexConsumer p_409842_) {
        p_409842_.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
        p_409842_.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col2());
        p_409842_.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
        p_409842_.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col1());
    }

    @Nullable
    private static ScreenRectangle getBounds(
        int p_409775_, int p_408911_, int p_405873_, int p_405895_, Matrix3x2f p_406223_, @Nullable ScreenRectangle p_409849_
    ) {
        ScreenRectangle screenrectangle = new ScreenRectangle(p_409775_, p_408911_, p_405873_ - p_409775_, p_405895_ - p_408911_).transformMaxBounds(p_406223_);
        return p_409849_ != null ? p_409849_.intersection(screenrectangle) : screenrectangle;
    }

    @Override
    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    @Nullable
    @Override
    public ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Nullable
    @Override
    public ScreenRectangle bounds() {
        return this.bounds;
    }
}