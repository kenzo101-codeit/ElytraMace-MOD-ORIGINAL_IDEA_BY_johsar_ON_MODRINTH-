package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugBrainDump;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrainDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final boolean SHOW_NAME_FOR_ALL = true;
    private static final boolean SHOW_PROFESSION_FOR_ALL = false;
    private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
    private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
    private static final boolean SHOW_INVENTORY_FOR_ALL = false;
    private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
    private static final boolean SHOW_HEALTH_FOR_ALL = false;
    private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_ALL = false;
    private static final boolean SHOW_NAME_FOR_SELECTED = true;
    private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
    private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
    private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
    private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
    private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
    private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
    private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
    private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
    private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
    private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final float TEXT_SCALE = 0.02F;
    private static final int CYAN = -16711681;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int ORANGE = -23296;
    private final Minecraft minecraft;
    @Nullable
    private UUID lastLookedAtUuid;

    public BrainDebugRenderer(Minecraft p_113200_) {
        this.minecraft = p_113200_;
    }

    @Override
    public void render(
        PoseStack p_113214_, MultiBufferSource p_113215_, double p_113216_, double p_113217_, double p_113218_, DebugValueAccess p_429372_, Frustum p_424662_
    ) {
        this.doRender(p_113214_, p_113215_, p_113216_, p_113217_, p_113218_, p_429372_);
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void doRender(PoseStack p_270747_, MultiBufferSource p_270289_, double p_270303_, double p_270416_, double p_270542_, DebugValueAccess p_430807_) {
        p_430807_.forEachEntity(DebugSubscriptions.BRAINS, (p_420965_, p_420966_) -> {
            if (this.minecraft.player.closerThan(p_420965_, 30.0)) {
                this.renderBrainInfo(p_270747_, p_270289_, p_420965_, p_420966_, p_270303_, p_270416_, p_270542_);
            }
        });
    }

    private void renderBrainInfo(
        PoseStack p_270145_, MultiBufferSource p_270489_, Entity p_428259_, DebugBrainDump p_422492_, double p_270922_, double p_270468_, double p_270838_
    ) {
        boolean flag = this.isMobSelected(p_428259_);
        int i = 0;
        DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, p_422492_.name(), -1, 0.03F);
        i++;
        if (flag) {
            DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, p_422492_.profession() + " " + p_422492_.xp() + " xp", -1, 0.02F);
            i++;
        }

        if (flag) {
            int j = p_422492_.health() < p_422492_.maxHealth() ? -23296 : -1;
            DebugRenderer.renderTextOverMob(
                p_270145_,
                p_270489_,
                p_428259_,
                i,
                "health: " + String.format(Locale.ROOT, "%.1f", p_422492_.health()) + " / " + String.format(Locale.ROOT, "%.1f", p_422492_.maxHealth()),
                j,
                0.02F
            );
            i++;
        }

        if (flag && !p_422492_.inventory().equals("")) {
            DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, p_422492_.inventory(), -98404, 0.02F);
            i++;
        }

        if (flag) {
            for (String s : p_422492_.behaviors()) {
                DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, s, -16711681, 0.02F);
                i++;
            }
        }

        if (flag) {
            for (String s1 : p_422492_.activities()) {
                DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, s1, -16711936, 0.02F);
                i++;
            }
        }

        if (p_422492_.wantsGolem()) {
            DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, "Wants Golem", -23296, 0.02F);
            i++;
        }

        if (flag && p_422492_.angerLevel() != -1) {
            DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, "Anger Level: " + p_422492_.angerLevel(), -98404, 0.02F);
            i++;
        }

        if (flag) {
            for (String s2 : p_422492_.gossips()) {
                if (s2.startsWith(p_422492_.name())) {
                    DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, s2, -1, 0.02F);
                } else {
                    DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, s2, -23296, 0.02F);
                }

                i++;
            }
        }

        if (flag) {
            for (String s3 : Lists.reverse(p_422492_.memories())) {
                DebugRenderer.renderTextOverMob(p_270145_, p_270489_, p_428259_, i, s3, -3355444, 0.02F);
                i++;
            }
        }
    }

    private boolean isMobSelected(Entity p_429708_) {
        return Objects.equals(this.lastLookedAtUuid, p_429708_.getUUID());
    }

    public Map<BlockPos, List<String>> getGhostPois(DebugValueAccess p_429188_) {
        Map<BlockPos, List<String>> map = Maps.newHashMap();
        p_429188_.forEachEntity(DebugSubscriptions.BRAINS, (p_420958_, p_420959_) -> {
            for (BlockPos blockpos : Iterables.concat(p_420959_.pois(), p_420959_.potentialPois())) {
                map.computeIfAbsent(blockpos, p_113292_ -> Lists.newArrayList()).add(p_420959_.name());
            }
        });
        return map;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(p_113212_ -> this.lastLookedAtUuid = p_113212_.getUUID());
    }
}