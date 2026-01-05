package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.model.CopperGolemStatueModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CopperGolemStatueSpecialRenderer implements NoDataSpecialModelRenderer {
    private final CopperGolemStatueModel model;
    private final ResourceLocation texture;
    static final Map<CopperGolemStatueBlock.Pose, ModelLayerLocation> MODELS = Map.of(
        CopperGolemStatueBlock.Pose.STANDING,
        ModelLayers.COPPER_GOLEM,
        CopperGolemStatueBlock.Pose.SITTING,
        ModelLayers.COPPER_GOLEM_SITTING,
        CopperGolemStatueBlock.Pose.STAR,
        ModelLayers.COPPER_GOLEM_STAR,
        CopperGolemStatueBlock.Pose.RUNNING,
        ModelLayers.COPPER_GOLEM_RUNNING
    );

    public CopperGolemStatueSpecialRenderer(CopperGolemStatueModel p_423450_, ResourceLocation p_427579_) {
        this.model = p_423450_;
        this.texture = p_427579_;
    }

    @Override
    public void submit(
        ItemDisplayContext p_429891_, PoseStack p_422495_, SubmitNodeCollector p_422708_, int p_426547_, int p_423523_, boolean p_427797_, int p_431896_
    ) {
        this.positionModel(p_422495_);
        p_422708_.submitModel(this.model, Direction.SOUTH, p_422495_, RenderType.entityCutoutNoCull(this.texture), p_426547_, p_423523_, -1, null, p_431896_, null);
    }

    @Override
    public void getExtents(Set<Vector3f> p_429021_) {
        PoseStack posestack = new PoseStack();
        this.positionModel(posestack);
        this.model.root().getExtentsForGui(posestack, p_429021_);
    }

    private void positionModel(PoseStack p_430903_) {
        p_430903_.translate(0.5F, 1.5F, 0.5F);
        p_430903_.scale(-1.0F, -1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public record Unbaked(ResourceLocation texture, CopperGolemStatueBlock.Pose pose) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<CopperGolemStatueSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_423413_ -> p_423413_.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(CopperGolemStatueSpecialRenderer.Unbaked::texture),
                    CopperGolemStatueBlock.Pose.CODEC.fieldOf("pose").forGetter(CopperGolemStatueSpecialRenderer.Unbaked::pose)
                )
                .apply(p_423413_, CopperGolemStatueSpecialRenderer.Unbaked::new)
        );

        public Unbaked(WeatheringCopper.WeatherState p_424329_, CopperGolemStatueBlock.Pose p_426378_) {
            this(CopperGolemOxidationLevels.getOxidationLevel(p_424329_).texture(), p_426378_);
        }

        @Override
        public MapCodec<CopperGolemStatueSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext p_431217_) {
            CopperGolemStatueModel coppergolemstatuemodel = new CopperGolemStatueModel(
                p_431217_.entityModelSet().bakeLayer(CopperGolemStatueSpecialRenderer.MODELS.get(this.pose))
            );
            return new CopperGolemStatueSpecialRenderer(coppergolemstatuemodel, this.texture);
        }
    }
}