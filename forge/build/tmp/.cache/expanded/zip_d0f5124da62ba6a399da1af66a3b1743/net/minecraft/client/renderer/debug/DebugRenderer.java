package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
    private final List<DebugRenderer.SimpleDebugRenderer> opaqueRenderers = new ArrayList<>();
    private final List<DebugRenderer.SimpleDebugRenderer> translucentRenderers = new ArrayList<>();
    private long lastDebugEntriesVersion;

    public DebugRenderer() {
        this.refreshRendererList();
    }

    public void refreshRendererList() {
        Minecraft minecraft = Minecraft.getInstance();
        this.opaqueRenderers.clear();
        this.translucentRenderers.clear();
        if (minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_BORDERS) && !minecraft.showOnlyReducedInfo()) {
            this.opaqueRenderers.add(new ChunkBorderRenderer(minecraft));
        }

        if (minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_OCTREE)) {
            this.opaqueRenderers.add(new OctreeDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_PATHFINDING) {
            this.opaqueRenderers.add(new PathfindingRenderer());
        }

        if (SharedConstants.DEBUG_WATER) {
            this.opaqueRenderers.add(new WaterDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_HEIGHTMAP) {
            this.opaqueRenderers.add(new HeightMapRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_COLLISION) {
            this.opaqueRenderers.add(new CollisionBoxRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_SUPPORT_BLOCKS) {
            this.opaqueRenderers.add(new SupportBlockRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_NEIGHBORSUPDATE) {
            this.opaqueRenderers.add(new NeighborsUpdateRenderer());
        }

        if (SharedConstants.DEBUG_EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER) {
            this.opaqueRenderers.add(new RedstoneWireOrientationsRenderer());
        }

        if (SharedConstants.DEBUG_STRUCTURES) {
            this.opaqueRenderers.add(new StructureRenderer());
        }

        if (SharedConstants.DEBUG_LIGHT) {
            this.opaqueRenderers.add(new LightDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_SOLID_FACE) {
            this.opaqueRenderers.add(new SolidFaceRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_VILLAGE_SECTIONS) {
            this.opaqueRenderers.add(new VillageSectionsDebugRenderer());
        }

        if (SharedConstants.DEBUG_BRAIN) {
            this.opaqueRenderers.add(new BrainDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_POI) {
            this.opaqueRenderers.add(new PoiDebugRenderer(new BrainDebugRenderer(minecraft)));
        }

        if (SharedConstants.DEBUG_BEES) {
            this.opaqueRenderers.add(new BeeDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_RAIDS) {
            this.opaqueRenderers.add(new RaidDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_GOAL_SELECTOR) {
            this.opaqueRenderers.add(new GoalSelectorDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_CHUNKS) {
            this.opaqueRenderers.add(new ChunkDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_GAME_EVENT_LISTENERS) {
            this.opaqueRenderers.add(new GameEventListenerRenderer());
        }

        if (SharedConstants.DEBUG_SKY_LIGHT_SECTIONS) {
            this.opaqueRenderers.add(new LightSectionDebugRenderer(minecraft, LightLayer.SKY));
        }

        if (SharedConstants.DEBUG_BREEZE_MOB) {
            this.opaqueRenderers.add(new BreezeDebugRenderer(minecraft));
        }

        if (SharedConstants.DEBUG_ENTITY_BLOCK_INTERSECTION) {
            this.opaqueRenderers.add(new EntityBlockIntersectionDebugRenderer());
        }

        this.translucentRenderers.add(new ChunkCullingDebugRenderer(minecraft));
    }

    public void render(
        PoseStack p_113458_,
        Frustum p_369349_,
        MultiBufferSource.BufferSource p_113459_,
        double p_113460_,
        double p_113461_,
        double p_113462_,
        boolean p_427371_
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        DebugValueAccess debugvalueaccess = minecraft.getConnection().createDebugValueAccess();
        if (minecraft.debugEntries.getCurrentlyEnabledVersion() != this.lastDebugEntriesVersion) {
            this.lastDebugEntriesVersion = minecraft.debugEntries.getCurrentlyEnabledVersion();
            this.refreshRendererList();
        }

        for (DebugRenderer.SimpleDebugRenderer debugrenderer$simpledebugrenderer : p_427371_ ? this.translucentRenderers : this.opaqueRenderers) {
            debugrenderer$simpledebugrenderer.render(p_113458_, p_113459_, p_113460_, p_113461_, p_113462_, debugvalueaccess, p_369349_);
        }
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity p_113449_, int p_113450_) {
        if (p_113449_ == null) {
            return Optional.empty();
        } else {
            Vec3 vec3 = p_113449_.getEyePosition();
            Vec3 vec31 = p_113449_.getViewVector(1.0F).scale(p_113450_);
            Vec3 vec32 = vec3.add(vec31);
            AABB aabb = p_113449_.getBoundingBox().expandTowards(vec31).inflate(1.0);
            int i = p_113450_ * p_113450_;
            EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(p_113449_, vec3, vec32, aabb, EntitySelector.CAN_BE_PICKED, i);
            if (entityhitresult == null) {
                return Optional.empty();
            } else {
                return vec3.distanceToSqr(entityhitresult.getLocation()) > i ? Optional.empty() : Optional.of(entityhitresult.getEntity());
            }
        }
    }

    public static void renderFilledUnitCube(
        PoseStack p_312607_, MultiBufferSource p_311178_, BlockPos p_311097_, float p_312295_, float p_312800_, float p_309393_, float p_312684_
    ) {
        renderFilledBox(p_312607_, p_311178_, p_311097_, p_311097_.offset(1, 1, 1), p_312295_, p_312800_, p_309393_, p_312684_);
    }

    public static void renderFilledBox(
        PoseStack p_270169_,
        MultiBufferSource p_270417_,
        BlockPos p_270790_,
        BlockPos p_270610_,
        float p_270515_,
        float p_270494_,
        float p_270869_,
        float p_270844_
    ) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera.isInitialized()) {
            Vec3 vec3 = camera.getPosition().reverse();
            AABB aabb = AABB.encapsulatingFullBlocks(p_270790_, p_270610_).move(vec3);
            renderFilledBox(p_270169_, p_270417_, aabb, p_270515_, p_270494_, p_270869_, p_270844_);
        }
    }

    public static void renderFilledBox(
        PoseStack p_270877_,
        MultiBufferSource p_270925_,
        BlockPos p_270480_,
        float p_270569_,
        float p_270315_,
        float p_270182_,
        float p_270862_,
        float p_270973_
    ) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera.isInitialized()) {
            Vec3 vec3 = camera.getPosition().reverse();
            AABB aabb = new AABB(p_270480_).move(vec3).inflate(p_270569_);
            renderFilledBox(p_270877_, p_270925_, aabb, p_270315_, p_270182_, p_270862_, p_270973_);
        }
    }

    public static void renderFilledBox(
        PoseStack p_271017_, MultiBufferSource p_270356_, AABB p_270833_, float p_270850_, float p_270249_, float p_270654_, float p_270476_
    ) {
        renderFilledBox(
            p_271017_,
            p_270356_,
            p_270833_.minX,
            p_270833_.minY,
            p_270833_.minZ,
            p_270833_.maxX,
            p_270833_.maxY,
            p_270833_.maxZ,
            p_270850_,
            p_270249_,
            p_270654_,
            p_270476_
        );
    }

    public static void renderFilledBox(
        PoseStack p_270616_,
        MultiBufferSource p_270769_,
        double p_270653_,
        double p_270967_,
        double p_270556_,
        double p_270724_,
        double p_270427_,
        double p_270138_,
        float p_270391_,
        float p_270093_,
        float p_270312_,
        float p_270567_
    ) {
        VertexConsumer vertexconsumer = p_270769_.getBuffer(RenderType.debugFilledBox());
        ShapeRenderer.addChainedFilledBoxVertices(
            p_270616_, vertexconsumer, p_270653_, p_270967_, p_270556_, p_270724_, p_270427_, p_270138_, p_270391_, p_270093_, p_270312_, p_270567_
        );
    }

    public static void renderTextOverBlock(
        PoseStack p_430112_, MultiBufferSource p_424196_, String p_427344_, BlockPos p_431739_, int p_424446_, int p_423013_, float p_427623_
    ) {
        double d0 = 1.3;
        double d1 = 0.2;
        double d2 = p_431739_.getX() + 0.5;
        double d3 = p_431739_.getY() + 1.3 + p_424446_ * 0.2;
        double d4 = p_431739_.getZ() + 0.5;
        renderFloatingText(p_430112_, p_424196_, p_427344_, d2, d3, d4, p_423013_, p_427623_, true, 0.0F, true);
    }

    public static void renderTextOverMob(
        PoseStack p_424986_, MultiBufferSource p_431452_, Entity p_424035_, int p_426811_, String p_425595_, int p_425841_, float p_422725_
    ) {
        double d0 = 2.4;
        double d1 = 0.25;
        double d2 = p_424035_.getBlockX() + 0.5;
        double d3 = p_424035_.getY() + 2.4 + p_426811_ * 0.25;
        double d4 = p_424035_.getBlockZ() + 0.5;
        float f = 0.5F;
        renderFloatingText(p_424986_, p_431452_, p_425595_, d2, d3, d4, p_425841_, p_422725_, false, 0.5F, true);
    }

    public static void renderFloatingText(PoseStack p_270671_, MultiBufferSource p_271023_, String p_270521_, int p_270729_, int p_270562_, int p_270828_, int p_270164_) {
        renderFloatingText(p_270671_, p_271023_, p_270521_, p_270729_ + 0.5, p_270562_ + 0.5, p_270828_ + 0.5, p_270164_);
    }

    public static void renderFloatingText(
        PoseStack p_270905_, MultiBufferSource p_270581_, String p_270305_, double p_270645_, double p_270746_, double p_270364_, int p_270977_
    ) {
        renderFloatingText(p_270905_, p_270581_, p_270305_, p_270645_, p_270746_, p_270364_, p_270977_, 0.02F);
    }

    public static void renderFloatingText(
        PoseStack p_270216_,
        MultiBufferSource p_270684_,
        String p_270564_,
        double p_270935_,
        double p_270856_,
        double p_270908_,
        int p_270180_,
        float p_270685_
    ) {
        renderFloatingText(p_270216_, p_270684_, p_270564_, p_270935_, p_270856_, p_270908_, p_270180_, p_270685_, true, 0.0F, false);
    }

    public static void renderFloatingText(
        PoseStack p_270649_,
        MultiBufferSource p_270695_,
        String p_270703_,
        double p_270942_,
        double p_270292_,
        double p_270885_,
        int p_270956_,
        float p_270657_,
        boolean p_270731_,
        float p_270825_,
        boolean p_270222_
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (camera.isInitialized() && minecraft.getEntityRenderDispatcher().options != null) {
            Font font = minecraft.font;
            double d0 = camera.getPosition().x;
            double d1 = camera.getPosition().y;
            double d2 = camera.getPosition().z;
            p_270649_.pushPose();
            p_270649_.translate((float)(p_270942_ - d0), (float)(p_270292_ - d1) + 0.07F, (float)(p_270885_ - d2));
            p_270649_.mulPose(camera.rotation());
            p_270649_.scale(p_270657_, -p_270657_, p_270657_);
            float f = p_270731_ ? -font.width(p_270703_) / 2.0F : 0.0F;
            f -= p_270825_ / p_270657_;
            font.drawInBatch(
                p_270703_,
                f,
                0.0F,
                p_270956_,
                false,
                p_270649_.last().pose(),
                p_270695_,
                p_270222_ ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                0,
                15728880
            );
            p_270649_.popPose();
        }
    }

    private static Vec3 mixColor(float p_362317_) {
        float f = 5.99999F;
        int i = (int)(Mth.clamp(p_362317_, 0.0F, 1.0F) * 5.99999F);
        float f1 = p_362317_ * 5.99999F - i;

        return switch (i) {
            case 0 -> new Vec3(1.0, f1, 0.0);
            case 1 -> new Vec3(1.0F - f1, 1.0, 0.0);
            case 2 -> new Vec3(0.0, 1.0, f1);
            case 3 -> new Vec3(0.0, 1.0 - f1, 1.0);
            case 4 -> new Vec3(f1, 0.0, 1.0);
            case 5 -> new Vec3(1.0, 0.0, 1.0 - f1);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    private static Vec3 shiftHue(float p_366349_, float p_365255_, float p_365397_, float p_365478_) {
        Vec3 vec3 = mixColor(p_365478_).scale(p_366349_);
        Vec3 vec31 = mixColor((p_365478_ + 0.33333334F) % 1.0F).scale(p_365255_);
        Vec3 vec32 = mixColor((p_365478_ + 0.6666667F) % 1.0F).scale(p_365397_);
        Vec3 vec33 = vec3.add(vec31).add(vec32);
        double d0 = Math.max(Math.max(1.0, vec33.x), Math.max(vec33.y, vec33.z));
        return new Vec3(vec33.x / d0, vec33.y / d0, vec33.z / d0);
    }

    public static void renderVoxelShape(
        PoseStack p_360817_,
        VertexConsumer p_363492_,
        VoxelShape p_363714_,
        double p_364187_,
        double p_365344_,
        double p_366898_,
        float p_369234_,
        float p_367922_,
        float p_367674_,
        float p_361835_,
        boolean p_364311_
    ) {
        List<AABB> list = p_363714_.toAabbs();
        if (!list.isEmpty()) {
            int i = p_364311_ ? list.size() : list.size() * 8;
            ShapeRenderer.renderShape(
                p_360817_, p_363492_, Shapes.create(list.get(0)), p_364187_, p_365344_, p_366898_, ARGB.colorFromFloat(p_361835_, p_369234_, p_367922_, p_367674_)
            );

            for (int j = 1; j < list.size(); j++) {
                AABB aabb = list.get(j);
                float f = (float)j / i;
                Vec3 vec3 = shiftHue(p_369234_, p_367922_, p_367674_, f);
                ShapeRenderer.renderShape(
                    p_360817_,
                    p_363492_,
                    Shapes.create(aabb),
                    p_364187_,
                    p_365344_,
                    p_366898_,
                    ARGB.colorFromFloat(p_361835_, (float)vec3.x, (float)vec3.y, (float)vec3.z)
                );
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface SimpleDebugRenderer {
        void render(
            PoseStack p_113507_,
            MultiBufferSource p_113508_,
            double p_113509_,
            double p_113510_,
            double p_113511_,
            DebugValueAccess p_424575_,
            Frustum p_431256_
        );
    }
}