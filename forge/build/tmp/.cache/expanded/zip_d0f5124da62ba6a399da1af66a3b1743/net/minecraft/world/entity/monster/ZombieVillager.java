package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    private static final int NOT_CONVERTING = -1;
    private static final int DEFAULT_XP = 0;
    private int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    @Nullable
    private GossipContainer gossips;
    @Nullable
    private MerchantOffers tradeOffers;
    private int villagerXp = 0;

    public ZombieVillager(EntityType<? extends ZombieVillager> p_34368_, Level p_34369_) {
        super(p_34368_, p_34369_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335784_) {
        super.defineSynchedData(p_335784_);
        p_335784_.define(DATA_CONVERTING_ID, false);
        p_335784_.define(DATA_VILLAGER_DATA, this.initializeVillagerData());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput p_408157_) {
        super.addAdditionalSaveData(p_408157_);
        p_408157_.store("VillagerData", VillagerData.CODEC, this.getVillagerData());
        p_408157_.storeNullable("Offers", MerchantOffers.CODEC, this.tradeOffers);
        p_408157_.storeNullable("Gossips", GossipContainer.CODEC, this.gossips);
        p_408157_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        p_408157_.storeNullable("ConversionPlayer", UUIDUtil.CODEC, this.conversionStarter);
        p_408157_.putInt("Xp", this.villagerXp);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput p_405852_) {
        super.readAdditionalSaveData(p_405852_);
        this.entityData.set(DATA_VILLAGER_DATA, p_405852_.read("VillagerData", VillagerData.CODEC).orElseGet(this::initializeVillagerData));
        this.tradeOffers = p_405852_.read("Offers", MerchantOffers.CODEC).orElse(null);
        this.gossips = p_405852_.read("Gossips", GossipContainer.CODEC).orElse(null);
        int i = p_405852_.getIntOr("ConversionTime", -1);
        if (i != -1) {
            UUID uuid = p_405852_.read("ConversionPlayer", UUIDUtil.CODEC).orElse(null);
            this.startConverting(uuid, i);
        } else {
            this.getEntityData().set(DATA_CONVERTING_ID, false);
            this.villagerConversionTime = -1;
        }

        this.villagerXp = p_405852_.getIntOr("Xp", 0);
    }

    private VillagerData initializeVillagerData() {
        Level level = this.level();
        Optional<Holder.Reference<VillagerProfession>> optional = BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random);
        VillagerData villagerdata = Villager.createDefaultVillagerData().withType(level.registryAccess(), VillagerType.byBiome(level.getBiome(this.blockPosition())));
        if (optional.isPresent()) {
            villagerdata = villagerdata.withProfession(optional.get());
        }

        return villagerdata;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion((ServerLevel)this.level());
            }
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player p_34394_, InteractionHand p_34395_) {
        ItemStack itemstack = p_34394_.getItemInHand(p_34395_);
        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                itemstack.consume(1, p_34394_);
                if (!this.level().isClientSide()) {
                    this.startConverting(p_34394_.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(p_34394_, p_34395_);
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double p_34414_) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID p_34384_, int p_34385_) {
        this.conversionStarter = p_34384_;
        this.villagerConversionTime = p_34385_;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, p_34385_, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte)16);
    }

    @Override
    public void handleEntityEvent(byte p_34372_) {
        if (p_34372_ == 16) {
            if (!this.isSilent()) {
                this.level()
                    .playLocalSound(
                        this.getX(),
                        this.getEyeY(),
                        this.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        this.getSoundSource(),
                        1.0F + this.random.nextFloat(),
                        this.random.nextFloat() * 0.7F + 0.3F,
                        false
                    );
            }
        } else {
            super.handleEntityEvent(p_34372_);
        }
    }

    private void finishConversion(ServerLevel p_34399_) {
        this.convertTo(
            EntityType.VILLAGER,
            ConversionParams.single(this, false, false),
            p_359261_ -> {
                for (EquipmentSlot equipmentslot : this.dropPreservedEquipment(
                    p_34399_, p_341444_ -> !EnchantmentHelper.has(p_341444_, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)
                )) {
                    SlotAccess slotaccess = p_359261_.getSlot(equipmentslot.getIndex() + 300);
                    slotaccess.set(this.getItemBySlot(equipmentslot));
                }

                p_359261_.setVillagerData(this.getVillagerData());
                if (this.gossips != null) {
                    p_359261_.setGossips(this.gossips);
                }

                if (this.tradeOffers != null) {
                    p_359261_.setOffers(this.tradeOffers.copy());
                }

                p_359261_.setVillagerXp(this.villagerXp);
                p_359261_.finalizeSpawn(p_34399_, p_34399_.getCurrentDifficultyAt(p_359261_.blockPosition()), EntitySpawnReason.CONVERSION, null);
                p_359261_.refreshBrain(p_34399_);
                if (this.conversionStarter != null) {
                    Player player = p_34399_.getPlayerByUUID(this.conversionStarter);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)player, this, p_359261_);
                        p_34399_.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, p_359261_);
                    }
                }

                p_359261_.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
                if (!this.isSilent()) {
                    p_34399_.levelEvent(null, 1027, this.blockPosition(), 0);
                }
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, p_359261_);
            }
        );
    }

    @VisibleForTesting
    public void setVillagerConversionTime(int p_368211_) {
        this.villagerConversionTime = p_368211_;
    }

    private int getConversionProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; k++) {
                for (int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; l++) {
                    for (int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; i1++) {
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                i++;
                            }

                            j++;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby()
            ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F
            : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource p_34404_) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    public void setTradeOffers(MerchantOffers p_330397_) {
        this.tradeOffers = p_330397_;
    }

    public void setGossips(GossipContainer p_398029_) {
        this.gossips = p_398029_;
    }

    @Override
    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (!villagerdata.profession().equals(p_34376_.profession())) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> p_394501_) {
        return p_394501_ == DataComponents.VILLAGER_VARIANT ? castComponentValue((DataComponentType<T>)p_394501_, this.getVillagerData().type()) : super.get(p_394501_);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter p_393349_) {
        this.applyImplicitComponentIfPresent(p_393349_, DataComponents.VILLAGER_VARIANT);
        super.applyImplicitComponents(p_393349_);
    }

    @Override
    protected <T> boolean applyImplicitComponent(DataComponentType<T> p_393444_, T p_394446_) {
        if (p_393444_ == DataComponents.VILLAGER_VARIANT) {
            Holder<VillagerType> holder = castComponentValue(DataComponents.VILLAGER_VARIANT, p_394446_);
            this.setVillagerData(this.getVillagerData().withType(holder));
            return true;
        } else {
            return super.applyImplicitComponent(p_393444_, p_394446_);
        }
    }
}
