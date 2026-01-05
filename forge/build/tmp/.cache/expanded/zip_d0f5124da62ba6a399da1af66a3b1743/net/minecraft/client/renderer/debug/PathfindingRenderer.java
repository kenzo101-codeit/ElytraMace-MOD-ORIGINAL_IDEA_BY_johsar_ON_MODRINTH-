package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Locale;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugPathInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final float MAX_RENDER_DIST = 80.0F;
    private static final int MAX_TARGETING_DIST = 8;
    private static final boolean SHOW_ONLY_SELECTED = false;
    private static final boolean SHOW_OPEN_CLOSED = true;
    private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
    private static final boolean SHOW_GROUND_LABELS = true;
    private static final float TEXT_SCALE = 0.02F;

    @Override
    public void render(
        PoseStack p_113629_, MultiBufferSource p_113630_, double p_113631_, double p_113632_, double p_113633_, DebugValueAccess p_425397_, Frustum p_428493_
    ) {
        p_425397_.forEachEntity(
            DebugSubscriptions.ENTITY_PATHS,
            (p_424992_, p_423733_) -> renderPath(p_113629_, p_113630_, p_113631_, p_113632_, p_113633_, p_423733_.path(), p_423733_.maxNodeDistance())
        );
    }

    private static void renderPath(
        PoseStack p_427444_, MultiBufferSource p_426261_, double p_424791_, double p_429196_, double p_428938_, Path p_425995_, float p_422888_
    ) {
        renderPath(p_427444_, p_426261_, p_425995_, p_422888_, true, true, p_424791_, p_429196_, p_428938_);
    }

    public static void renderPath(
        PoseStack p_270399_,
        MultiBufferSource p_270359_,
        Path p_270189_,
        float p_270841_,
        boolean p_270481_,
        boolean p_270748_,
        double p_270187_,
        double p_270252_,
        double p_270371_
    ) {
        renderPathLine(p_270399_, p_270359_.getBuffer(RenderType.debugLineStrip(6.0)), p_270189_, p_270187_, p_270252_, p_270371_);
        BlockPos blockpos = p_270189_.getTarget();
        if (distanceToCamera(blockpos, p_270187_, p_270252_, p_270371_) <= 80.0F) {
            DebugRenderer.renderFilledBox(
                p_270399_,
                p_270359_,
                new AABB(
                        blockpos.getX() + 0.25F,
                        blockpos.getY() + 0.25F,
                        blockpos.getZ() + 0.25,
                        blockpos.getX() + 0.75F,
                        blockpos.getY() + 0.75F,
                        blockpos.getZ() + 0.75F
                    )
                    .move(-p_270187_, -p_270252_, -p_270371_),
                0.0F,
                1.0F,
                0.0F,
                0.5F
            );

            for (int i = 0; i < p_270189_.getNodeCount(); i++) {
                Node node = p_270189_.getNode(i);
                if (distanceToCamera(node.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
                    float f = i == p_270189_.getNextNodeIndex() ? 1.0F : 0.0F;
                    float f1 = i == p_270189_.getNextNodeIndex() ? 0.0F : 1.0F;
                    DebugRenderer.renderFilledBox(
                        p_270399_,
                        p_270359_,
                        new AABB(
                                node.x + 0.5F - p_270841_,
                                node.y + 0.01F * i,
                                node.z + 0.5F - p_270841_,
                                node.x + 0.5F + p_270841_,
                                node.y + 0.25F + 0.01F * i,
                                node.z + 0.5F + p_270841_
                            )
                            .move(-p_270187_, -p_270252_, -p_270371_),
                        f,
                        0.0F,
                        f1,
                        0.5F
                    );
                }
            }
        }

        Path.DebugData path$debugdata = p_270189_.debugData();
        if (p_270481_ && path$debugdata != null) {
            for (Node node1 : path$debugdata.closedSet()) {
                if (distanceToCamera(node1.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
                    DebugRenderer.renderFilledBox(
                        p_270399_,
                        p_270359_,
                        new AABB(
                                node1.x + 0.5F - p_270841_ / 2.0F,
                                node1.y + 0.01F,
                                node1.z + 0.5F - p_270841_ / 2.0F,
                                node1.x + 0.5F + p_270841_ / 2.0F,
                                node1.y + 0.1,
                                node1.z + 0.5F + p_270841_ / 2.0F
                            )
                            .move(-p_270187_, -p_270252_, -p_270371_),
                        1.0F,
                        0.8F,
                        0.8F,
                        0.5F
                    );
                }
            }

            for (Node node3 : path$debugdata.openSet()) {
                if (distanceToCamera(node3.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
                    DebugRenderer.renderFilledBox(
                        p_270399_,
                        p_270359_,
                        new AABB(
                                node3.x + 0.5F - p_270841_ / 2.0F,
                                node3.y + 0.01F,
                                node3.z + 0.5F - p_270841_ / 2.0F,
                                node3.x + 0.5F + p_270841_ / 2.0F,
                                node3.y + 0.1,
                                node3.z + 0.5F + p_270841_ / 2.0F
                            )
                            .move(-p_270187_, -p_270252_, -p_270371_),
                        0.8F,
                        1.0F,
                        1.0F,
                        0.5F
                    );
                }
            }
        }

        if (p_270748_) {
            for (int j = 0; j < p_270189_.getNodeCount(); j++) {
                Node node2 = p_270189_.getNode(j);
                if (distanceToCamera(node2.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
                    DebugRenderer.renderFloatingText(
                        p_270399_,
                        p_270359_,
                        String.valueOf(node2.type),
                        node2.x + 0.5,
                        node2.y + 0.75,
                        node2.z + 0.5,
                        -1,
                        0.02F,
                        true,
                        0.0F,
                        true
                    );
                    DebugRenderer.renderFloatingText(
                        p_270399_,
                        p_270359_,
                        String.format(Locale.ROOT, "%.2f", node2.costMalus),
                        node2.x + 0.5,
                        node2.y + 0.25,
                        node2.z + 0.5,
                        -1,
                        0.02F,
                        true,
                        0.0F,
                        true
                    );
                }
            }
        }
    }

    public static void renderPathLine(PoseStack p_270666_, VertexConsumer p_270602_, Path p_270511_, double p_270524_, double p_270163_, double p_270176_) {
        for (int i = 0; i < p_270511_.getNodeCount(); i++) {
            Node node = p_270511_.getNode(i);
            if (!(distanceToCamera(node.asBlockPos(), p_270524_, p_270163_, p_270176_) > 80.0F)) {
                float f = (float)i / p_270511_.getNodeCount() * 0.33F;
                int j = i == 0 ? -16777216 : ARGB.opaque(Mth.hsvToRgb(f, 0.9F, 0.9F));
                p_270602_.addVertex(
                        p_270666_.last(),
                        (float)(node.x - p_270524_ + 0.5),
                        (float)(node.y - p_270163_ + 0.5),
                        (float)(node.z - p_270176_ + 0.5)
                    )
                    .setColor(j);
            }
        }
    }

    private static float distanceToCamera(BlockPos p_113635_, double p_113636_, double p_113637_, double p_113638_) {
        return (float)(Math.abs(p_113635_.getX() - p_113636_) + Math.abs(p_113635_.getY() - p_113637_) + Math.abs(p_113635_.getZ() - p_113638_));
    }
}