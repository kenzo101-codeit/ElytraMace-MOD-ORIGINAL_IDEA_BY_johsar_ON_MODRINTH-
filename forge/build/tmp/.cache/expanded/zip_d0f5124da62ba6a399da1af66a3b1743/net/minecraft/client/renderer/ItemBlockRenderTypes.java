package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemBlockRenderTypes {
    @Deprecated
    private static final Map<Block, ChunkSectionLayer> TYPE_BY_BLOCK = Util.make(Maps.newHashMap(), p_420870_ -> {
        ChunkSectionLayer chunksectionlayer = ChunkSectionLayer.TRIPWIRE;
        p_420870_.put(Blocks.TRIPWIRE, chunksectionlayer);
        ChunkSectionLayer chunksectionlayer1 = ChunkSectionLayer.CUTOUT_MIPPED;
        p_420870_.put(Blocks.GRASS_BLOCK, chunksectionlayer1);
        p_420870_.put(Blocks.IRON_BARS, chunksectionlayer1);
        Blocks.COPPER_BARS.forEach(p_420873_ -> p_420870_.put(p_420873_, chunksectionlayer1));
        p_420870_.put(Blocks.GLASS_PANE, chunksectionlayer1);
        p_420870_.put(Blocks.TRIPWIRE_HOOK, chunksectionlayer1);
        p_420870_.put(Blocks.HOPPER, chunksectionlayer1);
        p_420870_.put(Blocks.IRON_CHAIN, chunksectionlayer1);
        Blocks.COPPER_CHAIN.forEach(p_420866_ -> p_420870_.put(p_420866_, chunksectionlayer1));
        p_420870_.put(Blocks.JUNGLE_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.OAK_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.SPRUCE_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.ACACIA_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.CHERRY_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.BIRCH_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.DARK_OAK_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.PALE_OAK_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.AZALEA_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.FLOWERING_AZALEA_LEAVES, chunksectionlayer1);
        p_420870_.put(Blocks.MANGROVE_ROOTS, chunksectionlayer1);
        p_420870_.put(Blocks.MANGROVE_LEAVES, chunksectionlayer1);
        ChunkSectionLayer chunksectionlayer2 = ChunkSectionLayer.CUTOUT;
        p_420870_.put(Blocks.OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.SPRUCE_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.BIRCH_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.JUNGLE_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.ACACIA_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.CHERRY_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.DARK_OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.PALE_OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.GLASS, chunksectionlayer2);
        p_420870_.put(Blocks.WHITE_BED, chunksectionlayer2);
        p_420870_.put(Blocks.ORANGE_BED, chunksectionlayer2);
        p_420870_.put(Blocks.MAGENTA_BED, chunksectionlayer2);
        p_420870_.put(Blocks.LIGHT_BLUE_BED, chunksectionlayer2);
        p_420870_.put(Blocks.YELLOW_BED, chunksectionlayer2);
        p_420870_.put(Blocks.LIME_BED, chunksectionlayer2);
        p_420870_.put(Blocks.PINK_BED, chunksectionlayer2);
        p_420870_.put(Blocks.GRAY_BED, chunksectionlayer2);
        p_420870_.put(Blocks.LIGHT_GRAY_BED, chunksectionlayer2);
        p_420870_.put(Blocks.CYAN_BED, chunksectionlayer2);
        p_420870_.put(Blocks.PURPLE_BED, chunksectionlayer2);
        p_420870_.put(Blocks.BLUE_BED, chunksectionlayer2);
        p_420870_.put(Blocks.BROWN_BED, chunksectionlayer2);
        p_420870_.put(Blocks.GREEN_BED, chunksectionlayer2);
        p_420870_.put(Blocks.RED_BED, chunksectionlayer2);
        p_420870_.put(Blocks.BLACK_BED, chunksectionlayer2);
        p_420870_.put(Blocks.POWERED_RAIL, chunksectionlayer2);
        p_420870_.put(Blocks.DETECTOR_RAIL, chunksectionlayer2);
        p_420870_.put(Blocks.COBWEB, chunksectionlayer2);
        p_420870_.put(Blocks.SHORT_GRASS, chunksectionlayer2);
        p_420870_.put(Blocks.FERN, chunksectionlayer2);
        p_420870_.put(Blocks.BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.SHORT_DRY_GRASS, chunksectionlayer2);
        p_420870_.put(Blocks.TALL_DRY_GRASS, chunksectionlayer2);
        p_420870_.put(Blocks.SEAGRASS, chunksectionlayer2);
        p_420870_.put(Blocks.TALL_SEAGRASS, chunksectionlayer2);
        p_420870_.put(Blocks.DANDELION, chunksectionlayer2);
        p_420870_.put(Blocks.OPEN_EYEBLOSSOM, chunksectionlayer2);
        p_420870_.put(Blocks.CLOSED_EYEBLOSSOM, chunksectionlayer2);
        p_420870_.put(Blocks.POPPY, chunksectionlayer2);
        p_420870_.put(Blocks.BLUE_ORCHID, chunksectionlayer2);
        p_420870_.put(Blocks.ALLIUM, chunksectionlayer2);
        p_420870_.put(Blocks.AZURE_BLUET, chunksectionlayer2);
        p_420870_.put(Blocks.RED_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.ORANGE_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.WHITE_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.PINK_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.OXEYE_DAISY, chunksectionlayer2);
        p_420870_.put(Blocks.CORNFLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.WITHER_ROSE, chunksectionlayer2);
        p_420870_.put(Blocks.LILY_OF_THE_VALLEY, chunksectionlayer2);
        p_420870_.put(Blocks.BROWN_MUSHROOM, chunksectionlayer2);
        p_420870_.put(Blocks.RED_MUSHROOM, chunksectionlayer2);
        p_420870_.put(Blocks.TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.WALL_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.SOUL_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.SOUL_WALL_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.COPPER_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.COPPER_WALL_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.FIRE, chunksectionlayer2);
        p_420870_.put(Blocks.SOUL_FIRE, chunksectionlayer2);
        p_420870_.put(Blocks.SPAWNER, chunksectionlayer2);
        p_420870_.put(Blocks.TRIAL_SPAWNER, chunksectionlayer2);
        p_420870_.put(Blocks.VAULT, chunksectionlayer2);
        p_420870_.put(Blocks.REDSTONE_WIRE, chunksectionlayer2);
        p_420870_.put(Blocks.WHEAT, chunksectionlayer2);
        p_420870_.put(Blocks.OAK_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.LADDER, chunksectionlayer2);
        p_420870_.put(Blocks.RAIL, chunksectionlayer2);
        p_420870_.put(Blocks.IRON_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.REDSTONE_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.REDSTONE_WALL_TORCH, chunksectionlayer2);
        p_420870_.put(Blocks.CACTUS, chunksectionlayer2);
        p_420870_.put(Blocks.SUGAR_CANE, chunksectionlayer2);
        p_420870_.put(Blocks.REPEATER, chunksectionlayer2);
        p_420870_.put(Blocks.OAK_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.SPRUCE_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.BIRCH_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.JUNGLE_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.ACACIA_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.CHERRY_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.DARK_OAK_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.PALE_OAK_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.CRIMSON_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WARPED_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.MANGROVE_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.BAMBOO_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.EXPOSED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WEATHERED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.ATTACHED_PUMPKIN_STEM, chunksectionlayer2);
        p_420870_.put(Blocks.ATTACHED_MELON_STEM, chunksectionlayer2);
        p_420870_.put(Blocks.PUMPKIN_STEM, chunksectionlayer2);
        p_420870_.put(Blocks.MELON_STEM, chunksectionlayer2);
        p_420870_.put(Blocks.VINE, chunksectionlayer2);
        p_420870_.put(Blocks.PALE_MOSS_CARPET, chunksectionlayer2);
        p_420870_.put(Blocks.PALE_HANGING_MOSS, chunksectionlayer2);
        p_420870_.put(Blocks.GLOW_LICHEN, chunksectionlayer2);
        p_420870_.put(Blocks.RESIN_CLUMP, chunksectionlayer2);
        p_420870_.put(Blocks.LILY_PAD, chunksectionlayer2);
        p_420870_.put(Blocks.NETHER_WART, chunksectionlayer2);
        p_420870_.put(Blocks.BREWING_STAND, chunksectionlayer2);
        p_420870_.put(Blocks.COCOA, chunksectionlayer2);
        p_420870_.put(Blocks.BEACON, chunksectionlayer2);
        p_420870_.put(Blocks.FLOWER_POT, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_SPRUCE_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_BIRCH_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_JUNGLE_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_ACACIA_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CHERRY_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_DARK_OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_PALE_OAK_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_MANGROVE_PROPAGULE, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_FERN, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_DANDELION, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_POPPY, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_OPEN_EYEBLOSSOM, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CLOSED_EYEBLOSSOM, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_BLUE_ORCHID, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_ALLIUM, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_AZURE_BLUET, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_RED_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_ORANGE_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_WHITE_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_PINK_TULIP, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_OXEYE_DAISY, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CORNFLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_LILY_OF_THE_VALLEY, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_WITHER_ROSE, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_RED_MUSHROOM, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_BROWN_MUSHROOM, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_DEAD_BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CACTUS, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_AZALEA, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_FLOWERING_AZALEA, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_TORCHFLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.CARROTS, chunksectionlayer2);
        p_420870_.put(Blocks.POTATOES, chunksectionlayer2);
        p_420870_.put(Blocks.COMPARATOR, chunksectionlayer2);
        p_420870_.put(Blocks.ACTIVATOR_RAIL, chunksectionlayer2);
        p_420870_.put(Blocks.IRON_TRAPDOOR, chunksectionlayer2);
        p_420870_.put(Blocks.SUNFLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.LILAC, chunksectionlayer2);
        p_420870_.put(Blocks.ROSE_BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.PEONY, chunksectionlayer2);
        p_420870_.put(Blocks.TALL_GRASS, chunksectionlayer2);
        p_420870_.put(Blocks.LARGE_FERN, chunksectionlayer2);
        p_420870_.put(Blocks.SPRUCE_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.BIRCH_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.JUNGLE_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.ACACIA_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.CHERRY_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.DARK_OAK_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.PALE_OAK_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.MANGROVE_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.BAMBOO_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.EXPOSED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WEATHERED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.OXIDIZED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.END_ROD, chunksectionlayer2);
        p_420870_.put(Blocks.CHORUS_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.CHORUS_FLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.TORCHFLOWER, chunksectionlayer2);
        p_420870_.put(Blocks.TORCHFLOWER_CROP, chunksectionlayer2);
        p_420870_.put(Blocks.PITCHER_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.PITCHER_CROP, chunksectionlayer2);
        p_420870_.put(Blocks.BEETROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.KELP, chunksectionlayer2);
        p_420870_.put(Blocks.KELP_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.TURTLE_EGG, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_TUBE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BRAIN_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BUBBLE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_FIRE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_HORN_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.TUBE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.BRAIN_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.BUBBLE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.FIRE_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.HORN_CORAL, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_TUBE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BRAIN_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BUBBLE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_FIRE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_HORN_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.TUBE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.BRAIN_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.BUBBLE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.FIRE_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.HORN_CORAL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.TUBE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.BRAIN_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.BUBBLE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.FIRE_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.HORN_CORAL_WALL_FAN, chunksectionlayer2);
        p_420870_.put(Blocks.SEA_PICKLE, chunksectionlayer2);
        p_420870_.put(Blocks.CONDUIT, chunksectionlayer2);
        p_420870_.put(Blocks.BAMBOO_SAPLING, chunksectionlayer2);
        p_420870_.put(Blocks.BAMBOO, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_BAMBOO, chunksectionlayer2);
        p_420870_.put(Blocks.SCAFFOLDING, chunksectionlayer2);
        p_420870_.put(Blocks.STONECUTTER, chunksectionlayer2);
        p_420870_.put(Blocks.LANTERN, chunksectionlayer2);
        p_420870_.put(Blocks.SOUL_LANTERN, chunksectionlayer2);
        Blocks.COPPER_LANTERN.forEach(p_420869_ -> p_420870_.put(p_420869_, chunksectionlayer2));
        p_420870_.put(Blocks.CAMPFIRE, chunksectionlayer2);
        p_420870_.put(Blocks.SOUL_CAMPFIRE, chunksectionlayer2);
        p_420870_.put(Blocks.SWEET_BERRY_BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.WEEPING_VINES, chunksectionlayer2);
        p_420870_.put(Blocks.WEEPING_VINES_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.TWISTING_VINES, chunksectionlayer2);
        p_420870_.put(Blocks.TWISTING_VINES_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.NETHER_SPROUTS, chunksectionlayer2);
        p_420870_.put(Blocks.CRIMSON_FUNGUS, chunksectionlayer2);
        p_420870_.put(Blocks.WARPED_FUNGUS, chunksectionlayer2);
        p_420870_.put(Blocks.CRIMSON_ROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.WARPED_ROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CRIMSON_FUNGUS, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_WARPED_FUNGUS, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_CRIMSON_ROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.POTTED_WARPED_ROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.CRIMSON_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.WARPED_DOOR, chunksectionlayer2);
        p_420870_.put(Blocks.POINTED_DRIPSTONE, chunksectionlayer2);
        p_420870_.put(Blocks.SMALL_AMETHYST_BUD, chunksectionlayer2);
        p_420870_.put(Blocks.MEDIUM_AMETHYST_BUD, chunksectionlayer2);
        p_420870_.put(Blocks.LARGE_AMETHYST_BUD, chunksectionlayer2);
        p_420870_.put(Blocks.AMETHYST_CLUSTER, chunksectionlayer2);
        p_420870_.put(Blocks.CAVE_VINES, chunksectionlayer2);
        p_420870_.put(Blocks.CAVE_VINES_PLANT, chunksectionlayer2);
        p_420870_.put(Blocks.SPORE_BLOSSOM, chunksectionlayer2);
        p_420870_.put(Blocks.FLOWERING_AZALEA, chunksectionlayer2);
        p_420870_.put(Blocks.AZALEA, chunksectionlayer2);
        p_420870_.put(Blocks.PINK_PETALS, chunksectionlayer2);
        p_420870_.put(Blocks.WILDFLOWERS, chunksectionlayer2);
        p_420870_.put(Blocks.LEAF_LITTER, chunksectionlayer2);
        p_420870_.put(Blocks.BIG_DRIPLEAF, chunksectionlayer2);
        p_420870_.put(Blocks.BIG_DRIPLEAF_STEM, chunksectionlayer2);
        p_420870_.put(Blocks.SMALL_DRIPLEAF, chunksectionlayer2);
        p_420870_.put(Blocks.HANGING_ROOTS, chunksectionlayer2);
        p_420870_.put(Blocks.SCULK_SENSOR, chunksectionlayer2);
        p_420870_.put(Blocks.CALIBRATED_SCULK_SENSOR, chunksectionlayer2);
        p_420870_.put(Blocks.SCULK_VEIN, chunksectionlayer2);
        p_420870_.put(Blocks.SCULK_SHRIEKER, chunksectionlayer2);
        p_420870_.put(Blocks.MANGROVE_PROPAGULE, chunksectionlayer2);
        p_420870_.put(Blocks.FROGSPAWN, chunksectionlayer2);
        p_420870_.put(Blocks.COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.EXPOSED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.WEATHERED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.OXIDIZED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, chunksectionlayer2);
        p_420870_.put(Blocks.FIREFLY_BUSH, chunksectionlayer2);
        p_420870_.put(Blocks.CACTUS_FLOWER, chunksectionlayer2);
        ChunkSectionLayer chunksectionlayer3 = ChunkSectionLayer.TRANSLUCENT;
        p_420870_.put(Blocks.ICE, chunksectionlayer3);
        p_420870_.put(Blocks.NETHER_PORTAL, chunksectionlayer3);
        p_420870_.put(Blocks.WHITE_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.ORANGE_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.MAGENTA_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.LIGHT_BLUE_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.YELLOW_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.LIME_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.PINK_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.GRAY_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.LIGHT_GRAY_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.CYAN_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.PURPLE_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.BLUE_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.BROWN_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.GREEN_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.RED_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.BLACK_STAINED_GLASS, chunksectionlayer3);
        p_420870_.put(Blocks.WHITE_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.ORANGE_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.MAGENTA_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.YELLOW_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.LIME_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.PINK_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.GRAY_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.CYAN_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.PURPLE_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.BLUE_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.BROWN_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.GREEN_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.RED_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.BLACK_STAINED_GLASS_PANE, chunksectionlayer3);
        p_420870_.put(Blocks.SLIME_BLOCK, chunksectionlayer3);
        p_420870_.put(Blocks.HONEY_BLOCK, chunksectionlayer3);
        p_420870_.put(Blocks.FROSTED_ICE, chunksectionlayer3);
        p_420870_.put(Blocks.BUBBLE_COLUMN, chunksectionlayer3);
        p_420870_.put(Blocks.TINTED_GLASS, chunksectionlayer3);
    });
    @Deprecated
    private static final Map<Fluid, ChunkSectionLayer> LAYER_BY_FLUID = Util.make(Maps.newHashMap(), p_404902_ -> {
        p_404902_.put(Fluids.FLOWING_WATER, ChunkSectionLayer.TRANSLUCENT);
        p_404902_.put(Fluids.WATER, ChunkSectionLayer.TRANSLUCENT);
    });
    private static boolean renderCutout;

    /** @deprecated Forge: Use {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
    @Deprecated // Note: this method does NOT support model-based render types
    public static ChunkSectionLayer getChunkRenderType(BlockState p_109283_) {
        Block block = p_109283_.getBlock();
        if (block instanceof LeavesBlock) {
            return renderCutout ? ChunkSectionLayer.CUTOUT_MIPPED : ChunkSectionLayer.SOLID;
        } else {
            ChunkSectionLayer chunksectionlayer = TYPE_BY_BLOCK.get(block);
            return chunksectionlayer != null ? chunksectionlayer : ChunkSectionLayer.SOLID;
        }
    }

    /** @deprecated Forge: Use {@link net.minecraftforge.client.RenderTypeHelper#getMovingBlockRenderType(ChunkSectionLayer)} while iterating through {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
    @Deprecated // Note: this method does NOT support model-based render types
    public static RenderType getMovingBlockRenderType(BlockState p_109294_) {
        Block block = p_109294_.getBlock();
        if (block instanceof LeavesBlock) {
            return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
        } else {
            ChunkSectionLayer chunksectionlayer = TYPE_BY_BLOCK.get(block);
            if (chunksectionlayer != null) {
                return switch (chunksectionlayer) {
                    case SOLID -> RenderType.solid();
                    case CUTOUT_MIPPED -> RenderType.cutoutMipped();
                    case CUTOUT -> RenderType.cutout();
                    case TRANSLUCENT -> RenderType.translucentMovingBlock();
                    case TRIPWIRE -> RenderType.tripwire();
                };
            } else {
                return RenderType.solid();
            }
        }
    }

    /** @deprecated Forge: Use {@link net.minecraftforge.client.RenderTypeHelper#getEntityRenderType(ChunkSectionLayer)} while iterating through {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
    @Deprecated // Note: this method does NOT support model-based render types
    public static RenderType getRenderType(BlockState p_364446_) {
        ChunkSectionLayer chunksectionlayer = getChunkRenderType(p_364446_);
        return chunksectionlayer == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentItemSheet() : Sheets.cutoutBlockSheet();
    }

    /* Forge: Use {@link net.minecraft.client.resources.model.BakedModel#getRenderPasses(ItemStack, boolean)} and {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(ItemStack, boolean)}. */
    public static RenderType getRenderType(ItemStack p_363859_) {
        if (p_363859_.getItem() instanceof BlockItem blockitem) {
            Block block = blockitem.getBlock();
            return getRenderType(block.defaultBlockState());
        } else {
            return Sheets.translucentItemSheet();
        }
    }

    // Note: this method does NOT support model-based render types
    public static ChunkSectionLayer getRenderLayer(FluidState p_109288_) {
        var chunksectionlayer = FLUID_RENDER_TYPES.get(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(p_109288_.getType()));
        return chunksectionlayer != null ? chunksectionlayer : ChunkSectionLayer.SOLID;
    }

    public static void setFancy(boolean p_109292_) {
        renderCutout = p_109292_;
    }

    /** Forge: Check if we are running in {@linkplain net.minecraft.client.Minecraft#useFancyGraphics() fancy graphics} to account for fast graphics render types */
    public static boolean isFancy() {
        return renderCutout;
    }

    private static final java.util.Collection<ChunkSectionLayer> CUTOUT_MIPPED = java.util.EnumSet.of(ChunkSectionLayer.CUTOUT_MIPPED);
    private static final java.util.Collection<ChunkSectionLayer> SOLID = java.util.EnumSet.of(ChunkSectionLayer.SOLID);
    private static final Map<net.minecraft.core.Holder.Reference<Block>, java.util.Collection<ChunkSectionLayer>> BLOCK_RENDER_TYPES = Util.make(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(TYPE_BY_BLOCK.size(), 0.5F), map -> {
       map.defaultReturnValue(SOLID);
       for(Map.Entry<Block, ChunkSectionLayer> entry : TYPE_BY_BLOCK.entrySet())
          map.put(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(entry.getKey()), java.util.EnumSet.of(entry.getValue()));
    });
    private static final Map<net.minecraft.core.Holder.Reference<Fluid>, ChunkSectionLayer> FLUID_RENDER_TYPES = Util.make(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(LAYER_BY_FLUID.size(), 0.5F), map -> {
       map.defaultReturnValue(ChunkSectionLayer.SOLID);
       for(var entry : LAYER_BY_FLUID.entrySet())
          map.put(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(entry.getKey()), entry.getValue());
    });

    /** @deprecated Use {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
    public static java.util.Collection<ChunkSectionLayer> getRenderLayers(BlockState state) {
       Block block = state.getBlock();
       if (block instanceof LeavesBlock) {
          return renderCutout ? CUTOUT_MIPPED : SOLID;
       } else {
          return BLOCK_RENDER_TYPES.get(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
       }
    }

    /**
     * It is recommended to set your render type in your block model's JSON (eg. {@code "render_type": "cutout"}) so that it can be data driven.
     * But if you want to set it in code feel free to set it here like vanilla does.
     */
    public static void setRenderLayer(Block block, ChunkSectionLayer type) {
       checkClientLoading();
       BLOCK_RENDER_TYPES.put(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(block), java.util.EnumSet.of(type));
    }

    /**
     * It is recommended to set your render type in your block model's JSON (eg. {@code "render_type": "cutout"}) so that it can be data driven.
     * But if you want to set it in code feel free to set it here like vanilla does.
     */
    public static synchronized void setRenderLayer(Block block, ChunkSectionLayer first, ChunkSectionLayer... others) {
       checkClientLoading();
       BLOCK_RENDER_TYPES.put(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(block), java.util.EnumSet.of(first, others));
    }

    public static synchronized void setRenderLayer(Fluid fluid, ChunkSectionLayer type) {
       checkClientLoading();
       FLUID_RENDER_TYPES.put(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid), type);
    }

    private static void checkClientLoading() {
       com.google.common.base.Preconditions.checkState(net.minecraftforge.client.loading.ClientModLoader.isLoading(),
               "Render layers can only be set during client loading! " +
                       "This might ideally be done from `FMLClientSetupEvent`."
       );
    }
}
