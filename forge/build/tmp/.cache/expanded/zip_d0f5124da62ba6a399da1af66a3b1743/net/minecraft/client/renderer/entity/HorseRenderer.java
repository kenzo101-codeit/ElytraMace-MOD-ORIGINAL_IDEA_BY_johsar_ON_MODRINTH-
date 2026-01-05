package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class HorseRenderer extends AbstractHorseRenderer<Horse, HorseRenderState, HorseModel> {
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Maps.newEnumMap(
        Map.of(
            Variant.WHITE,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_white.png"),
            Variant.CREAMY,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_creamy.png"),
            Variant.CHESTNUT,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_chestnut.png"),
            Variant.BROWN,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_brown.png"),
            Variant.BLACK,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_black.png"),
            Variant.GRAY,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_gray.png"),
            Variant.DARK_BROWN,
            ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_darkbrown.png")
        )
    );

    public HorseRenderer(EntityRendererProvider.Context p_174167_) {
        super(p_174167_, new HorseModel(p_174167_.bakeLayer(ModelLayers.HORSE)), new HorseModel(p_174167_.bakeLayer(ModelLayers.HORSE_BABY)));
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(
            new SimpleEquipmentLayer<>(
                this,
                p_174167_.getEquipmentRenderer(),
                EquipmentClientInfo.LayerType.HORSE_BODY,
                p_389515_ -> p_389515_.bodyArmorItem,
                new HorseModel(p_174167_.bakeLayer(ModelLayers.HORSE_ARMOR)),
                new HorseModel(p_174167_.bakeLayer(ModelLayers.HORSE_BABY_ARMOR)),
                2
            )
        );
        this.addLayer(
            new SimpleEquipmentLayer<>(
                this,
                p_174167_.getEquipmentRenderer(),
                EquipmentClientInfo.LayerType.HORSE_SADDLE,
                p_389516_ -> p_389516_.saddle,
                new EquineSaddleModel(p_174167_.bakeLayer(ModelLayers.HORSE_SADDLE)),
                new EquineSaddleModel(p_174167_.bakeLayer(ModelLayers.HORSE_BABY_SADDLE)),
                2
            )
        );
    }

    public ResourceLocation getTextureLocation(HorseRenderState p_365322_) {
        return LOCATION_BY_VARIANT.get(p_365322_.variant);
    }

    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }

    public void extractRenderState(Horse p_363101_, HorseRenderState p_362954_, float p_365681_) {
        super.extractRenderState(p_363101_, p_362954_, p_365681_);
        p_362954_.variant = p_363101_.getVariant();
        p_362954_.markings = p_363101_.getMarkings();
        p_362954_.bodyArmorItem = p_363101_.getBodyArmorItem().copy();
    }
}