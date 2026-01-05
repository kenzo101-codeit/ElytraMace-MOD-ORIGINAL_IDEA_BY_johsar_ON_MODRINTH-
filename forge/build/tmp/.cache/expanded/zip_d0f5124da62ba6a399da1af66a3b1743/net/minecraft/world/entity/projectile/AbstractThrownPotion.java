package net.minecraft.world.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class AbstractThrownPotion extends ThrowableItemProjectile {
    public static final double SPLASH_RANGE = 4.0;
    protected static final double SPLASH_RANGE_SQ = 16.0;
    public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = p_421907_ -> p_421907_.isSensitiveToWater() || p_421907_.isOnFire();

    public AbstractThrownPotion(EntityType<? extends AbstractThrownPotion> p_396168_, Level p_391569_) {
        super(p_396168_, p_391569_);
    }

    public AbstractThrownPotion(EntityType<? extends AbstractThrownPotion> p_395859_, Level p_392178_, LivingEntity p_396219_, ItemStack p_391550_) {
        super(p_395859_, p_396219_, p_392178_, p_391550_);
    }

    public AbstractThrownPotion(
        EntityType<? extends AbstractThrownPotion> p_394877_, Level p_392727_, double p_391358_, double p_391356_, double p_392694_, ItemStack p_392268_
    ) {
        super(p_394877_, p_391358_, p_391356_, p_392694_, p_392727_, p_392268_);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected void onHitBlock(BlockHitResult p_393510_) {
        super.onHitBlock(p_393510_);
        if (!this.level().isClientSide()) {
            ItemStack itemstack = this.getItem();
            Direction direction = p_393510_.getDirection();
            BlockPos blockpos = p_393510_.getBlockPos();
            BlockPos blockpos1 = blockpos.relative(direction);
            PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potioncontents.is(Potions.WATER)) {
                this.dowseFire(blockpos1);
                this.dowseFire(blockpos1.relative(direction.getOpposite()));

                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.dowseFire(blockpos1.relative(direction1));
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult p_396449_) {
        super.onHit(p_396449_);
        if (this.level() instanceof ServerLevel serverlevel) {
            ItemStack itemstack = this.getItem();
            PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potioncontents.is(Potions.WATER)) {
                this.onHitAsWater(serverlevel);
            } else if (potioncontents.hasEffects()) {
                this.onHitAsPotion(serverlevel, itemstack, p_396449_);
            }

            int i = potioncontents.potion().isPresent() && potioncontents.potion().get().value().hasInstantEffects() ? 2007 : 2002;
            serverlevel.levelEvent(i, this.blockPosition(), potioncontents.getColor());
            this.discard();
        }
    }

    private void onHitAsWater(ServerLevel p_396129_) {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, aabb, WATER_SENSITIVE_OR_ON_FIRE)) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0) {
                if (livingentity.isSensitiveToWater()) {
                    livingentity.hurtServer(p_396129_, this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }

                if (livingentity.isOnFire() && livingentity.isAlive()) {
                    livingentity.extinguishFire();
                }
            }
        }

        for (Axolotl axolotl : this.level().getEntitiesOfClass(Axolotl.class, aabb)) {
            axolotl.rehydrate();
        }
    }

    protected abstract void onHitAsPotion(ServerLevel p_392789_, ItemStack p_392118_, HitResult p_410466_);

    private void dowseFire(BlockPos p_393142_) {
        BlockState blockstate = this.level().getBlockState(p_393142_);
        if (blockstate.is(BlockTags.FIRE)) {
            this.level().destroyBlock(p_393142_, false, this);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish(null, blockstate, this.level(), p_393142_);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            this.level().levelEvent(null, 1009, p_393142_, 0);
            CampfireBlock.dowse(this.getOwner(), this.level(), p_393142_, blockstate);
            this.level().setBlockAndUpdate(p_393142_, blockstate.setValue(CampfireBlock.LIT, false));
        }
    }

    @Override
    public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity p_395530_, DamageSource p_393426_) {
        double d0 = p_395530_.position().x - this.position().x;
        double d1 = p_395530_.position().z - this.position().z;
        return DoubleDoubleImmutablePair.of(d0, d1);
    }
}