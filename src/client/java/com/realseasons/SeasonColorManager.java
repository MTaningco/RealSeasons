package com.realseasons;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

import static com.realseasons.RealSeasonsClient.currentInGameYearProgress;
import static com.realseasons.RealSeasonsClient.isGameDays;
import static com.realseasons.SeasonUtil.getSeasonIndex;
import static com.realseasons.SeasonUtil.getRealYearProgress;

public class SeasonColorManager {

    public static int getGrassColor(BlockAndTintGetter world, BlockPos pos, GrassColorMapConfig config) throws Exception {
        float yearProgressCoefficient = !isGameDays ? getRealYearProgress() : currentInGameYearProgress;

        Holder<Biome> entry = world.getBiomeFabric(pos);

        if (entry == null) {
            return world.getBlockTint(pos, (biome, x1, z) -> {
                int originalGrassColor = biome.getGrassColor(x1, z);
                String biomeId = config.defaultGrassColorToBiomeGroupMap.get(originalGrassColor);
                if (config.defaultGrassColorToBiomeGroupMap.get(originalGrassColor) == null && !RealSeasonsClient.unknownOriginalGrassColorSet.contains(originalGrassColor)) {
                    RealSeasonsClient.unknownOriginalGrassColorSet.add(originalGrassColor);
                    RealSeasonsClient.LOGGER.error("[Real Seasons]: Biome with grass color {} was not in the mapping. Please add it in.", originalGrassColor);
                }
                int leftSeason = getGrassBiomeSeason(biomeId, yearProgressCoefficient, config);
                int rightSeason = getGrassBiomeSeason(biomeId, yearProgressCoefficient + 0.25f, config);

                float scaled = yearProgressCoefficient * 4;
                int index = (int) scaled;
                float localT = scaled - index;

                return lerpColor(leftSeason, rightSeason, localT);
            });
        }

        String biomeId = entry.getRegisteredName();

        int leftSeason = getGrassBiomeSeason(biomeId, yearProgressCoefficient, config);
        int rightSeason = getGrassBiomeSeason(biomeId, yearProgressCoefficient + 0.25f, config);

        float scaled = yearProgressCoefficient * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        return lerpColor(leftSeason, rightSeason, localT);
    }

    public static int getGrassBiomeSeason(String biomeId, float yearProgressCoefficient, GrassColorMapConfig config) {
        int seasonIndex = getSeasonIndex(yearProgressCoefficient);

        if (!config.biomeIdToSeasonArrayMap.containsKey(biomeId) && !RealSeasonsClient.unknownBiomeIdsSet.contains(biomeId)) {
            RealSeasonsClient.unknownBiomeIdsSet.add(biomeId);
            RealSeasonsClient.LOGGER.error("[Real Seasons]: {} was not in the seasonsGrassConfig. Please add it in", biomeId);
        }
        return config.biomeIdToSeasonArrayMap.getOrDefault(biomeId, new int[]{0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF})[seasonIndex];
    }

    public static int getBlendedGrassColor(BlockAndTintGetter world, BlockPos pos, GrassColorMapConfig config) throws Exception {

        int radius = 2; // same idea as vanilla
        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {

                BlockPos samplePos = pos.offset(dx, 0, dz);

                int color = getGrassColor(world, samplePos, config);

                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;

                count++;
            }
        }

        return (0xFF << 24) | (r / count << 16) | (g / count << 8) | (b / count); // FF prefixed to handle transparency
    }

    public static int getBlendedFoliageColor(BlockAndTintGetter world, BlockPos pos, BlockState state, FoliageColorMapConfig config) {
        int radius = 2; // same idea as vanilla
        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {

                BlockPos samplePos = pos.offset(dx, 0, dz);

                int color = getFoliageColor(world, samplePos, state, config);

                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;

                count++;
            }
        }

        return (0xFF << 24) | (r / count << 16) | (g / count << 8) | (b / count); // FF prefixed to handle transparency
    }

    public static int getFoliageColor(BlockAndTintGetter world, BlockPos pos, BlockState state, FoliageColorMapConfig config) {
        float yearProgressCoefficient = !isGameDays ? getRealYearProgress() : currentInGameYearProgress;

        Holder<Biome> entry = world.getBiomeFabric(pos);

        if (entry == null) {
            return world.getBlockTint(pos, (biome, x1, z) -> {
                int originalGrassColor = biome.getGrassColor(x1, z);
                String biomeId = config.defaultGrassColorToBiomeGroupMap.get(originalGrassColor);
                int leftSeason = getFoliageBiomeSeason(biomeId, state, yearProgressCoefficient, config);
                int rightSeason = getFoliageBiomeSeason(biomeId, state, yearProgressCoefficient + 0.25f, config);

                float scaled = yearProgressCoefficient * 4;
                int index = (int) scaled;
                float localT = scaled - index;

                return lerpColor(leftSeason, rightSeason, localT);
            });
        }

        String biomeId = entry.getRegisteredName();

        int leftSeason = getFoliageBiomeSeason(biomeId, state, yearProgressCoefficient, config);
        int rightSeason = getFoliageBiomeSeason(biomeId, state, yearProgressCoefficient + 0.25f, config);

        float scaled = yearProgressCoefficient * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        return lerpColor(leftSeason, rightSeason, localT);
    }

    public static int getFoliageBiomeSeason(String biomeId, BlockState state, float yearProgressCoefficient, FoliageColorMapConfig config) {
        int seasonIndex = getSeasonIndex(yearProgressCoefficient);
        int[] defaultArray = new int[]{0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF};

        if((state.is(Blocks.OAK_LEAVES) || state.is(Blocks.VINE)) && config.blockTypeToBiomeSeasonMap.containsKey("oak")) {
            return config.blockTypeToBiomeSeasonMap.get("oak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.SPRUCE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("spruce")) {
            return config.blockTypeToBiomeSeasonMap.get("spruce").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.BIRCH_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("birch")) {
            return config.blockTypeToBiomeSeasonMap.get("birch").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.JUNGLE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("jungle")) {
            return config.blockTypeToBiomeSeasonMap.get("jungle").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.ACACIA_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("acacia")) {
            return config.blockTypeToBiomeSeasonMap.get("acacia").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.DARK_OAK_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("darkOak")) {
            return config.blockTypeToBiomeSeasonMap.get("darkOak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.MANGROVE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("mangrove")) {
            return config.blockTypeToBiomeSeasonMap.get("mangrove").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.PALE_OAK_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("paleOak")) {
            return config.blockTypeToBiomeSeasonMap.get("paleOak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.is(Blocks.AZALEA_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("azalea")) {
            return config.blockTypeToBiomeSeasonMap.get("azalea").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        return defaultArray[seasonIndex];
    }

    private static int lerpColor(int a, int b, float t) {

        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;

        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        int r = (int)(ar + (br - ar) * t);
        int g = (int)(ag + (bg - ag) * t);
        int bC = (int)(ab + (bb - ab) * t);

        return (r << 16) | (g << 8) | bC;
    }
}
