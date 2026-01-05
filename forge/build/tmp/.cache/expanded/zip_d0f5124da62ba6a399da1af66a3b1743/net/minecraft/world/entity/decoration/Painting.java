package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Painting extends HangingEntity {
    private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(Painting.class, EntityDataSerializers.PAINTING_VARIANT);
    public static final float DEPTH = 0.0625F;

    public Painting(EntityType<? extends Painting> p_31904_, Level p_31905_) {
        super(p_31904_, p_31905_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_334800_) {
        super.defineSynchedData(p_334800_);
        p_334800_.define(DATA_PAINTING_VARIANT_ID, VariantUtils.getAny(this.registryAccess(), Registries.PAINTING_VARIANT));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_218896_) {
        super.onSyncedDataUpdated(p_218896_);
        if (DATA_PAINTING_VARIANT_ID.equals(p_218896_)) {
            this.recalculateBoundingBox();
        }
    }

    private void setVariant(Holder<PaintingVariant> p_218892_) {
        this.entityData.set(DATA_PAINTING_VARIANT_ID, p_218892_);
    }

    public Holder<PaintingVariant> getVariant() {
        return this.entityData.get(DATA_PAINTING_VARIANT_ID);
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> p_392432_) {
        return p_392432_ == DataComponents.PAINTING_VARIANT ? castComponentValue((DataComponentType<T>)p_392432_, this.getVariant()) : super.get(p_392432_);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter p_393374_) {
        this.applyImplicitComponentIfPresent(p_393374_, DataComponents.PAINTING_VARIANT);
        super.applyImplicitComponents(p_393374_);
    }

    @Override
    protected <T> boolean applyImplicitComponent(DataComponentType<T> p_391540_, T p_391174_) {
        if (p_391540_ == DataComponents.PAINTING_VARIANT) {
            this.setVariant(castComponentValue(DataComponents.PAINTING_VARIANT, p_391174_));
            return true;
        } else {
            return super.applyImplicitComponent(p_391540_, p_391174_);
        }
    }

    public static Optional<Painting> create(Level p_218888_, BlockPos p_218889_, Direction p_218890_) {
        Painting painting = new Painting(p_218888_, p_218889_);
        List<Holder<PaintingVariant>> list = new ArrayList<>();
        p_218888_.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            painting.setDirection(p_218890_);
            list.removeIf(p_390674_ -> {
                painting.setVariant((Holder<PaintingVariant>)p_390674_);
                return !painting.survives();
            });
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                int i = list.stream().mapToInt(Painting::variantArea).max().orElse(0);
                list.removeIf(p_218883_ -> variantArea((Holder<PaintingVariant>)p_218883_) < i);
                Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, painting.random);
                if (optional.isEmpty()) {
                    return Optional.empty();
                } else {
                    painting.setVariant(optional.get());
                    painting.setDirection(p_218890_);
                    return Optional.of(painting);
                }
            }
        }
    }

    private static int variantArea(Holder<PaintingVariant> p_218899_) {
        return p_218899_.value().area();
    }

    private Painting(Level p_218874_, BlockPos p_218875_) {
        super(EntityType.PAINTING, p_218874_, p_218875_);
    }

    public Painting(Level p_218877_, BlockPos p_218878_, Direction p_218879_, Holder<PaintingVariant> p_218880_) {
        this(p_218877_, p_218878_);
        this.setVariant(p_218880_);
        this.setDirection(p_218879_);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput p_406503_) {
        p_406503_.store("facing", Direction.LEGACY_ID_CODEC_2D, this.getDirection());
        super.addAdditionalSaveData(p_406503_);
        VariantUtils.writeVariant(p_406503_, this.getVariant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput p_408678_) {
        Direction direction = p_408678_.read("facing", Direction.LEGACY_ID_CODEC_2D).orElse(Direction.SOUTH);
        super.readAdditionalSaveData(p_408678_);
        this.setDirection(direction);
        VariantUtils.readVariant(p_408678_, Registries.PAINTING_VARIANT).ifPresent(this::setVariant);
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos p_344620_, Direction p_345489_) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(p_344620_).relative(p_345489_, -0.46875);
        PaintingVariant paintingvariant = this.getVariant().value();
        double d0 = this.offsetForPaintingSize(paintingvariant.width());
        double d1 = this.offsetForPaintingSize(paintingvariant.height());
        Direction direction = p_345489_.getCounterClockWise();
        Vec3 vec31 = vec3.relative(direction, d0).relative(Direction.UP, d1);
        Direction.Axis direction$axis = p_345489_.getAxis();
        double d2 = direction$axis == Direction.Axis.X ? 0.0625 : paintingvariant.width();
        double d3 = paintingvariant.height();
        double d4 = direction$axis == Direction.Axis.Z ? 0.0625 : paintingvariant.width();
        return AABB.ofSize(vec31, d2, d3, d4);
    }

    private double offsetForPaintingSize(int p_344506_) {
        return p_344506_ % 2 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void dropItem(ServerLevel p_364635_, @Nullable Entity p_31925_) {
        if (p_364635_.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (!(p_31925_ instanceof Player player && player.hasInfiniteMaterials())) {
                this.spawnAtLocation(p_364635_, Items.PAINTING);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void snapTo(double p_391234_, double p_393145_, double p_396829_, float p_392159_, float p_396472_) {
        this.setPos(p_391234_, p_393145_, p_396829_);
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_345195_) {
        return new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket p_218894_) {
        super.recreateFromPacket(p_218894_);
        this.setDirection(Direction.from3DDataValue(p_218894_.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.PAINTING);
    }
}