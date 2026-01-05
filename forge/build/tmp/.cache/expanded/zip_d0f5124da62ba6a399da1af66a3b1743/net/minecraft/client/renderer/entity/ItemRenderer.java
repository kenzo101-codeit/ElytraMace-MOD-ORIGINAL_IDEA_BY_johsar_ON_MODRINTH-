package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer {
    public static final ResourceLocation ENCHANTED_GLINT_ARMOR = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
    public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
    public static final float SPECIAL_FOIL_UI_SCALE = 0.5F;
    public static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75F;
    public static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125F;
    public static final int NO_TINT = -1;

    public static void renderItem(
        ItemDisplayContext p_362035_,
        PoseStack p_370127_,
        MultiBufferSource p_365365_,
        int p_363416_,
        int p_367651_,
        int[] p_378157_,
        List<BakedQuad> p_395550_,
        RenderType p_377949_,
        ItemStackRenderState.FoilType p_378770_
    ) {
        VertexConsumer vertexconsumer;
        if (p_378770_ == ItemStackRenderState.FoilType.SPECIAL) {
            PoseStack.Pose posestack$pose = p_370127_.last().copy();
            if (p_362035_ == ItemDisplayContext.GUI) {
                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
            } else if (p_362035_.firstPerson()) {
                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
            }

            vertexconsumer = getSpecialFoilBuffer(p_365365_, p_377949_, posestack$pose);
        } else {
            vertexconsumer = getFoilBuffer(p_365365_, p_377949_, true, p_378770_ != ItemStackRenderState.FoilType.NONE);
        }

        renderQuadList(p_370127_, vertexconsumer, p_395550_, p_378157_, p_363416_, p_367651_);
    }

    private static VertexConsumer getSpecialFoilBuffer(MultiBufferSource p_407663_, RenderType p_409389_, PoseStack.Pose p_406599_) {
        return VertexMultiConsumer.create(
            new SheetedDecalTextureGenerator(p_407663_.getBuffer(useTransparentGlint(p_409389_) ? RenderType.glintTranslucent() : RenderType.glint()), p_406599_, 0.0078125F),
            p_407663_.getBuffer(p_409389_)
        );
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
        if (p_115215_) {
            return useTransparentGlint(p_115213_)
                ? VertexMultiConsumer.create(p_115212_.getBuffer(RenderType.glintTranslucent()), p_115212_.getBuffer(p_115213_))
                : VertexMultiConsumer.create(p_115212_.getBuffer(p_115214_ ? RenderType.glint() : RenderType.entityGlint()), p_115212_.getBuffer(p_115213_));
        } else {
            return p_115212_.getBuffer(p_115213_);
        }
    }

    public static List<RenderType> getFoilRenderTypes(RenderType p_427288_, boolean p_423420_, boolean p_428971_) {
        if (p_428971_) {
            return useTransparentGlint(p_427288_)
                ? List.of(p_427288_, RenderType.glintTranslucent())
                : List.of(p_427288_, p_423420_ ? RenderType.glint() : RenderType.entityGlint());
        } else {
            return List.of(p_427288_);
        }
    }

    private static boolean useTransparentGlint(RenderType p_406788_) {
        return Minecraft.useShaderTransparency() && p_406788_ == Sheets.translucentItemSheet();
    }

    private static int getLayerColorSafe(int[] p_377342_, int p_378491_) {
        return p_378491_ >= 0 && p_378491_ < p_377342_.length ? p_377342_[p_378491_] : -1;
    }

    public static void renderQuadList(PoseStack p_115163_, VertexConsumer p_115164_, List<BakedQuad> p_115165_, int[] p_375549_, int p_115167_, int p_115168_) {
        PoseStack.Pose posestack$pose = p_115163_.last();

        for (BakedQuad bakedquad : p_115165_) {
            float f;
            float f1;
            float f2;
            float f3;
            if (bakedquad.isTinted()) {
                int i = getLayerColorSafe(p_375549_, bakedquad.tintIndex());
                f = ARGB.alpha(i) / 255.0F;
                f1 = ARGB.red(i) / 255.0F;
                f2 = ARGB.green(i) / 255.0F;
                f3 = ARGB.blue(i) / 255.0F;
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
                f3 = 1.0F;
            }

            p_115164_.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, p_115167_, p_115168_, true);
        }
    }
}
