package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerItemInHandLayer<S extends AvatarRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
    private static final float X_ROT_MIN = (float) (-Math.PI / 6);
    private static final float X_ROT_MAX = (float) (Math.PI / 2);

    public PlayerItemInHandLayer(RenderLayerParent<S, M> p_234866_) {
        super(p_234866_);
    }

    protected void submitArmWithItem(
        S p_424744_, ItemStackRenderState p_427189_, HumanoidArm p_428124_, PoseStack p_426341_, SubmitNodeCollector p_427234_, int p_431065_
    ) {
        if (!p_427189_.isEmpty()) {
            InteractionHand interactionhand = p_428124_ == p_424744_.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (p_424744_.isUsingItem && p_424744_.useItemHand == interactionhand && p_424744_.attackTime < 1.0E-5F && !p_424744_.heldOnHead.isEmpty()) {
                this.renderItemHeldToEye(p_424744_, p_428124_, p_426341_, p_427234_, p_431065_);
            } else {
                super.submitArmWithItem(p_424744_, p_427189_, p_428124_, p_426341_, p_427234_, p_431065_);
            }
        }
    }

    private void renderItemHeldToEye(S p_425508_, HumanoidArm p_378038_, PoseStack p_376706_, SubmitNodeCollector p_425625_, int p_376343_) {
        p_376706_.pushPose();
        this.getParentModel().root().translateAndRotate(p_376706_);
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (float) (-Math.PI / 6), (float) (Math.PI / 2));
        modelpart.translateAndRotate(p_376706_);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(p_376706_, CustomHeadLayer.Transforms.DEFAULT);
        boolean flag = p_378038_ == HumanoidArm.LEFT;
        p_376706_.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
        p_425508_.heldOnHead.submit(p_376706_, p_425625_, p_376343_, OverlayTexture.NO_OVERLAY, p_425508_.outlineColor);
        p_376706_.popPose();
    }
}