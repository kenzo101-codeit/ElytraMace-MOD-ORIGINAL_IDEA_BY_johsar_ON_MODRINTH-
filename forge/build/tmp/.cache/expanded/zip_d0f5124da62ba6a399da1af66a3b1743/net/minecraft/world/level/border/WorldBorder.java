package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder extends SavedData {
    public static final double MAX_SIZE = 5.999997E7F;
    public static final double MAX_CENTER_COORDINATE = 2.9999984E7;
    public static final Codec<WorldBorder> CODEC = WorldBorder.Settings.CODEC.xmap(WorldBorder.Settings::toWorldBorder, WorldBorder.Settings::new);
    public static final SavedDataType<WorldBorder> TYPE = new SavedDataType<>(
        "world_border", p_422194_ -> WorldBorder.Settings.DEFAULT.toWorldBorder(), p_422195_ -> CODEC, DataFixTypes.SAVED_DATA_WORLD_BORDER
    );
    private final List<BorderChangeListener> listeners = Lists.newArrayList();
    double damagePerBlock = 0.2;
    double safeZone = 5.0;
    int warningTime = 15;
    int warningBlocks = 5;
    double centerX;
    double centerZ;
    int absoluteMaxSize = 29999984;
    WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(5.999997E7F);

    public boolean isWithinBounds(BlockPos p_61938_) {
        return this.isWithinBounds(p_61938_.getX(), p_61938_.getZ());
    }

    public boolean isWithinBounds(Vec3 p_343899_) {
        return this.isWithinBounds(p_343899_.x, p_343899_.z);
    }

    public boolean isWithinBounds(ChunkPos p_61928_) {
        return this.isWithinBounds(p_61928_.getMinBlockX(), p_61928_.getMinBlockZ()) && this.isWithinBounds(p_61928_.getMaxBlockX(), p_61928_.getMaxBlockZ());
    }

    public boolean isWithinBounds(AABB p_61936_) {
        return this.isWithinBounds(p_61936_.minX, p_61936_.minZ, p_61936_.maxX - 1.0E-5F, p_61936_.maxZ - 1.0E-5F);
    }

    private boolean isWithinBounds(double p_342617_, double p_344821_, double p_344911_, double p_344145_) {
        return this.isWithinBounds(p_342617_, p_344821_) && this.isWithinBounds(p_344911_, p_344145_);
    }

    public boolean isWithinBounds(double p_156094_, double p_156095_) {
        return this.isWithinBounds(p_156094_, p_156095_, 0.0);
    }

    public boolean isWithinBounds(double p_187563_, double p_187564_, double p_187565_) {
        return p_187563_ >= this.getMinX() - p_187565_
            && p_187563_ < this.getMaxX() + p_187565_
            && p_187564_ >= this.getMinZ() - p_187565_
            && p_187564_ < this.getMaxZ() + p_187565_;
    }

    public BlockPos clampToBounds(BlockPos p_342374_) {
        return this.clampToBounds(p_342374_.getX(), p_342374_.getY(), p_342374_.getZ());
    }

    public BlockPos clampToBounds(Vec3 p_345328_) {
        return this.clampToBounds(p_345328_.x(), p_345328_.y(), p_345328_.z());
    }

    public BlockPos clampToBounds(double p_187570_, double p_187571_, double p_187572_) {
        return BlockPos.containing(this.clampVec3ToBound(p_187570_, p_187571_, p_187572_));
    }

    public Vec3 clampVec3ToBound(Vec3 p_369267_) {
        return this.clampVec3ToBound(p_369267_.x, p_369267_.y, p_369267_.z);
    }

    public Vec3 clampVec3ToBound(double p_363761_, double p_367247_, double p_362240_) {
        return new Vec3(
            Mth.clamp(p_363761_, this.getMinX(), this.getMaxX() - 1.0E-5F), p_367247_, Mth.clamp(p_362240_, this.getMinZ(), this.getMaxZ() - 1.0E-5F)
        );
    }

    public double getDistanceToBorder(Entity p_61926_) {
        return this.getDistanceToBorder(p_61926_.getX(), p_61926_.getZ());
    }

    public VoxelShape getCollisionShape() {
        return this.extent.getCollisionShape();
    }

    public double getDistanceToBorder(double p_61942_, double p_61943_) {
        double d0 = p_61943_ - this.getMinZ();
        double d1 = this.getMaxZ() - p_61943_;
        double d2 = p_61942_ - this.getMinX();
        double d3 = this.getMaxX() - p_61942_;
        double d4 = Math.min(d2, d3);
        d4 = Math.min(d4, d0);
        return Math.min(d4, d1);
    }

    public boolean isInsideCloseToBorder(Entity p_187567_, AABB p_187568_) {
        double d0 = Math.max(Mth.absMax(p_187568_.getXsize(), p_187568_.getZsize()), 1.0);
        return this.getDistanceToBorder(p_187567_) < d0 * 2.0 && this.isWithinBounds(p_187567_.getX(), p_187567_.getZ(), d0);
    }

    public BorderStatus getStatus() {
        return this.extent.getStatus();
    }

    public double getMinX() {
        return this.extent.getMinX();
    }

    public double getMinZ() {
        return this.extent.getMinZ();
    }

    public double getMaxX() {
        return this.extent.getMaxX();
    }

    public double getMaxZ() {
        return this.extent.getMaxZ();
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(double p_61950_, double p_61951_) {
        this.centerX = p_61950_;
        this.centerZ = p_61951_;
        this.extent.onCenterChange();
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetCenter(this, p_61950_, p_61951_);
        }
    }

    public double getSize() {
        return this.extent.getSize();
    }

    public long getLerpTime() {
        return this.extent.getLerpTime();
    }

    public double getLerpTarget() {
        return this.extent.getLerpTarget();
    }

    public void setSize(double p_61918_) {
        this.extent = new WorldBorder.StaticBorderExtent(p_61918_);
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetSize(this, p_61918_);
        }
    }

    public void lerpSizeBetween(double p_61920_, double p_61921_, long p_61922_) {
        this.extent = (WorldBorder.BorderExtent)(p_61920_ == p_61921_
            ? new WorldBorder.StaticBorderExtent(p_61921_)
            : new WorldBorder.MovingBorderExtent(p_61920_, p_61921_, p_61922_));
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onLerpSize(this, p_61920_, p_61921_, p_61922_);
        }
    }

    protected List<BorderChangeListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(BorderChangeListener p_61930_) {
        this.listeners.add(p_61930_);
    }

    public void removeListener(BorderChangeListener p_156097_) {
        this.listeners.remove(p_156097_);
    }

    public void setAbsoluteMaxSize(int p_61924_) {
        this.absoluteMaxSize = p_61924_;
        this.extent.onAbsoluteMaxSizeChange();
    }

    public int getAbsoluteMaxSize() {
        return this.absoluteMaxSize;
    }

    public double getSafeZone() {
        return this.safeZone;
    }

    public void setSafeZone(double p_424052_) {
        this.safeZone = p_424052_;
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetSafeZone(this, p_424052_);
        }
    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double p_61948_) {
        this.damagePerBlock = p_61948_;
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetDamagePerBlock(this, p_61948_);
        }
    }

    public double getLerpSpeed() {
        return this.extent.getLerpSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int p_61945_) {
        this.warningTime = p_61945_;
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetWarningTime(this, p_61945_);
        }
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int p_61953_) {
        this.warningBlocks = p_61953_;
        this.setDirty();

        for (BorderChangeListener borderchangelistener : this.getListeners()) {
            borderchangelistener.onSetWarningBlocks(this, p_61953_);
        }
    }

    public void tick() {
        this.extent = this.extent.update();
    }

    public void applySettings(WorldBorder.Settings p_61932_) {
        this.setCenter(p_61932_.centerX(), p_61932_.centerZ());
        this.setDamagePerBlock(p_61932_.damagePerBlock());
        this.setSafeZone(p_61932_.safeZone());
        this.setWarningBlocks(p_61932_.warningBlocks());
        this.setWarningTime(p_61932_.warningTime());
        if (p_61932_.lerpTime() > 0L) {
            this.lerpSizeBetween(p_61932_.size(), p_61932_.lerpTarget(), p_61932_.lerpTime());
        } else {
            this.setSize(p_61932_.size());
        }
    }

    interface BorderExtent {
        double getMinX();

        double getMaxX();

        double getMinZ();

        double getMaxZ();

        double getSize();

        double getLerpSpeed();

        long getLerpTime();

        double getLerpTarget();

        BorderStatus getStatus();

        void onAbsoluteMaxSizeChange();

        void onCenterChange();

        WorldBorder.BorderExtent update();

        VoxelShape getCollisionShape();
    }

    class MovingBorderExtent implements WorldBorder.BorderExtent {
        private final double from;
        private final double to;
        private final long lerpEnd;
        private final long lerpBegin;
        private final double lerpDuration;

        MovingBorderExtent(final double p_61979_, final double p_61980_, final long p_61981_) {
            this.from = p_61979_;
            this.to = p_61980_;
            this.lerpDuration = p_61981_;
            this.lerpBegin = Util.getMillis();
            this.lerpEnd = this.lerpBegin + p_61981_;
        }

        @Override
        public double getMinX() {
            return Mth.clamp(WorldBorder.this.getCenterX() - this.getSize() / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMinZ() {
            return Mth.clamp(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxX() {
            return Mth.clamp(WorldBorder.this.getCenterX() + this.getSize() / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxZ() {
            return Mth.clamp(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getSize() {
            double d0 = (Util.getMillis() - this.lerpBegin) / this.lerpDuration;
            return d0 < 1.0 ? Mth.lerp(d0, this.from, this.to) : this.to;
        }

        @Override
        public double getLerpSpeed() {
            return Math.abs(this.from - this.to) / (this.lerpEnd - this.lerpBegin);
        }

        @Override
        public long getLerpTime() {
            return this.lerpEnd - Util.getMillis();
        }

        @Override
        public double getLerpTarget() {
            return this.to;
        }

        @Override
        public BorderStatus getStatus() {
            return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
        }

        @Override
        public void onCenterChange() {
        }

        @Override
        public void onAbsoluteMaxSizeChange() {
        }

        @Override
        public WorldBorder.BorderExtent update() {
            if (this.getLerpTime() <= 0L) {
                WorldBorder.this.setDirty();
                return WorldBorder.this.new StaticBorderExtent(this.to);
            } else {
                return this;
            }
        }

        @Override
        public VoxelShape getCollisionShape() {
            return Shapes.join(
                Shapes.INFINITY,
                Shapes.box(
                    Math.floor(this.getMinX()),
                    Double.NEGATIVE_INFINITY,
                    Math.floor(this.getMinZ()),
                    Math.ceil(this.getMaxX()),
                    Double.POSITIVE_INFINITY,
                    Math.ceil(this.getMaxZ())
                ),
                BooleanOp.ONLY_FIRST
            );
        }
    }

    public record Settings(
        double centerX, double centerZ, double damagePerBlock, double safeZone, int warningBlocks, int warningTime, double size, long lerpTime, double lerpTarget
    ) {
        public static final WorldBorder.Settings DEFAULT = new WorldBorder.Settings(0.0, 0.0, 0.2, 5.0, 5, 15, 5.999997E7F, 0L, 0.0);
        public static final Codec<WorldBorder.Settings> CODEC = RecordCodecBuilder.create(
            p_423671_ -> p_423671_.group(
                    Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_x").forGetter(WorldBorder.Settings::centerX),
                    Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_z").forGetter(WorldBorder.Settings::centerZ),
                    Codec.DOUBLE.fieldOf("damage_per_block").forGetter(WorldBorder.Settings::damagePerBlock),
                    Codec.DOUBLE.fieldOf("safe_zone").forGetter(WorldBorder.Settings::safeZone),
                    Codec.INT.fieldOf("warning_blocks").forGetter(WorldBorder.Settings::warningBlocks),
                    Codec.INT.fieldOf("warning_time").forGetter(WorldBorder.Settings::warningTime),
                    Codec.DOUBLE.fieldOf("size").forGetter(WorldBorder.Settings::size),
                    Codec.LONG.fieldOf("lerp_time").forGetter(WorldBorder.Settings::lerpTime),
                    Codec.DOUBLE.fieldOf("lerp_target").forGetter(WorldBorder.Settings::lerpTarget)
                )
                .apply(p_423671_, WorldBorder.Settings::new)
        );

        public Settings(WorldBorder p_62032_) {
            this(
                p_62032_.centerX,
                p_62032_.centerZ,
                p_62032_.damagePerBlock,
                p_62032_.safeZone,
                p_62032_.warningBlocks,
                p_62032_.warningTime,
                p_62032_.extent.getSize(),
                p_62032_.extent.getLerpTime(),
                p_62032_.extent.getLerpTarget()
            );
        }

        public WorldBorder toWorldBorder() {
            WorldBorder worldborder = new WorldBorder();
            worldborder.applySettings(this);
            return worldborder;
        }
    }

    class StaticBorderExtent implements WorldBorder.BorderExtent {
        private final double size;
        private double minX;
        private double minZ;
        private double maxX;
        private double maxZ;
        private VoxelShape shape;

        public StaticBorderExtent(final double p_62059_) {
            this.size = p_62059_;
            this.updateBox();
        }

        @Override
        public double getMinX() {
            return this.minX;
        }

        @Override
        public double getMaxX() {
            return this.maxX;
        }

        @Override
        public double getMinZ() {
            return this.minZ;
        }

        @Override
        public double getMaxZ() {
            return this.maxZ;
        }

        @Override
        public double getSize() {
            return this.size;
        }

        @Override
        public BorderStatus getStatus() {
            return BorderStatus.STATIONARY;
        }

        @Override
        public double getLerpSpeed() {
            return 0.0;
        }

        @Override
        public long getLerpTime() {
            return 0L;
        }

        @Override
        public double getLerpTarget() {
            return this.size;
        }

        private void updateBox() {
            this.minX = Mth.clamp(WorldBorder.this.getCenterX() - this.size / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
            this.minZ = Mth.clamp(WorldBorder.this.getCenterZ() - this.size / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
            this.maxX = Mth.clamp(WorldBorder.this.getCenterX() + this.size / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
            this.maxZ = Mth.clamp(WorldBorder.this.getCenterZ() + this.size / 2.0, -WorldBorder.this.absoluteMaxSize, WorldBorder.this.absoluteMaxSize);
            this.shape = Shapes.join(
                Shapes.INFINITY,
                Shapes.box(
                    Math.floor(this.getMinX()),
                    Double.NEGATIVE_INFINITY,
                    Math.floor(this.getMinZ()),
                    Math.ceil(this.getMaxX()),
                    Double.POSITIVE_INFINITY,
                    Math.ceil(this.getMaxZ())
                ),
                BooleanOp.ONLY_FIRST
            );
        }

        @Override
        public void onAbsoluteMaxSizeChange() {
            this.updateBox();
        }

        @Override
        public void onCenterChange() {
            this.updateBox();
        }

        @Override
        public WorldBorder.BorderExtent update() {
            return this;
        }

        @Override
        public VoxelShape getCollisionShape() {
            return this.shape;
        }
    }
}