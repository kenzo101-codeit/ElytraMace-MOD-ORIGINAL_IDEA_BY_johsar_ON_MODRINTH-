package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HappyGhastHarnessModel;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.RopesLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HappyGhastRenderer extends AgeableMobRenderer<HappyGhast, HappyGhastRenderState, HappyGhastModel> {
    private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast.png");
    private static final ResourceLocation GHAST_BABY_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_baby.png");
    private static final ResourceLocation GHAST_ROPES = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_ropes.png");

    public HappyGhastRenderer(EntityRendererProvider.Context p_408214_) {
        super(p_408214_, new HappyGhastModel(p_408214_.bakeLayer(ModelLayers.HAPPY_GHAST)), new HappyGhastModel(p_408214_.bakeLayer(ModelLayers.HAPPY_GHAST_BABY)), 2.0F);
        this.addLayer(
            new SimpleEquipmentLayer<>(
                this,
                p_408214_.getEquipmentRenderer(),
                EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY,
                p_408530_ -> p_408530_.bodyItem,
                new HappyGhastHarnessModel(p_408214_.bakeLayer(ModelLayers.HAPPY_GHAST_HARNESS)),
                new HappyGhastHarnessModel(p_408214_.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_HARNESS))
            )
        );
        this.addLayer(new RopesLayer<>(this, p_408214_.getModelSet(), GHAST_ROPES));
    }

    public ResourceLocation getTextureLocation(HappyGhastRenderState p_407787_) {
        return p_407787_.isBaby ? GHAST_BABY_LOCATION : GHAST_LOCATION;
    }

    public HappyGhastRenderState createRenderState() {
        return new HappyGhastRenderState();
    }

    protected AABB getBoundingBoxForCulling(HappyGhast p_410454_) {
        AABB aabb = super.getBoundingBoxForCulling(p_410454_);
        float f = p_410454_.getBbHeight();
        return aabb.setMinY(aabb.minY - f / 2.0F);
    }

    public void extractRenderState(HappyGhast p_406562_, HappyGhastRenderState p_407988_, float p_409409_) {
        super.extractRenderState(p_406562_, p_407988_, p_409409_);
        p_407988_.bodyItem = p_406562_.getItemBySlot(EquipmentSlot.BODY).copy();
        p_407988_.isRidden = p_406562_.isVehicle();
        p_407988_.isLeashHolder = p_406562_.isLeashHolder();
    }
}