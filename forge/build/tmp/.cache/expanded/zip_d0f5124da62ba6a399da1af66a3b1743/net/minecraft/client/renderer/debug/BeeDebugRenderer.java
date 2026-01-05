package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.debug.DebugBeeInfo;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugHiveInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
    private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
    private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
    private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
    private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_MEMBERS = true;
    private static final boolean SHOW_BLACKLISTS = true;
    private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
    private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final float TEXT_SCALE = 0.02F;
    private static final int ORANGE = -23296;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private final Minecraft minecraft;
    @Nullable
    private UUID lastLookedAtUuid;

    public BeeDebugRenderer(Minecraft p_113053_) {
        this.minecraft = p_113053_;
    }

    @Override
    public void render(
        PoseStack p_113061_, MultiBufferSource p_113062_, double p_113063_, double p_113064_, double p_113065_, DebugValueAccess p_428757_, Frustum p_425727_
    ) {
        this.doRender(p_113061_, p_113062_, p_428757_);
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void doRender(PoseStack p_270886_, MultiBufferSource p_270808_, DebugValueAccess p_429458_) {
        BlockPos blockpos = this.getCamera().getBlockPosition();
        p_429458_.forEachEntity(DebugSubscriptions.BEES, (p_420934_, p_420935_) -> {
            if (this.minecraft.player.closerThan(p_420934_, 30.0)) {
                DebugGoalInfo debuggoalinfo = p_429458_.getEntityValue(DebugSubscriptions.GOAL_SELECTORS, p_420934_);
                this.renderBeeInfo(p_270886_, p_270808_, p_420934_, p_420935_, debuggoalinfo);
            }
        });
        this.renderFlowerInfos(p_270886_, p_270808_, p_429458_);
        Map<BlockPos, Set<UUID>> map = this.createHiveBlacklistMap(p_429458_);
        p_429458_.forEachBlock(DebugSubscriptions.BEE_HIVES, (p_420944_, p_420945_) -> {
            if (blockpos.closerThan(p_420944_, 30.0)) {
                highlightHive(p_270886_, p_270808_, p_420944_);
                Set<UUID> set = map.getOrDefault(p_420944_, Set.of());
                this.renderHiveInfo(p_270886_, p_270808_, p_420944_, p_420945_, set, p_429458_);
            }
        });
        this.getGhostHives(p_429458_).forEach((p_269699_, p_269700_) -> {
            if (blockpos.closerThan(p_269699_, 30.0)) {
                this.renderGhostHive(p_270886_, p_270808_, p_269699_, (List<String>)p_269700_);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> createHiveBlacklistMap(DebugValueAccess p_427268_) {
        Map<BlockPos, Set<UUID>> map = new HashMap<>();
        p_427268_.forEachEntity(DebugSubscriptions.BEES, (p_420937_, p_420938_) -> {
            for (BlockPos blockpos : p_420938_.blacklistedHives()) {
                map.computeIfAbsent(blockpos, p_296252_ -> new HashSet<>()).add(p_420937_.getUUID());
            }
        });
        return map;
    }

    private void renderFlowerInfos(PoseStack p_270578_, MultiBufferSource p_270098_, DebugValueAccess p_428203_) {
        Map<BlockPos, Set<UUID>> map = new HashMap<>();
        p_428203_.forEachEntity(DebugSubscriptions.BEES, (p_420955_, p_420956_) -> {
            if (p_420956_.flowerPos().isPresent()) {
                map.computeIfAbsent(p_420956_.flowerPos().get(), p_420926_ -> new HashSet<>()).add(p_420955_.getUUID());
            }
        });
        map.forEach((p_420952_, p_420953_) -> {
            Set<String> set = p_420953_.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
            int i = 1;
            DebugRenderer.renderTextOverBlock(p_270578_, p_270098_, set.toString(), p_420952_, i++, -256, 0.02F);
            DebugRenderer.renderTextOverBlock(p_270578_, p_270098_, "Flower", p_420952_, i++, -1, 0.02F);
            float f = 0.05F;
            DebugRenderer.renderFilledBox(p_270578_, p_270098_, p_420952_, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
        });
    }

    private static String getBeeUuidsAsString(Collection<UUID> p_113116_) {
        if (p_113116_.isEmpty()) {
            return "-";
        } else {
            return p_113116_.size() > 3
                ? p_113116_.size() + " bees"
                : p_113116_.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
        }
    }

    private static void highlightHive(PoseStack p_270133_, MultiBufferSource p_270766_, BlockPos p_270687_) {
        float f = 0.05F;
        DebugRenderer.renderFilledBox(p_270133_, p_270766_, p_270687_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
    }

    private void renderGhostHive(PoseStack p_270949_, MultiBufferSource p_270718_, BlockPos p_270550_, List<String> p_270221_) {
        float f = 0.05F;
        DebugRenderer.renderFilledBox(p_270949_, p_270718_, p_270550_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
        DebugRenderer.renderTextOverBlock(p_270949_, p_270718_, p_270221_.toString(), p_270550_, 0, -256, 0.02F);
        DebugRenderer.renderTextOverBlock(p_270949_, p_270718_, "Ghost Hive", p_270550_, 1, -65536, 0.02F);
    }

    private void renderHiveInfo(
        PoseStack p_270194_, MultiBufferSource p_270431_, BlockPos p_427329_, DebugHiveInfo p_422944_, Collection<UUID> p_270946_, DebugValueAccess p_429657_
    ) {
        int i = 0;
        if (!p_270946_.isEmpty()) {
            renderTextOverHive(p_270194_, p_270431_, "Blacklisted by " + getBeeUuidsAsString(p_270946_), p_427329_, p_422944_, i++, -65536);
        }

        renderTextOverHive(p_270194_, p_270431_, "Out: " + getBeeUuidsAsString(this.getHiveMembers(p_427329_, p_429657_)), p_427329_, p_422944_, i++, -3355444);
        if (p_422944_.occupantCount() == 0) {
            renderTextOverHive(p_270194_, p_270431_, "In: -", p_427329_, p_422944_, i++, -256);
        } else if (p_422944_.occupantCount() == 1) {
            renderTextOverHive(p_270194_, p_270431_, "In: 1 bee", p_427329_, p_422944_, i++, -256);
        } else {
            renderTextOverHive(p_270194_, p_270431_, "In: " + p_422944_.occupantCount() + " bees", p_427329_, p_422944_, i++, -256);
        }

        renderTextOverHive(p_270194_, p_270431_, "Honey: " + p_422944_.honeyLevel(), p_427329_, p_422944_, i++, -23296);
        renderTextOverHive(
            p_270194_, p_270431_, p_422944_.type().getName().getString() + (p_422944_.sedated() ? " (sedated)" : ""), p_427329_, p_422944_, i++, -1
        );
    }

    private void renderBeeInfo(PoseStack p_270154_, MultiBufferSource p_270397_, Entity p_427794_, DebugBeeInfo p_427535_, @Nullable DebugGoalInfo p_422631_) {
        boolean flag = this.isBeeSelected(p_427794_);
        int i = 0;
        DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, p_427535_.toString(), -1, 0.03F);
        if (p_427535_.hivePos().isEmpty()) {
            DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, "No hive", -98404, 0.02F);
        } else {
            DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, "Hive: " + this.getPosDescription(p_427794_, p_427535_.hivePos().get()), -256, 0.02F);
        }

        if (p_427535_.flowerPos().isEmpty()) {
            DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, "No flower", -98404, 0.02F);
        } else {
            DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, "Flower: " + this.getPosDescription(p_427794_, p_427535_.flowerPos().get()), -256, 0.02F);
        }

        if (p_422631_ != null) {
            for (DebugGoalInfo.DebugGoal debuggoalinfo$debuggoal : p_422631_.goals()) {
                if (debuggoalinfo$debuggoal.isRunning()) {
                    DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, debuggoalinfo$debuggoal.name(), -16711936, 0.02F);
                }
            }
        }

        if (p_427535_.travelTicks() > 0) {
            int j = p_427535_.travelTicks() < 2400 ? -3355444 : -23296;
            DebugRenderer.renderTextOverMob(p_270154_, p_270397_, p_427794_, i++, "Travelling: " + p_427535_.travelTicks() + " ticks", j, 0.02F);
        }
    }

    private static void renderTextOverHive(
        PoseStack p_270915_, MultiBufferSource p_270663_, String p_270119_, BlockPos p_430067_, DebugHiveInfo p_423346_, int p_270930_, int p_270094_
    ) {
        DebugRenderer.renderTextOverBlock(p_270915_, p_270663_, p_270119_, p_430067_, p_270930_, p_270094_, 0.02F);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private String getPosDescription(Entity p_428780_, BlockPos p_113070_) {
        double d0 = p_113070_.distToCenterSqr(p_428780_.position());
        double d1 = Math.round(d0 * 10.0) / 10.0;
        return p_113070_.toShortString() + " (dist " + d1 + ")";
    }

    private boolean isBeeSelected(Entity p_424141_) {
        return Objects.equals(this.lastLookedAtUuid, p_424141_.getUUID());
    }

    private Collection<UUID> getHiveMembers(BlockPos p_113130_, DebugValueAccess p_430632_) {
        Set<UUID> set = new HashSet<>();
        p_430632_.forEachEntity(DebugSubscriptions.BEES, (p_420948_, p_420949_) -> {
            if (p_420949_.hasHive(p_113130_)) {
                set.add(p_420948_.getUUID());
            }
        });
        return set;
    }

    private Map<BlockPos, List<String>> getGhostHives(DebugValueAccess p_423818_) {
        Map<BlockPos, List<String>> map = new HashMap<>();
        p_423818_.forEachEntity(DebugSubscriptions.BEES, (p_420929_, p_420930_) -> {
            if (p_420930_.hivePos().isPresent() && p_423818_.getBlockValue(DebugSubscriptions.BEE_HIVES, p_420930_.hivePos().get()) == null) {
                map.computeIfAbsent(p_420930_.hivePos().get(), p_113140_ -> Lists.newArrayList()).add(DebugEntityNameGenerator.getEntityName(p_420929_));
            }
        });
        return map;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(p_113059_ -> this.lastLookedAtUuid = p_113059_.getUUID());
    }
}