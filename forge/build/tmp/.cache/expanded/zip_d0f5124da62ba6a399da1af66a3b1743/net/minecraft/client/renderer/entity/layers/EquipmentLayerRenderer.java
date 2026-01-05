package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EquipmentLayerRenderer {
    private static final int NO_LAYER_COLOR = 0;
    private final EquipmentAssetManager equipmentAssets;
    private final Function<EquipmentLayerRenderer.LayerTextureKey, ResourceLocation> layerTextureLookup;
    private final Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

    public EquipmentLayerRenderer(EquipmentAssetManager p_375597_, TextureAtlas p_363154_) {
        this.equipmentAssets = p_375597_;
        this.layerTextureLookup = Util.memoize(p_374656_ -> p_374656_.layer.getTextureLocation(p_374656_.layerType));
        this.trimSpriteLookup = Util.memoize(p_389518_ -> p_363154_.getSprite(p_389518_.spriteId()));
    }

    public <S> void renderLayers(
        EquipmentClientInfo.LayerType p_377792_,
        ResourceKey<EquipmentAsset> p_377288_,
        Model<? super S> p_366813_,
        S p_428973_,
        ItemStack p_363462_,
        PoseStack p_361892_,
        SubmitNodeCollector p_423232_,
        int p_367241_,
        int p_427024_
    ) {
        this.renderLayers(p_377792_, p_377288_, p_366813_, p_428973_, p_363462_, p_361892_, p_423232_, p_367241_, null, p_427024_, 1);
    }

    public <S> void renderLayers(
        EquipmentClientInfo.LayerType p_376060_,
        ResourceKey<EquipmentAsset> p_375841_,
        Model<? super S> p_366052_,
        S p_425323_,
        ItemStack p_368999_,
        PoseStack p_366797_,
        SubmitNodeCollector p_428767_,
        int p_365571_,
        @Nullable ResourceLocation p_430815_,
        int p_423152_,
        int p_422776_
    ) {
        List<EquipmentClientInfo.Layer> list = this.equipmentAssets.get(p_375841_).getLayers(p_376060_);
        if (!list.isEmpty()) {
            int i = DyedItemColor.getOrDefault(p_368999_, 0);
            boolean flag = p_368999_.hasFoil();
            int j = p_422776_;

            for (EquipmentClientInfo.Layer equipmentclientinfo$layer : list) {
                int k = getColorForLayer(equipmentclientinfo$layer, i);
                if (k != 0) {
                    ResourceLocation resourcelocation = equipmentclientinfo$layer.usePlayerTexture() && p_430815_ != null
                        ? p_430815_
                        : this.layerTextureLookup.apply(new EquipmentLayerRenderer.LayerTextureKey(p_376060_, equipmentclientinfo$layer));
                    p_428767_.order(j++)
                        .submitModel(
                            p_366052_,
                            p_425323_,
                            p_366797_,
                            RenderType.armorCutoutNoCull(resourcelocation),
                            p_365571_,
                            OverlayTexture.NO_OVERLAY,
                            k,
                            null,
                            p_423152_,
                            null
                        );
                    if (flag) {
                        p_428767_.order(j++)
                            .submitModel(p_366052_, p_425323_, p_366797_, RenderType.armorEntityGlint(), p_365571_, OverlayTexture.NO_OVERLAY, k, null, p_423152_, null);
                    }

                    flag = false;
                }
            }

            ArmorTrim armortrim = p_368999_.get(DataComponents.TRIM);
            if (armortrim != null) {
                TextureAtlasSprite textureatlassprite = this.trimSpriteLookup.apply(new EquipmentLayerRenderer.TrimSpriteKey(armortrim, p_376060_, p_375841_));
                RenderType rendertype = Sheets.armorTrimsSheet(armortrim.pattern().value().decal());
                p_428767_.order(j++)
                    .submitModel(p_366052_, p_425323_, p_366797_, rendertype, p_365571_, OverlayTexture.NO_OVERLAY, -1, textureatlassprite, p_423152_, null);
            }
        }
    }

    private static int getColorForLayer(EquipmentClientInfo.Layer p_376428_, int p_365160_) {
        Optional<EquipmentClientInfo.Dyeable> optional = p_376428_.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return p_365160_ != 0 ? p_365160_ : i;
        } else {
            return -1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    record LayerTextureKey(EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer) {
    }

    @OnlyIn(Dist.CLIENT)
    record TrimSpriteKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId) {
        public ResourceLocation spriteId() {
            return this.trim.layerAssetId(this.layerType.trimAssetPrefix(), this.equipmentAssetId);
        }
    }
}