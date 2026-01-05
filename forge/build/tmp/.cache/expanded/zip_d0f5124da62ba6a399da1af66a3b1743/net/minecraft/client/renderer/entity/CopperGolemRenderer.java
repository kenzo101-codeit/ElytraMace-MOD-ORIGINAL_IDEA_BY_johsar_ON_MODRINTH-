package net.minecraft.client.renderer.entity;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.BlockDecorationLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CopperGolemRenderer extends MobRenderer<CopperGolem, CopperGolemRenderState, CopperGolemModel> {
    public CopperGolemRenderer(EntityRendererProvider.Context p_428998_) {
        super(p_428998_, new CopperGolemModel(p_428998_.bakeLayer(ModelLayers.COPPER_GOLEM)), 0.5F);
        this.addLayer(
            new LivingEntityEmissiveLayer<>(
                this,
                getEyeTextureLocationProvider(),
                (p_424565_, p_422617_) -> 1.0F,
                new CopperGolemModel(p_428998_.bakeLayer(ModelLayers.COPPER_GOLEM)),
                RenderType::eyes,
                false
            )
        );
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new BlockDecorationLayer<>(this, p_426295_ -> p_426295_.blockOnAntenna, this.model::applyBlockOnAntennaTransform));
        this.addLayer(new CustomHeadLayer<>(this, p_428998_.getModelSet(), p_428998_.getPlayerSkinRenderCache()));
    }

    public ResourceLocation getTextureLocation(CopperGolemRenderState p_423722_) {
        return CopperGolemOxidationLevels.getOxidationLevel(p_423722_.weathering).texture();
    }

    private static Function<CopperGolemRenderState, ResourceLocation> getEyeTextureLocationProvider() {
        return p_430384_ -> CopperGolemOxidationLevels.getOxidationLevel(p_430384_.weathering).eyeTexture();
    }

    public CopperGolemRenderState createRenderState() {
        return new CopperGolemRenderState();
    }

    public void extractRenderState(CopperGolem p_429359_, CopperGolemRenderState p_431593_, float p_427347_) {
        super.extractRenderState(p_429359_, p_431593_, p_427347_);
        ArmedEntityRenderState.extractArmedEntityRenderState(p_429359_, p_431593_, this.itemModelResolver);
        p_431593_.weathering = p_429359_.getWeatherState();
        p_431593_.copperGolemState = p_429359_.getState();
        p_431593_.idleAnimationState.copyFrom(p_429359_.getIdleAnimationState());
        p_431593_.interactionGetItem.copyFrom(p_429359_.getInteractionGetItemAnimationState());
        p_431593_.interactionGetNoItem.copyFrom(p_429359_.getInteractionGetNoItemAnimationState());
        p_431593_.interactionDropItem.copyFrom(p_429359_.getInteractionDropItemAnimationState());
        p_431593_.interactionDropNoItem.copyFrom(p_429359_.getInteractionDropNoItemAnimationState());
        p_431593_.blockOnAntenna = Optional.of(p_429359_.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA)).flatMap(p_428707_ -> {
            if (p_428707_.getItem() instanceof BlockItem blockitem) {
                BlockItemStateProperties blockitemstateproperties = p_428707_.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
                return Optional.of(blockitemstateproperties.apply(blockitem.getBlock().defaultBlockState()));
            } else {
                return Optional.empty();
            }
        });
    }
}