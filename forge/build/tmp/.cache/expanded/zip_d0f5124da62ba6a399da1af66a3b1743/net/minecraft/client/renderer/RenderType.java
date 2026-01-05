package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public abstract class RenderType extends RenderStateShard {
    private static final int MEGABYTE = 1048576;
    public static final int BIG_BUFFER_SIZE = 4194304;
    public static final int SMALL_BUFFER_SIZE = 786432;
    public static final int TRANSIENT_BUFFER_SIZE = 1536;
    private static final RenderType SOLID = create(
        "solid", 1536, true, false, RenderPipelines.SOLID, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true)
    );
    private static final RenderType CUTOUT_MIPPED = create(
        "cutout_mipped",
        1536,
        true,
        false,
        RenderPipelines.CUTOUT_MIPPED,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true)
    );
    private static final RenderType CUTOUT = create(
        "cutout", 1536, true, false, RenderPipelines.CUTOUT, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).createCompositeState(true)
    );
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = create(
        "translucent_moving_block",
        786432,
        false,
        true,
        RenderPipelines.TRANSLUCENT_MOVING_BLOCK,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true)
    );
    private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize(
        p_404960_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404960_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            return create("armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT = Util.memoize(
        p_404956_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404956_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            return create("armor_translucent", 1536, true, true, RenderPipelines.ARMOR_TRANSLUCENT, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID = Util.memoize(
        p_404946_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404946_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_solid", 1536, true, false, RenderPipelines.ENTITY_SOLID, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize(
        p_404961_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404961_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD)
                .createCompositeState(true);
            return create("entity_solid_z_offset_forward", 1536, true, false, RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT = Util.memoize(
        p_404976_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404976_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_cutout", 1536, true, false, RenderPipelines.ENTITY_CUTOUT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL = Util.memoize(
        (p_404977_, p_404978_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404977_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(p_404978_);
            return create("entity_cutout_no_cull", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(
        (p_404953_, p_404954_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404953_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(p_404954_);
            return create("entity_cutout_no_cull_z_offset", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
        p_404975_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404975_, false))
                .setOutputState(ITEM_ENTITY_TARGET)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("item_entity_translucent_cull", 1536, true, true, RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(
        (p_404966_, p_404967_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404966_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(p_404967_);
            return create("entity_translucent", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
        (p_404962_, p_404963_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404962_, false))
                .setOverlayState(OVERLAY)
                .createCompositeState(p_404963_);
            return create("entity_translucent_emissive", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize(
        p_404964_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404964_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_smooth_cutout", 1536, RenderPipelines.ENTITY_SMOOTH_CUTOUT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM = Util.memoize(
        (p_404950_, p_404951_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404950_, false))
                .createCompositeState(false);
            return create("beacon_beam", 1536, false, true, p_404951_ ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_DECAL = Util.memoize(
        p_404957_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404957_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
            return create("entity_decal", 1536, RenderPipelines.ENTITY_DECAL, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE = Util.memoize(
        p_404979_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404979_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
            return create("entity_no_outline", 1536, false, true, RenderPipelines.ENTITY_NO_OUTLINE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW = Util.memoize(
        p_404959_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404959_, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(false);
            return create("entity_shadow", 1536, false, false, RenderPipelines.ENTITY_SHADOW, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize(
        p_404965_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_404965_, false))
                .createCompositeState(true);
            return create("entity_alpha", 1536, RenderPipelines.DRAGON_EXPLOSION_ALPHA, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize(
        p_404969_ -> {
            RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_404969_, false);
            return create(
                "eyes",
                1536,
                false,
                true,
                RenderPipelines.EYES,
                RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false)
            );
        }
    );
    private static final RenderType LEASH = create(
        "leash", 1536, RenderPipelines.LEASH, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final RenderType WATER_MASK = create(
        "water_mask", 1536, RenderPipelines.WATER_MASK, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).createCompositeState(false)
    );
    private static final RenderType ARMOR_ENTITY_GLINT = create(
        "armor_entity_glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ARMOR, false))
            .setTexturingState(ARMOR_ENTITY_GLINT_TEXTURING)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false)
    );
    private static final RenderType GLINT_TRANSLUCENT = create(
        "glint_translucent",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false))
            .setTexturingState(GLINT_TEXTURING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    private static final RenderType GLINT = create(
        "glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false))
            .setTexturingState(GLINT_TEXTURING)
            .createCompositeState(false)
    );
    private static final RenderType ENTITY_GLINT = create(
        "entity_glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false))
            .setTexturingState(ENTITY_GLINT_TEXTURING)
            .createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize(
        p_404971_ -> {
            RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_404971_, false);
            return create(
                "crumbling",
                1536,
                false,
                true,
                RenderPipelines.CRUMBLING,
                RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false)
            );
        }
    );
    private static final Function<ResourceLocation, RenderType> TEXT = Util.memoize(
        p_404980_ -> create(
            "text",
            786432,
            false,
            false,
            RenderPipelines.TEXT,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404980_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final RenderType TEXT_BACKGROUND = create(
        "text_background",
        1536,
        false,
        true,
        RenderPipelines.TEXT_BACKGROUND,
        RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize(
        p_404947_ -> create(
            "text_intensity",
            786432,
            false,
            false,
            RenderPipelines.TEXT_INTENSITY,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404947_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize(
        p_404955_ -> create(
            "text_polygon_offset",
            1536,
            false,
            true,
            RenderPipelines.TEXT_POLYGON_OFFSET,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404955_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
        p_404958_ -> create(
            "text_intensity_polygon_offset",
            1536,
            false,
            true,
            RenderPipelines.TEXT_INTENSITY,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404958_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize(
        p_404968_ -> create(
            "text_see_through",
            1536,
            false,
            false,
            RenderPipelines.TEXT_SEE_THROUGH,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404968_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final RenderType TEXT_BACKGROUND_SEE_THROUGH = create(
        "text_background_see_through",
        1536,
        false,
        true,
        RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH,
        RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
        p_404970_ -> create(
            "text_intensity_see_through",
            1536,
            false,
            true,
            RenderPipelines.TEXT_INTENSITY_SEE_THROUGH,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404970_, false)).setLightmapState(LIGHTMAP).createCompositeState(false)
        )
    );
    private static final RenderType LIGHTNING = create(
        "lightning", 1536, false, true, RenderPipelines.LIGHTNING, RenderType.CompositeState.builder().setOutputState(WEATHER_TARGET).createCompositeState(false)
    );
    private static final RenderType DRAGON_RAYS = create(
        "dragon_rays", 1536, false, false, RenderPipelines.DRAGON_RAYS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType DRAGON_RAYS_DEPTH = create(
        "dragon_rays_depth", 1536, false, false, RenderPipelines.DRAGON_RAYS_DEPTH, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType TRIPWIRE = create(
        "tripwire",
        1536,
        true,
        true,
        RenderPipelines.TRIPWIRE,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(WEATHER_TARGET).createCompositeState(true)
    );
    private static final RenderType END_PORTAL = create(
        "end_portal",
        1536,
        false,
        false,
        RenderPipelines.END_PORTAL,
        RenderType.CompositeState.builder()
            .setTextureState(
                RenderStateShard.MultiTextureStateShard.builder()
                    .add(AbstractEndPortalRenderer.END_SKY_LOCATION, false)
                    .add(AbstractEndPortalRenderer.END_PORTAL_LOCATION, false)
                    .build()
            )
            .createCompositeState(false)
    );
    private static final RenderType END_GATEWAY = create(
        "end_gateway",
        1536,
        false,
        false,
        RenderPipelines.END_GATEWAY,
        RenderType.CompositeState.builder()
            .setTextureState(
                RenderStateShard.MultiTextureStateShard.builder()
                    .add(AbstractEndPortalRenderer.END_SKY_LOCATION, false)
                    .add(AbstractEndPortalRenderer.END_PORTAL_LOCATION, false)
                    .build()
            )
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType LINES = create(
        "lines",
        1536,
        RenderPipelines.LINES,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType SECONDARY_BLOCK_OUTLINE = create(
        "secondary_block_outline",
        1536,
        RenderPipelines.SECONDARY_BLOCK_OUTLINE,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(7.0)))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType LINE_STRIP = create(
        "line_strip",
        1536,
        RenderPipelines.LINE_STRIP,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    private static final Function<Double, RenderType.CompositeRenderType> DEBUG_LINE_STRIP = Util.memoize(
        p_389417_ -> create(
            "debug_line_strip",
            1536,
            RenderPipelines.DEBUG_LINE_STRIP,
            RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(p_389417_))).createCompositeState(false)
        )
    );
    private static final RenderType.CompositeRenderType DEBUG_FILLED_BOX = create(
        "debug_filled_box", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_QUADS = create(
        "debug_quads", 1536, false, true, RenderPipelines.DEBUG_QUADS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_TRIANGLE_FAN = create(
        "debug_triangle_fan", 1536, false, true, RenderPipelines.DEBUG_TRIANGLE_FAN, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_STRUCTURE_QUADS = create(
        "debug_structure_quads", 1536, false, true, RenderPipelines.DEBUG_STRUCTURE_QUADS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_SECTION_QUADS = create(
        "debug_section_quads", 1536, false, true, RenderPipelines.DEBUG_SECTION_QUADS, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> WEATHER_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_DEPTH_WRITE);
    private static final Function<ResourceLocation, RenderType> WEATHER_NO_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_NO_DEPTH_WRITE);
    private static final Function<ResourceLocation, RenderType> BLOCK_SCREEN_EFFECT = Util.memoize(
        p_404952_ -> create(
            "block_screen_effect",
            1536,
            false,
            false,
            RenderPipelines.BLOCK_SCREEN_EFFECT,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404952_, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> FIRE_SCREEN_EFFECT = Util.memoize(
        p_404948_ -> create(
            "fire_screen_effect",
            1536,
            false,
            false,
            RenderPipelines.FIRE_SCREEN_EFFECT,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_404948_, false)).createCompositeState(false)
        )
    );
    private final int bufferSize;
    private final boolean affectsCrumbling;
    private final boolean sortOnUpload;
    private int chunkLayerId = -1;
    /** {@return the unique ID of this {@link RenderType} for chunk rendering purposes, or {@literal -1} if this is not a chunk {@link RenderType}} */
    public final int getChunkLayerId() {
        return chunkLayerId;
    }

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType armorCutoutNoCull(ResourceLocation p_110432_) {
        return ARMOR_CUTOUT_NO_CULL.apply(p_110432_);
    }

    public static RenderType createArmorDecalCutoutNoCull(ResourceLocation p_298982_) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(p_298982_, false))
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(true);
        return create("armor_decal_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL, rendertype$compositestate);
    }

    public static RenderType armorTranslucent(ResourceLocation p_368218_) {
        return ARMOR_TRANSLUCENT.apply(p_368218_);
    }

    public static RenderType entitySolid(ResourceLocation p_110447_) {
        return ENTITY_SOLID.apply(p_110447_);
    }

    public static RenderType entitySolidZOffsetForward(ResourceLocation p_364403_) {
        return ENTITY_SOLID_Z_OFFSET_FORWARD.apply(p_364403_);
    }

    public static RenderType entityCutout(ResourceLocation p_110453_) {
        return ENTITY_CUTOUT.apply(p_110453_);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation p_110444_, boolean p_110445_) {
        return ENTITY_CUTOUT_NO_CULL.apply(p_110444_, p_110445_);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation p_110459_) {
        return entityCutoutNoCull(p_110459_, true);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_110449_, boolean p_110450_) {
        return ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(p_110449_, p_110450_);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_110465_) {
        return entityCutoutNoCullZOffset(p_110465_, true);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation p_110468_) {
        return ITEM_ENTITY_TRANSLUCENT_CULL.apply(p_110468_);
    }

    public static RenderType entityTranslucent(ResourceLocation p_110455_, boolean p_110456_) {
        return ENTITY_TRANSLUCENT.apply(p_110455_, p_110456_);
    }

    public static RenderType entityTranslucent(ResourceLocation p_110474_) {
        return entityTranslucent(p_110474_, true);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation p_234336_, boolean p_234337_) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(p_234336_, p_234337_);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation p_234339_) {
        return entityTranslucentEmissive(p_234339_, true);
    }

    public static RenderType entitySmoothCutout(ResourceLocation p_110477_) {
        return ENTITY_SMOOTH_CUTOUT.apply(p_110477_);
    }

    public static RenderType beaconBeam(ResourceLocation p_110461_, boolean p_110462_) {
        return BEACON_BEAM.apply(p_110461_, p_110462_);
    }

    public static RenderType entityDecal(ResourceLocation p_110480_) {
        return ENTITY_DECAL.apply(p_110480_);
    }

    public static RenderType entityNoOutline(ResourceLocation p_110483_) {
        return ENTITY_NO_OUTLINE.apply(p_110483_);
    }

    public static RenderType entityShadow(ResourceLocation p_110486_) {
        return ENTITY_SHADOW.apply(p_110486_);
    }

    public static RenderType dragonExplosionAlpha(ResourceLocation p_173236_) {
        return DRAGON_EXPLOSION_ALPHA.apply(p_173236_);
    }

    public static RenderType eyes(ResourceLocation p_110489_) {
        return EYES.apply(p_110489_);
    }

    public static RenderType breezeEyes(ResourceLocation p_311465_) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(p_311465_, false);
    }

    public static RenderType breezeWind(ResourceLocation p_311543_, float p_312161_, float p_310801_) {
        return create(
            "breeze_wind",
            1536,
            false,
            true,
            RenderPipelines.BREEZE_WIND,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_311543_, false))
                .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_312161_, p_310801_))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false)
        );
    }

    public static RenderType energySwirl(ResourceLocation p_110437_, float p_110438_, float p_110439_) {
        return create(
            "energy_swirl",
            1536,
            false,
            true,
            RenderPipelines.ENERGY_SWIRL,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_110437_, false))
                .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_110438_, p_110439_))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false)
        );
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(ResourceLocation p_110492_) {
        return RenderType.CompositeRenderType.OUTLINE.apply(p_110492_, false);
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType crumbling(ResourceLocation p_110495_) {
        return CRUMBLING.apply(p_110495_);
    }

    public static RenderType text(ResourceLocation p_110498_) {
        return net.minecraftforge.client.ForgeRenderTypes.getText(p_110498_);
    }

    public static RenderType textBackground() {
        return TEXT_BACKGROUND;
    }

    public static RenderType textIntensity(ResourceLocation p_173238_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensity(p_173238_);
    }

    public static RenderType textPolygonOffset(ResourceLocation p_181445_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextPolygonOffset(p_181445_);
    }

    public static RenderType textIntensityPolygonOffset(ResourceLocation p_181447_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensityPolygonOffset(p_181447_);
    }

    public static RenderType textSeeThrough(ResourceLocation p_110501_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextSeeThrough(p_110501_);
    }

    public static RenderType textBackgroundSeeThrough() {
        return TEXT_BACKGROUND_SEE_THROUGH;
    }

    public static RenderType textIntensitySeeThrough(ResourceLocation p_173241_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensitySeeThrough(p_173241_);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType dragonRays() {
        return DRAGON_RAYS;
    }

    public static RenderType dragonRaysDepth() {
        return DRAGON_RAYS_DEPTH;
    }

    public static RenderType tripwire() {
        return TRIPWIRE;
    }

    public static RenderType endPortal() {
        return END_PORTAL;
    }

    public static RenderType endGateway() {
        return END_GATEWAY;
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType secondaryBlockOutline() {
        return SECONDARY_BLOCK_OUTLINE;
    }

    public static RenderType lineStrip() {
        return LINE_STRIP;
    }

    public static RenderType debugLineStrip(double p_270166_) {
        return DEBUG_LINE_STRIP.apply(p_270166_);
    }

    public static RenderType debugFilledBox() {
        return DEBUG_FILLED_BOX;
    }

    public static RenderType debugQuads() {
        return DEBUG_QUADS;
    }

    public static RenderType debugTriangleFan() {
        return DEBUG_TRIANGLE_FAN;
    }

    public static RenderType debugStructureQuads() {
        return DEBUG_STRUCTURE_QUADS;
    }

    public static RenderType debugSectionQuads() {
        return DEBUG_SECTION_QUADS;
    }

    private static Function<ResourceLocation, RenderType> createWeather(RenderPipeline p_395516_) {
        return Util.memoize(
            p_404974_ -> create(
                "weather",
                1536,
                false,
                false,
                p_395516_,
                RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(p_404974_, false))
                    .setOutputState(WEATHER_TARGET)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false)
            )
        );
    }

    public static RenderType weather(ResourceLocation p_376873_, boolean p_375456_) {
        return (p_375456_ ? WEATHER_DEPTH_WRITE : WEATHER_NO_DEPTH_WRITE).apply(p_376873_);
    }

    public static RenderType blockScreenEffect(ResourceLocation p_378425_) {
        return BLOCK_SCREEN_EFFECT.apply(p_378425_);
    }

    public static RenderType fireScreenEffect(ResourceLocation p_376662_) {
        return FIRE_SCREEN_EFFECT.apply(p_376662_);
    }

    public RenderType(String p_173178_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173184_, p_173185_);
        this.bufferSize = p_173181_;
        this.affectsCrumbling = p_173182_;
        this.sortOnUpload = p_173183_;
    }

    public static RenderType.CompositeRenderType create(String p_173210_, int p_173213_, RenderPipeline p_394966_, RenderType.CompositeState p_173214_) {
        return create(p_173210_, p_173213_, false, false, p_394966_, p_173214_);
    }

    public static RenderType.CompositeRenderType create(
        String p_173216_, int p_173219_, boolean p_173220_, boolean p_173221_, RenderPipeline p_393788_, RenderType.CompositeState p_173222_
    ) {
        return new RenderType.CompositeRenderType(p_173216_, p_173219_, p_173220_, p_173221_, p_393788_, p_173222_);
    }

    public abstract void draw(MeshData p_343145_);

    public int bufferSize() {
        return this.bufferSize;
    }

    public abstract VertexFormat format();

    public abstract VertexFormat.Mode mode();

    public Optional<RenderType> outline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public abstract RenderPipeline pipeline();

    public boolean affectsCrumbling() {
        return this.affectsCrumbling;
    }

    public boolean canConsolidateConsecutiveGeometry() {
        return !this.mode().connectedPrimitives;
    }

    public boolean sortOnUpload() {
        return this.sortOnUpload;
    }

    @OnlyIn(Dist.CLIENT)
    public static final class CompositeRenderType extends RenderType {
        static final BiFunction<ResourceLocation, Boolean, RenderType> OUTLINE = Util.memoize(
            (p_404982_, p_404983_) -> RenderType.create(
                "outline",
                1536,
                p_404983_ ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL,
                RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(p_404982_, false))
                    .setOutputState(OUTLINE_TARGET)
                    .createCompositeState(RenderType.OutlineProperty.IS_OUTLINE)
            )
        );
        private final RenderType.CompositeState state;
        private final RenderPipeline renderPipeline;
        private final Optional<RenderType> outline;
        private final boolean isOutline;

        CompositeRenderType(
            String p_173258_, int p_173261_, boolean p_173262_, boolean p_173263_, RenderPipeline p_394121_, RenderType.CompositeState p_173264_
        ) {
            super(
                p_173258_,
                p_173261_,
                p_173262_,
                p_173263_,
                () -> p_173264_.states.forEach(RenderStateShard::setupRenderState),
                () -> p_173264_.states.forEach(RenderStateShard::clearRenderState)
            );
            this.state = p_173264_;
            this.renderPipeline = p_394121_;
            this.outline = p_173264_.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE
                ? p_173264_.textureState.cutoutTexture().map(p_389456_ -> OUTLINE.apply(p_389456_, p_394121_.isCull()))
                : Optional.empty();
            this.isOutline = p_173264_.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
        }

        @Override
        public Optional<RenderType> outline() {
            return this.outline;
        }

        @Override
        public boolean isOutline() {
            return this.isOutline;
        }

        @Override
        public VertexFormat format() {
            return this.renderPipeline.getVertexFormat();
        }

        @Override
        public VertexFormat.Mode mode() {
            return this.renderPipeline.getVertexFormatMode();
        }

        @Override
        public RenderPipeline pipeline() {
            return this.renderPipeline;
        }

        @Override
        public void draw(MeshData p_397523_) {
            this.setupRenderState();
            GpuBufferSlice gpubufferslice = RenderSystem.getDynamicUniforms()
                .writeTransform(
                    RenderSystem.getModelViewMatrix(),
                    new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                    new Vector3f(),
                    RenderSystem.getTextureMatrix(),
                    RenderSystem.getShaderLineWidth()
                );
            MeshData meshdata = p_397523_;

            try {
                GpuBuffer gpubuffer = this.renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(p_397523_.vertexBuffer());
                GpuBuffer gpubuffer1;
                VertexFormat.IndexType vertexformat$indextype;
                if (p_397523_.indexBuffer() == null) {
                    RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(
                        p_397523_.drawState().mode()
                    );
                    gpubuffer1 = rendersystem$autostorageindexbuffer.getBuffer(p_397523_.drawState().indexCount());
                    vertexformat$indextype = rendersystem$autostorageindexbuffer.type();
                } else {
                    gpubuffer1 = this.renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(p_397523_.indexBuffer());
                    vertexformat$indextype = p_397523_.drawState().indexType();
                }

                RenderTarget rendertarget = this.state.outputState.getRenderTarget();
                GpuTextureView gputextureview = RenderSystem.outputColorTextureOverride != null
                    ? RenderSystem.outputColorTextureOverride
                    : rendertarget.getColorTextureView();
                GpuTextureView gputextureview1 = rendertarget.useDepth
                    ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : rendertarget.getDepthTextureView())
                    : null;

                try (RenderPass renderpass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(
                            () -> "Immediate draw for " + this.getName(), gputextureview, OptionalInt.empty(), gputextureview1, OptionalDouble.empty()
                        )) {
                    renderpass.setPipeline(this.renderPipeline);
                    ScissorState scissorstate = RenderSystem.getScissorStateForRenderTypeDraws();
                    if (scissorstate.enabled()) {
                        renderpass.enableScissor(scissorstate.x(), scissorstate.y(), scissorstate.width(), scissorstate.height());
                    }

                    RenderSystem.bindDefaultUniforms(renderpass);
                    renderpass.setUniform("DynamicTransforms", gpubufferslice);
                    renderpass.setVertexBuffer(0, gpubuffer);

                    for (int i = 0; i < 12; i++) {
                        GpuTextureView gputextureview2 = RenderSystem.getShaderTexture(i);
                        if (gputextureview2 != null) {
                            renderpass.bindSampler("Sampler" + i, gputextureview2);
                        }
                    }

                    renderpass.setIndexBuffer(gpubuffer1, vertexformat$indextype);
                    renderpass.drawIndexed(0, 0, p_397523_.drawState().indexCount(), 1);
                }
            } catch (Throwable throwable2) {
                if (p_397523_ != null) {
                    try {
                        meshdata.close();
                    } catch (Throwable throwable) {
                        throwable2.addSuppressed(throwable);
                    }
                }

                throw throwable2;
            }

            if (p_397523_ != null) {
                p_397523_.close();
            }

            this.clearRenderState();
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + this.state + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class CompositeState {
        final RenderStateShard.EmptyTextureStateShard textureState;
        final RenderStateShard.OutputStateShard outputState;
        final RenderType.OutlineProperty outlineProperty;
        final ImmutableList<RenderStateShard> states;

        CompositeState(
            RenderStateShard.EmptyTextureStateShard p_286632_,
            RenderStateShard.LightmapStateShard p_286744_,
            RenderStateShard.OverlayStateShard p_286754_,
            RenderStateShard.LayeringStateShard p_286895_,
            RenderStateShard.OutputStateShard p_286435_,
            RenderStateShard.TexturingStateShard p_286893_,
            RenderStateShard.LineStateShard p_286768_,
            RenderType.OutlineProperty p_286290_
        ) {
            this.textureState = p_286632_;
            this.outputState = p_286435_;
            this.outlineProperty = p_286290_;
            this.states = ImmutableList.of(p_286632_, p_286744_, p_286754_, p_286895_, p_286435_, p_286893_, p_286768_);
        }

        @Override
        public String toString() {
            return "CompositeState[" + this.states + ", outlineProperty=" + this.outlineProperty + "]";
        }

        public static RenderType.CompositeState.CompositeStateBuilder builder() {
            return new RenderType.CompositeState.CompositeStateBuilder();
        }

        @OnlyIn(Dist.CLIENT)
        public static class CompositeStateBuilder {
            private RenderStateShard.EmptyTextureStateShard textureState = RenderStateShard.NO_TEXTURE;
            private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
            private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
            private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
            private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
            private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
            private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;

            public RenderType.CompositeState.CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard p_173291_) {
                this.textureState = p_173291_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard p_110672_) {
                this.lightmapState = p_110672_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard p_110678_) {
                this.overlayState = p_110678_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard p_110670_) {
                this.layeringState = p_110670_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard p_110676_) {
                this.outputState = p_110676_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard p_110684_) {
                this.texturingState = p_110684_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLineState(RenderStateShard.LineStateShard p_110674_) {
                this.lineState = p_110674_;
                return this;
            }

            public RenderType.CompositeState createCompositeState(boolean p_110692_) {
                return this.createCompositeState(p_110692_ ? RenderType.OutlineProperty.AFFECTS_OUTLINE : RenderType.OutlineProperty.NONE);
            }

            public RenderType.CompositeState createCompositeState(RenderType.OutlineProperty p_110690_) {
                return new RenderType.CompositeState(
                    this.textureState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.lineState, p_110690_
                );
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum OutlineProperty {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        private OutlineProperty(final String p_110702_) {
            this.name = p_110702_;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
