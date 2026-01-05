package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SpinAttackEffectModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpinAttackEffectLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
    private final SpinAttackEffectModel model;

    public SpinAttackEffectLayer(RenderLayerParent<AvatarRenderState, PlayerModel> p_174540_, EntityModelSet p_174541_) {
        super(p_174540_);
        this.model = new SpinAttackEffectModel(p_174541_.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK));
    }

    public void submit(PoseStack p_425499_, SubmitNodeCollector p_425370_, int p_423874_, AvatarRenderState p_430853_, float p_431621_, float p_431026_) {
        if (p_430853_.isAutoSpinAttack) {
            p_425370_.submitModel(
                this.model, p_430853_, p_425499_, this.model.renderType(TEXTURE), p_423874_, OverlayTexture.NO_OVERLAY, p_430853_.outlineColor, null
            );
        }
    }
}