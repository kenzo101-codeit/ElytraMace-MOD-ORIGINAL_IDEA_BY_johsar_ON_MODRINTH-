package net.minecraft.world.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecart extends VehicleEntity implements net.minecraftforge.common.extensions.IForgeAbstractMinecart {
    private static final Vec3 LOWERED_PASSENGER_ATTACHMENT = new Vec3(0.0, 0.0, 0.0);
    private static final EntityDataAccessor<Optional<BlockState>> DATA_ID_CUSTOM_DISPLAY_BLOCK = SynchedEntityData.defineId(
        AbstractMinecart.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
    );
    private static final EntityDataAccessor<Integer> DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(
        Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1)
    );
    protected static final float WATER_SLOWDOWN_FACTOR = 0.95F;
    private static final boolean DEFAULT_FLIPPED_ROTATION = false;
    private boolean onRails;
    private boolean flipped = false;
    private final MinecartBehavior behavior;
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = Maps.newEnumMap(
        Util.make(
            () -> {
                Vec3i vec3i = Direction.WEST.getUnitVec3i();
                Vec3i vec3i1 = Direction.EAST.getUnitVec3i();
                Vec3i vec3i2 = Direction.NORTH.getUnitVec3i();
                Vec3i vec3i3 = Direction.SOUTH.getUnitVec3i();
                Vec3i vec3i4 = vec3i.below();
                Vec3i vec3i5 = vec3i1.below();
                Vec3i vec3i6 = vec3i2.below();
                Vec3i vec3i7 = vec3i3.below();
                return ImmutableMap.of(
                    RailShape.NORTH_SOUTH,
                    Pair.of(vec3i2, vec3i3),
                    RailShape.EAST_WEST,
                    Pair.of(vec3i, vec3i1),
                    RailShape.ASCENDING_EAST,
                    Pair.of(vec3i4, vec3i1),
                    RailShape.ASCENDING_WEST,
                    Pair.of(vec3i, vec3i5),
                    RailShape.ASCENDING_NORTH,
                    Pair.of(vec3i2, vec3i7),
                    RailShape.ASCENDING_SOUTH,
                    Pair.of(vec3i6, vec3i3),
                    RailShape.SOUTH_EAST,
                    Pair.of(vec3i3, vec3i1),
                    RailShape.SOUTH_WEST,
                    Pair.of(vec3i3, vec3i),
                    RailShape.NORTH_WEST,
                    Pair.of(vec3i2, vec3i),
                    RailShape.NORTH_EAST,
                    Pair.of(vec3i2, vec3i1)
                );
            }
        )
    );
    private boolean canBePushed = true;

    protected AbstractMinecart(EntityType<?> p_38087_, Level p_38088_) {
        super(p_38087_, p_38088_);
        this.blocksBuilding = true;
        this.behavior = behaviorFactory.apply(this, p_38088_);
    }

    protected AbstractMinecart(EntityType<?> p_38090_, Level p_38091_, double p_38092_, double p_38093_, double p_38094_) {
        this(p_38090_, p_38091_);
        this.setInitialPos(p_38092_, p_38093_, p_38094_);
    }

    public void setInitialPos(double p_364838_, double p_369805_, double p_367256_) {
        this.setPos(p_364838_, p_369805_, p_367256_);
        this.xo = p_364838_;
        this.yo = p_369805_;
        this.zo = p_367256_;
    }

    @Nullable
    public static <T extends AbstractMinecart> T createMinecart(
        Level p_368792_,
        double p_38121_,
        double p_38122_,
        double p_38123_,
        EntityType<T> p_363374_,
        EntitySpawnReason p_365925_,
        ItemStack p_311363_,
        @Nullable Player p_310754_
    ) {
        T t = (T)p_363374_.create(p_368792_, p_365925_);
        if (t != null) {
            t.setInitialPos(p_38121_, p_38122_, p_38123_);
            EntityType.createDefaultStackConfig(p_368792_, p_311363_, p_310754_).accept(t);
            if (t.getBehavior() instanceof NewMinecartBehavior newminecartbehavior) {
                BlockPos blockpos = t.getCurrentBlockPosOrRailBelow();
                BlockState blockstate = p_368792_.getBlockState(blockpos);
                newminecartbehavior.adjustToRails(blockpos, blockstate, true);
            }
        }

        return t;
    }

    public MinecartBehavior getBehavior() {
        return this.behavior;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_333316_) {
        super.defineSynchedData(p_333316_);
        p_333316_.define(DATA_ID_CUSTOM_DISPLAY_BLOCK, Optional.empty());
        p_333316_.define(DATA_ID_DISPLAY_OFFSET, this.getDefaultDisplayOffset());
    }

    @Override
    public boolean canCollideWith(Entity p_38168_) {
        return AbstractBoat.canVehicleCollide(this, p_38168_);
    }

    @Override
    public boolean isPushable() {
        return canBePushed;
    }

    @Override
    public Vec3 getRelativePortalPosition(Direction.Axis p_38132_, BlockUtil.FoundRectangle p_38133_) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(p_38132_, p_38133_));
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity p_300806_, EntityDimensions p_300201_, float p_299127_) {
        boolean flag = p_300806_ instanceof Villager || p_300806_ instanceof WanderingTrader;
        return flag ? LOWERED_PASSENGER_ATTACHMENT : super.getPassengerAttachmentPoint(p_300806_, p_300201_, p_299127_);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_38145_) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(p_38145_);
        } else {
            int[][] aint = DismountHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            ImmutableList<Pose> immutablelist = p_38145_.getDismountPoses();

            for (Pose pose : immutablelist) {
                EntityDimensions entitydimensions = p_38145_.getDimensions(pose);
                float f = Math.min(entitydimensions.width(), 1.0F) / 2.0F;

                for (int i : POSE_DISMOUNT_HEIGHTS.get(pose)) {
                    for (int[] aint1 : aint) {
                        blockpos$mutableblockpos.set(blockpos.getX() + aint1[0], blockpos.getY() + i, blockpos.getZ() + aint1[1]);
                        double d0 = this.level()
                            .getBlockFloorHeight(
                                DismountHelper.nonClimbableShape(this.level(), blockpos$mutableblockpos),
                                () -> DismountHelper.nonClimbableShape(this.level(), blockpos$mutableblockpos.below())
                            );
                        if (DismountHelper.isBlockFloorValid(d0)) {
                            AABB aabb = new AABB(-f, 0.0, -f, f, entitydimensions.height(), f);
                            Vec3 vec3 = Vec3.upFromBottomCenterOf(blockpos$mutableblockpos, d0);
                            if (DismountHelper.canDismountTo(this.level(), p_38145_, aabb.move(vec3))) {
                                p_38145_.setPose(pose);
                                return vec3;
                            }
                        }
                    }
                }
            }

            double d1 = this.getBoundingBox().maxY;
            blockpos$mutableblockpos.set(blockpos.getX(), d1, blockpos.getZ());

            for (Pose pose1 : immutablelist) {
                double d2 = p_38145_.getDimensions(pose1).height();
                int j = Mth.ceil(d1 - blockpos$mutableblockpos.getY() + d2);
                double d3 = DismountHelper.findCeilingFrom(
                    blockpos$mutableblockpos, j, p_421931_ -> this.level().getBlockState(p_421931_).getCollisionShape(this.level(), p_421931_)
                );
                if (d1 + d2 <= d3) {
                    p_38145_.setPose(pose1);
                    break;
                }
            }

            return super.getDismountLocationForPassenger(p_38145_);
        }
    }

    @Override
    protected float getBlockSpeedFactor() {
        BlockState blockstate = this.level().getBlockState(this.blockPosition());
        return blockstate.is(BlockTags.RAILS) ? 1.0F : super.getBlockSpeedFactor();
    }

    @Override
    public void animateHurt(float p_265349_) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    public static Pair<Vec3i, Vec3i> exits(RailShape p_38126_) {
        return EXITS.get(p_38126_);
    }

    @Override
    public Direction getMotionDirection() {
        return this.behavior.getMotionDirection();
    }

    @Override
    protected double getDefaultGravity() {
        return this.isInWater() ? 0.005 : 0.04;
    }

    @Override
    public void tick() {
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        this.checkBelowWorld();
        this.handlePortal();
        this.behavior.tick();
        this.updateInWaterStateAndDoFluidPushing();
        if (this.isInLava()) {
            this.lavaIgnite();
            this.lavaHurt();
            this.fallDistance *= 0.5;
        }

        this.firstTick = false;
    }

    public boolean isFirstTick() {
        return this.firstTick;
    }

    public BlockPos getCurrentBlockPosOrRailBelow() {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        if (useExperimentalMovement(this.level())) {
            double d0 = this.getY() - 0.1 - 1.0E-5F;
            if (this.level().getBlockState(BlockPos.containing(i, d0, k)).is(BlockTags.RAILS)) {
                j = Mth.floor(d0);
            }
        } else if (this.level().getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            j--;
        }

        return new BlockPos(i, j, k);
    }

    protected double getMaxSpeed(ServerLevel p_368180_) {
        return this.behavior.getMaxSpeed(p_368180_);
    }

    public void activateMinecart(int p_38111_, int p_38112_, int p_38113_, boolean p_38114_) {
    }

    @Override
    public void lerpPositionAndRotationStep(int p_363253_, double p_361925_, double p_362778_, double p_361683_, double p_360914_, double p_361120_) {
        super.lerpPositionAndRotationStep(p_363253_, p_361925_, p_362778_, p_361683_, p_360914_, p_361120_);
    }

    @Override
    public void applyGravity() {
        super.applyGravity();
    }

    @Override
    public void reapplyPosition() {
        super.reapplyPosition();
    }

    @Override
    public boolean updateInWaterStateAndDoFluidPushing() {
        return super.updateInWaterStateAndDoFluidPushing();
    }

    @Override
    public Vec3 getKnownMovement() {
        return this.behavior.getKnownMovement(super.getKnownMovement());
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.behavior.getInterpolation();
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket p_409401_) {
        super.recreateFromPacket(p_409401_);
        this.behavior.lerpMotion(this.getDeltaMovement());
    }

    @Override
    public void lerpMotion(Vec3 p_430606_) {
        this.behavior.lerpMotion(p_430606_);
    }

    protected void moveAlongTrack(ServerLevel p_367889_) {
        this.behavior.moveAlongTrack(p_367889_);
    }

    protected void comeOffTrack(ServerLevel p_365684_) {
        double d0 = this.onGround() ? this.getMaxSpeed(p_365684_) : getMaxSpeedAirLateral();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(Mth.clamp(vec3.x, -d0, d0), vec3.y, Mth.clamp(vec3.z, -d0, d0));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        }

        if (getMaxSpeedAirVertical() > 0 && getDeltaMovement().y > getMaxSpeedAirVertical()) {
            if (Math.abs(getDeltaMovement().x) < 0.3f && Math.abs(getDeltaMovement().z) < 0.3f) {
                setDeltaMovement(new Vec3(getDeltaMovement().x, 0.15f, getDeltaMovement().z));
            } else {
                setDeltaMovement(new Vec3(getDeltaMovement().x, getMaxSpeedAirVertical(), getDeltaMovement().z));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(getDragAir()));
        }
    }

    protected double makeStepAlongTrack(BlockPos p_368364_, RailShape p_364631_, double p_369237_) {
        return this.behavior.stepAlongTrack(p_368364_, p_364631_, p_369237_);
    }

    @Override
    public void move(MoverType p_361237_, Vec3 p_364999_) {
        if (useExperimentalMovement(this.level())) {
            Vec3 vec3 = this.position().add(p_364999_);
            super.move(p_361237_, p_364999_);
            boolean flag = this.behavior.pushAndPickupEntities();
            if (flag) {
                super.move(p_361237_, vec3.subtract(this.position()));
            }

            if (p_361237_.equals(MoverType.PISTON)) {
                this.onRails = false;
            }
        } else {
            super.move(p_361237_, p_364999_);
            this.applyEffectsFromBlocks();
        }
    }

    @Override
    public void applyEffectsFromBlocks() {
        if (useExperimentalMovement(this.level())) {
            super.applyEffectsFromBlocks();
        } else {
            this.applyEffectsFromBlocks(this.position(), this.position());
            this.clearMovementThisTick();
        }
    }

    @Override
    public boolean isOnRails() {
        return this.onRails;
    }

    public void setOnRails(boolean p_361351_) {
        this.onRails = p_361351_;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public void setFlipped(boolean p_361801_) {
        this.flipped = p_361801_;
    }

    public Vec3 getRedstoneDirection(BlockPos p_369874_) {
        BlockState blockstate = this.level().getBlockState(p_369874_);
        if (blockstate.is(Blocks.POWERED_RAIL) && blockstate.getValue(PoweredRailBlock.POWERED)) {
            RailShape railshape = ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.level(), p_369874_, this);
            if (railshape == RailShape.EAST_WEST) {
                if (this.isRedstoneConductor(p_369874_.west())) {
                    return new Vec3(1.0, 0.0, 0.0);
                }

                if (this.isRedstoneConductor(p_369874_.east())) {
                    return new Vec3(-1.0, 0.0, 0.0);
                }
            } else if (railshape == RailShape.NORTH_SOUTH) {
                if (this.isRedstoneConductor(p_369874_.north())) {
                    return new Vec3(0.0, 0.0, 1.0);
                }

                if (this.isRedstoneConductor(p_369874_.south())) {
                    return new Vec3(0.0, 0.0, -1.0);
                }
            }

            return Vec3.ZERO;
        } else {
            return Vec3.ZERO;
        }
    }

    public boolean isRedstoneConductor(BlockPos p_38130_) {
        return this.level().getBlockState(p_38130_).isRedstoneConductor(this.level(), p_38130_);
    }

    protected Vec3 applyNaturalSlowdown(Vec3 p_368399_) {
        double d0 = this.behavior.getSlowdownFactor();
        Vec3 vec3 = p_368399_.multiply(d0, 0.0, d0);
        if (this.isInWater()) {
            vec3 = vec3.scale(0.95F);
        }

        return vec3;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput p_409882_) {
        this.setCustomDisplayBlockState(p_409882_.read("DisplayState", BlockState.CODEC));
        this.setDisplayOffset(p_409882_.getIntOr("DisplayOffset", this.getDefaultDisplayOffset()));
        this.flipped = p_409882_.getBooleanOr("FlippedRotation", false);
        this.firstTick = p_409882_.getBooleanOr("HasTicked", false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput p_406211_) {
        this.getCustomDisplayBlockState().ifPresent(p_405575_ -> p_406211_.store("DisplayState", BlockState.CODEC, p_405575_));
        int i = this.getDisplayOffset();
        if (i != this.getDefaultDisplayOffset()) {
            p_406211_.putInt("DisplayOffset", i);
        }

        p_406211_.putBoolean("FlippedRotation", this.flipped);
        p_406211_.putBoolean("HasTicked", this.firstTick);
    }

    @Override
    public void push(Entity p_38165_) {
        if (!this.level().isClientSide()) {
            if (!p_38165_.noPhysics && !this.noPhysics) {
                if (!this.hasPassenger(p_38165_)) {
                    double d0 = p_38165_.getX() - this.getX();
                    double d1 = p_38165_.getZ() - this.getZ();
                    double d2 = d0 * d0 + d1 * d1;
                    if (d2 >= 1.0E-4F) {
                        d2 = Math.sqrt(d2);
                        d0 /= d2;
                        d1 /= d2;
                        double d3 = 1.0 / d2;
                        if (d3 > 1.0) {
                            d3 = 1.0;
                        }

                        d0 *= d3;
                        d1 *= d3;
                        d0 *= 0.1F;
                        d1 *= 0.1F;
                        d0 *= 0.5;
                        d1 *= 0.5;
                        if (p_38165_ instanceof AbstractMinecart abstractminecart) {
                            this.pushOtherMinecart(abstractminecart, d0, d1);
                        } else {
                            this.push(-d0, 0.0, -d1);
                            p_38165_.push(d0 / 4.0, 0.0, d1 / 4.0);
                        }
                    }
                }
            }
        }
    }

    private void pushOtherMinecart(AbstractMinecart p_363124_, double p_365746_, double p_363827_) {
        double d0;
        double d1;
        if (useExperimentalMovement(this.level())) {
            d0 = this.getDeltaMovement().x;
            d1 = this.getDeltaMovement().z;
        } else {
            d0 = p_363124_.getX() - this.getX();
            d1 = p_363124_.getZ() - this.getZ();
        }

        Vec3 vec3 = new Vec3(d0, 0.0, d1).normalize();
        Vec3 vec31 = new Vec3(Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)), 0.0, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)))
            .normalize();
        double d2 = Math.abs(vec3.dot(vec31));
        if (!(d2 < 0.8F) || useExperimentalMovement(this.level())) {
            Vec3 vec32 = this.getDeltaMovement();
            Vec3 vec33 = p_363124_.getDeltaMovement();
            if (p_363124_.isFurnace() && !this.isFurnace()) {
                this.setDeltaMovement(vec32.multiply(0.2, 1.0, 0.2));
                this.push(vec33.x - p_365746_, 0.0, vec33.z - p_363827_);
                p_363124_.setDeltaMovement(vec33.multiply(0.95, 1.0, 0.95));
            } else if (!p_363124_.isFurnace() && this.isFurnace()) {
                p_363124_.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2));
                p_363124_.push(vec32.x + p_365746_, 0.0, vec32.z + p_363827_);
                this.setDeltaMovement(vec32.multiply(0.95, 1.0, 0.95));
            } else {
                double d3 = (vec33.x + vec32.x) / 2.0;
                double d4 = (vec33.z + vec32.z) / 2.0;
                this.setDeltaMovement(vec32.multiply(0.2, 1.0, 0.2));
                this.push(d3 - p_365746_, 0.0, d4 - p_363827_);
                p_363124_.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2));
                p_363124_.push(d3 + p_365746_, 0.0, d4 + p_363827_);
            }
        }
    }

    public BlockState getDisplayBlockState() {
        return this.getCustomDisplayBlockState().orElseGet(this::getDefaultDisplayBlockState);
    }

    private Optional<BlockState> getCustomDisplayBlockState() {
        return this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY_BLOCK);
    }

    public BlockState getDefaultDisplayBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    public int getDisplayOffset() {
        return this.getEntityData().get(DATA_ID_DISPLAY_OFFSET);
    }

    public int getDefaultDisplayOffset() {
        return 6;
    }

    public void setCustomDisplayBlockState(Optional<BlockState> p_393882_) {
        this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY_BLOCK, p_393882_);
    }

    public void setDisplayOffset(int p_38175_) {
        this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, p_38175_);
    }

    public static boolean useExperimentalMovement(Level p_368699_) {
        return p_368699_.enabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
    }

    @Override
    public abstract ItemStack getPickResult();

    public boolean isRideable() {
        return false;
    }

    public boolean isFurnace() {
        return false;
    }

    private static java.util.function.BiFunction<AbstractMinecart, Level, MinecartBehavior> behaviorFactory = (cart, level) -> {
        return useExperimentalMovement(level) ? new NewMinecartBehavior(cart) : new OldMinecartBehavior(cart);
    };
    public static void setBehaviorFactory(java.util.function.BiFunction<AbstractMinecart, Level, MinecartBehavior> factory) { behaviorFactory = factory; }
    public static java.util.function.BiFunction<AbstractMinecart, Level, MinecartBehavior> getBehaviorFactory() { return behaviorFactory; }

              private boolean canUseRail = true;
    @Override public  boolean canUseRail() { return canUseRail; }
    @Override public  void    setCanUseRail(boolean value) { this.canUseRail = value; }
              private float    currentSpeedCapOnRail = getMaxCartSpeedOnRail();
    @Override public  float getCurrentCartSpeedCapOnRail() { return currentSpeedCapOnRail; }
    @Override public  void  setCurrentCartSpeedCapOnRail(float value) { currentSpeedCapOnRail = Math.min(value, getMaxCartSpeedOnRail()); }
              private Float    maxSpeedAirLateral = null;
    @Override public  float getMaxSpeedAirLateral() { return maxSpeedAirLateral == null ? (float)this.getMaxSpeed((ServerLevel)this.level()) : maxSpeedAirLateral; }
    @Override public  void  setMaxSpeedAirLateral(float value) { maxSpeedAirLateral = value; }
              private float    maxSpeedAirVertical = DEFAULT_MAX_SPEED_AIR_VERTICAL;
    @Override public  float getMaxSpeedAirVertical() { return maxSpeedAirVertical; }
    @Override public  void  setMaxSpeedAirVertical(float value) { maxSpeedAirVertical = value; }
              private double    dragAir = DEFAULT_AIR_DRAG;
    @Override public  double getDragAir() { return dragAir; }
    @Override public  void   setDragAir(double value) { dragAir = value; }
    @Override
    public double getMaxSpeedWithRail() { //Non-default because getMaximumSpeed is protected
        if (!canUseRail()) {
            return getMaxSpeed((ServerLevel)this.level());
        }
        BlockPos pos = this.getCurrentRailPosition();
        BlockState state = this.level().getBlockState(pos);

        if (!state.is(BlockTags.RAILS)) {
            return getMaxSpeed((ServerLevel)this.level());
        }

        float railMaxSpeed = ((BaseRailBlock)state.getBlock()).getRailMaxSpeed(state, this.level(), pos, this);
        return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
    }
}
