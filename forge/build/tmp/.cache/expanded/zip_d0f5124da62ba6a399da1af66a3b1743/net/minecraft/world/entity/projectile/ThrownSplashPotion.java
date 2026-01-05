package net.minecraft.world.entity.projectile;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class ThrownSplashPotion extends AbstractThrownPotion {
    public ThrownSplashPotion(EntityType<? extends ThrownSplashPotion> p_396503_, Level p_393884_) {
        super(p_396503_, p_393884_);
    }

    public ThrownSplashPotion(Level p_397891_, LivingEntity p_395910_, ItemStack p_397906_) {
        super(EntityType.SPLASH_POTION, p_397891_, p_395910_, p_397906_);
    }

    public ThrownSplashPotion(Level p_392141_, double p_392628_, double p_391615_, double p_396766_, ItemStack p_393636_) {
        super(EntityType.SPLASH_POTION, p_392141_, p_392628_, p_391615_, p_396766_, p_393636_);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    public void onHitAsPotion(ServerLevel p_396848_, ItemStack p_398028_, HitResult p_407339_) {
        PotionContents potioncontents = p_398028_.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        float f = p_398028_.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
        Iterable<MobEffectInstance> iterable = potioncontents.getAllEffects();
        AABB aabb = this.getBoundingBox().move(p_407339_.getLocation().subtract(this.position()));
        AABB aabb1 = aabb.inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb1);
        float f1 = ProjectileUtil.computeMargin(this);
        if (!list.isEmpty()) {
            Entity entity = this.getEffectSource();

            for (LivingEntity livingentity : list) {
                if (livingentity.isAffectedByPotions()) {
                    double d0 = aabb.distanceToSqr(livingentity.getBoundingBox().inflate(f1));
                    if (d0 < 16.0) {
                        double d1 = 1.0 - Math.sqrt(d0) / 4.0;

                        for (MobEffectInstance mobeffectinstance : iterable) {
                            Holder<MobEffect> holder = mobeffectinstance.getEffect();
                            if (holder.value().isInstantenous()) {
                                holder.value().applyInstantenousEffect(p_396848_, this, this.getOwner(), livingentity, mobeffectinstance.getAmplifier(), d1);
                            } else {
                                int i = mobeffectinstance.mapDuration(p_393947_ -> (int)(d1 * p_393947_ * f + 0.5));
                                MobEffectInstance mobeffectinstance1 = new MobEffectInstance(
                                    holder, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()
                                );
                                if (!mobeffectinstance1.endsWithin(20)) {
                                    livingentity.addEffect(mobeffectinstance1, entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}