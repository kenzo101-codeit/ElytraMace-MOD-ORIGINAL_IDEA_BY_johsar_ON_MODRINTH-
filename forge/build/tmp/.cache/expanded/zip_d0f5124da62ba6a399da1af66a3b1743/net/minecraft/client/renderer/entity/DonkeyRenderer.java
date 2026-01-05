package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DonkeyModel;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DonkeyRenderer<T extends AbstractChestedHorse> extends AbstractHorseRenderer<T, DonkeyRenderState, DonkeyModel> {
    private final ResourceLocation texture;

    public DonkeyRenderer(EntityRendererProvider.Context p_362293_, DonkeyRenderer.Type p_397487_) {
        super(p_362293_, new DonkeyModel(p_362293_.bakeLayer(p_397487_.model)), new DonkeyModel(p_362293_.bakeLayer(p_397487_.babyModel)));
        this.texture = p_397487_.texture;
        this.addLayer(
            new SimpleEquipmentLayer<>(
                this,
                p_362293_.getEquipmentRenderer(),
                p_397487_.saddleLayer,
                p_397593_ -> p_397593_.saddle,
                new EquineSaddleModel(p_362293_.bakeLayer(p_397487_.saddleModel)),
                new EquineSaddleModel(p_362293_.bakeLayer(p_397487_.babySaddleModel))
            )
        );
    }

    public ResourceLocation getTextureLocation(DonkeyRenderState p_367902_) {
        return this.texture;
    }

    public DonkeyRenderState createRenderState() {
        return new DonkeyRenderState();
    }

    public void extractRenderState(T p_363167_, DonkeyRenderState p_369827_, float p_366107_) {
        super.extractRenderState(p_363167_, p_369827_, p_366107_);
        p_369827_.hasChest = p_363167_.hasChest();
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Type {
        DONKEY(
            ResourceLocation.withDefaultNamespace("textures/entity/horse/donkey.png"),
            ModelLayers.DONKEY,
            ModelLayers.DONKEY_BABY,
            EquipmentClientInfo.LayerType.DONKEY_SADDLE,
            ModelLayers.DONKEY_SADDLE,
            ModelLayers.DONKEY_BABY_SADDLE
        ),
        MULE(
            ResourceLocation.withDefaultNamespace("textures/entity/horse/mule.png"),
            ModelLayers.MULE,
            ModelLayers.MULE_BABY,
            EquipmentClientInfo.LayerType.MULE_SADDLE,
            ModelLayers.MULE_SADDLE,
            ModelLayers.MULE_BABY_SADDLE
        );

        final ResourceLocation texture;
        final ModelLayerLocation model;
        final ModelLayerLocation babyModel;
        final EquipmentClientInfo.LayerType saddleLayer;
        final ModelLayerLocation saddleModel;
        final ModelLayerLocation babySaddleModel;

        private Type(
            final ResourceLocation p_397282_,
            final ModelLayerLocation p_396383_,
            final ModelLayerLocation p_392561_,
            final EquipmentClientInfo.LayerType p_394331_,
            final ModelLayerLocation p_392966_,
            final ModelLayerLocation p_394783_
        ) {
            this.texture = p_397282_;
            this.model = p_396383_;
            this.babyModel = p_392561_;
            this.saddleLayer = p_394331_;
            this.saddleModel = p_392966_;
            this.babySaddleModel = p_394783_;
        }
    }
}