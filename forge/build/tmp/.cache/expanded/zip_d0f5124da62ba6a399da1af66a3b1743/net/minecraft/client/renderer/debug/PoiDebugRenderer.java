package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.debug.DebugBrainDump;
import net.minecraft.util.debug.DebugPoiInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoiDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
    private static final float TEXT_SCALE = 0.02F;
    private static final int ORANGE = -23296;
    private final BrainDebugRenderer brainRenderer;

    public PoiDebugRenderer(BrainDebugRenderer p_423648_) {
        this.brainRenderer = p_423648_;
    }

    @Override
    public void render(
        PoseStack p_431142_, MultiBufferSource p_425757_, double p_431422_, double p_428111_, double p_428385_, DebugValueAccess p_424547_, Frustum p_422627_
    ) {
        BlockPos blockpos = BlockPos.containing(p_431422_, p_428111_, p_428385_);
        p_424547_.forEachBlock(DebugSubscriptions.POIS, (p_424677_, p_428311_) -> {
            if (blockpos.closerThan(p_424677_, 30.0)) {
                highlightPoi(p_431142_, p_425757_, p_424677_);
                this.renderPoiInfo(p_431142_, p_425757_, p_428311_, p_424547_);
            }
        });
        this.brainRenderer.getGhostPois(p_424547_).forEach((p_422580_, p_429062_) -> {
            if (p_424547_.getBlockValue(DebugSubscriptions.POIS, p_422580_) == null) {
                if (blockpos.closerThan(p_422580_, 30.0)) {
                    this.renderGhostPoi(p_431142_, p_425757_, p_422580_, (List<String>)p_429062_);
                }
            }
        });
    }

    private static void highlightPoi(PoseStack p_430269_, MultiBufferSource p_425319_, BlockPos p_430422_) {
        float f = 0.05F;
        DebugRenderer.renderFilledBox(p_430269_, p_425319_, p_430422_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
    }

    private void renderGhostPoi(PoseStack p_430649_, MultiBufferSource p_426357_, BlockPos p_429608_, List<String> p_428316_) {
        float f = 0.05F;
        DebugRenderer.renderFilledBox(p_430649_, p_426357_, p_429608_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
        DebugRenderer.renderTextOverBlock(p_430649_, p_426357_, p_428316_.toString(), p_429608_, 0, -256, 0.02F);
        DebugRenderer.renderTextOverBlock(p_430649_, p_426357_, "Ghost POI", p_429608_, 1, -65536, 0.02F);
    }

    private void renderPoiInfo(PoseStack p_427712_, MultiBufferSource p_422325_, DebugPoiInfo p_430302_, DebugValueAccess p_428830_) {
        int i = 0;
        if (SharedConstants.DEBUG_BRAIN) {
            List<String> list = this.getTicketHolderNames(p_430302_, false, p_428830_);
            if (list.size() < 4) {
                renderTextOverPoi(p_427712_, p_422325_, "Owners: " + list, p_430302_, i, -256);
            } else {
                renderTextOverPoi(p_427712_, p_422325_, list.size() + " ticket holders", p_430302_, i, -256);
            }

            i++;
            List<String> list1 = this.getTicketHolderNames(p_430302_, true, p_428830_);
            if (list1.size() < 4) {
                renderTextOverPoi(p_427712_, p_422325_, "Candidates: " + list1, p_430302_, i, -23296);
            } else {
                renderTextOverPoi(p_427712_, p_422325_, list1.size() + " potential owners", p_430302_, i, -23296);
            }

            i++;
        }

        renderTextOverPoi(p_427712_, p_422325_, "Free tickets: " + p_430302_.freeTicketCount(), p_430302_, i, -256);
        renderTextOverPoi(p_427712_, p_422325_, p_430302_.poiType().getRegisteredName(), p_430302_, ++i, -1);
    }

    private static void renderTextOverPoi(PoseStack p_422586_, MultiBufferSource p_423594_, String p_427463_, DebugPoiInfo p_430300_, int p_426136_, int p_424914_) {
        DebugRenderer.renderTextOverBlock(p_422586_, p_423594_, p_427463_, p_430300_.pos(), p_426136_, p_424914_, 0.02F);
    }

    private List<String> getTicketHolderNames(DebugPoiInfo p_429201_, boolean p_426035_, DebugValueAccess p_429116_) {
        List<String> list = new ArrayList<>();
        p_429116_.forEachEntity(DebugSubscriptions.BRAINS, (p_425410_, p_425347_) -> {
            boolean flag = p_426035_ ? p_425347_.hasPotentialPoi(p_429201_.pos()) : p_425347_.hasPoi(p_429201_.pos());
            if (flag) {
                list.add(DebugEntityNameGenerator.getEntityName(p_425410_.getUUID()));
            }
        });
        return list;
    }
}