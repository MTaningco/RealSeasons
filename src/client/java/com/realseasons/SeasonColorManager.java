package com.realseasons;

import com.google.gson.Gson;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.realseasons.SeasonUtil.getSeasonIndex;
import static com.realseasons.SeasonUtil.getYearProgress;

public class SeasonColorManager {
    private static final int SPRING = 0x66BB44;
    private static final int SUMMER = 0x3FAF2A;
    private static final int FALL   = 0xD67D1F;
    private static final int WINTER = 0xCFE8FF;

    //The default colors signify a group of biomes
//    private static final int FROZEN_OCEAN = -8342377;

    public static byte[] x123() throws IOException {
//        Identifier id = Identifier.of("real-seasons", "real-seasons/seasons/fall_foliage.json");
//
//        Resource resource = MinecraftClient.getInstance()
//                .getResourceManager()
//                .getResource(id)
//                .orElseThrow();

//        InputStream stream = resource.getInputStream();
        InputStream stream = SeasonColorManager.class.getResourceAsStream(
                "/assets/real-seasons/seasons/fall_foliage.json"
        );

        assert stream != null;
        ColorMapConfig config = new Gson().fromJson(
                new InputStreamReader(stream),
                ColorMapConfig.class);

        BufferedImage grass = generateFromConfig(config);
        System.out.println("i got here to generateFromConfig");

        return toPngBytes(grass);
    }

    public static BufferedImage generateFromConfig(ColorMapConfig config) {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        // 1. Fill with a base color (important!)
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x0000FF)); // fallback green
        g.fillRect(0, 0, 256, 256);
        g.dispose();

        // 2. Apply your defined points
        for (ColorPoint p : config.values) {
            System.out.println(p);
            if (p.x >= 0 && p.x < 256 && p.y >= 0 && p.y < 256) {
                img.setRGB(p.x, p.y, p.color);
            }
        }

        return img;
    }

    public static byte[] toPngBytes(BufferedImage image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static int getGrassColor(BlockRenderView world, BlockPos pos) {
//
//        // 🟢 1. Get vanilla biome color
//        int vanilla = BiomeColors.getGrassColor(world, pos);
//
//        // 🎨 2. Get seasonal color
//        int seasonal = getSeasonalColor();
//
//        // 🔥 3. Blend them (this is your line)
//        return lerpColor(vanilla, seasonal, 0.6f);
//    }

    public static int getGrassColor2(BlockRenderView world, BlockPos pos, ColorMapConfig2 config) throws Exception {
         float t = getYearProgress();

        RegistryEntry<Biome> entry = world.getBiomeFabric(pos);

        if (entry == null) {
            int x = world.getColor(pos, new ColorResolver() {
                @Override
                public int getColor(Biome biome, double x, double z) {
                    int asdf = biome.getGrassColorAt(x, z);
                    String biomeId = config.defaultGrassColorToBiomeGroupMap.get(asdf);
                    if (config.defaultGrassColorToBiomeGroupMap.get(asdf) == null) {
                        System.out.println("Biome with grass color " + asdf + " was not in the mapping. Please add it in.");
                    }
                    int leftSeason = getGrassBiomeSeason(biomeId, t, config);
                    int rightSeason = getGrassBiomeSeason(biomeId, t + 0.25f, config);

                    float scaled = t * 4;
                    int index = (int) scaled;
                    float localT = scaled - index;

                    return lerpColor(leftSeason, rightSeason, localT);
                }
            });
            return x;
        }

        String biomeId = entry.getKey().get().getValue().getPath();

        int leftSeason = getGrassBiomeSeason(biomeId, t, config);
        int rightSeason = getGrassBiomeSeason(biomeId, t + 0.25f, config);

        float scaled = t * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        return lerpColor(leftSeason, rightSeason, localT);
    }

    public static int getGrassBiomeSeason(String biomeId, float t, ColorMapConfig2 config) {
        int seasonIndex = getSeasonIndex(t);

        if (!config.biomeIdToSeasonArrayMap.containsKey(biomeId)) {
            System.out.println(biomeId + " was not in the seasonsGrassConfig. Please add it in");
        }
        return config.biomeIdToSeasonArrayMap.getOrDefault(biomeId, new int[]{0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF})[seasonIndex];
    }

    public static int getBlendedGrassColor(BlockRenderView world, BlockPos pos, ColorMapConfig2 config) throws Exception {

        int radius = 2; // same idea as vanilla
        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {

                BlockPos samplePos = pos.add(dx, 0, dz);

//                int color = getGrassBiomeSeasonUsingDefaultGrassColor(world, samplePos);
                int color = getGrassColor2(world, samplePos, config);

                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;

                count++;
            }
        }

        return (r / count << 16) | (g / count << 8) | (b / count);
    }

    public static int getBlendedFoliageColor(BlockRenderView world, BlockPos pos, BlockState state, ColorMapConfig3 config) {
        int radius = 2; // same idea as vanilla
        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {

                BlockPos samplePos = pos.add(dx, 0, dz);

//                int color = getGrassBiomeSeasonUsingDefaultGrassColor(world, samplePos);
                int color = getFoliageColor2(world, samplePos, state, config);

                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;

                count++;
            }
        }

        return (r / count << 16) | (g / count << 8) | (b / count);
    }

    public static int getFoliageColor2(BlockRenderView world, BlockPos pos, BlockState state, ColorMapConfig3 config) {
        float t = getYearProgress();

        RegistryEntry<Biome> entry = world.getBiomeFabric(pos);

        if (entry == null) {
            int x = world.getColor(pos, new ColorResolver() {
                @Override
                public int getColor(Biome biome, double x, double z) {
                    int asdf = biome.getGrassColorAt(x, z);
                    String biomeId = config.defaultGrassColorToBiomeGroupMap.get(asdf);
                    int leftSeason = getFoliageBiomeSeason(biomeId, state, t, config);
                    int rightSeason = getFoliageBiomeSeason(biomeId, state, t + 0.25f, config);

                    float scaled = t * 4;
                    int index = (int) scaled;
                    float localT = scaled - index;

                    return lerpColor(leftSeason, rightSeason, localT);
                }
            });
            return x;
        }

        String biomeId = entry.getKey().get().getValue().getPath();

        int leftSeason = getFoliageBiomeSeason(biomeId, state, t, config);
        int rightSeason = getFoliageBiomeSeason(biomeId, state, t + 0.25f, config);

        float scaled = t * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        return lerpColor(leftSeason, rightSeason, localT);
    }

    public static int getFoliageBiomeSeason(String biomeId, BlockState state, float t, ColorMapConfig3 config) {
        int seasonIndex = getSeasonIndex(t);
        int[] defaultArray = new int[]{0x0000FF, 0x0000FF, 0x0000FF, 0x0000FF};

        if(state.isOf(Blocks.OAK_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("oak")) {
            return config.blockTypeToBiomeSeasonMap.get("oak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.SPRUCE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("spruce")) {
            return config.blockTypeToBiomeSeasonMap.get("spruce").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.BIRCH_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("birch")) {
            return config.blockTypeToBiomeSeasonMap.get("birch").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.JUNGLE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("jungle")) {
            return config.blockTypeToBiomeSeasonMap.get("jungle").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.ACACIA_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("acacia")) {
            return config.blockTypeToBiomeSeasonMap.get("acacia").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.DARK_OAK_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("darkOak")) {
            return config.blockTypeToBiomeSeasonMap.get("darkOak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.MANGROVE_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("mangrove")) {
            return config.blockTypeToBiomeSeasonMap.get("mangrove").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.PALE_OAK_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("paleOak")) {
            return config.blockTypeToBiomeSeasonMap.get("paleOak").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        if(state.isOf(Blocks.AZALEA_LEAVES) && config.blockTypeToBiomeSeasonMap.containsKey("azalea")) {
            return config.blockTypeToBiomeSeasonMap.get("azalea").getOrDefault(biomeId, defaultArray)[seasonIndex];
        }

        return defaultArray[seasonIndex];
    }







    // 🍃 Same idea for leaves

    public static int getLeafColor(BlockState state, BlockRenderView world, BlockPos pos) {
        int vanilla = BiomeColors.getFoliageColor(world, pos);
        float t = getYearProgress();
        int seasonal = getTreeSpecificColor(state, t);
        return lerpColor(vanilla, seasonal, 0.7f);
    }

    private static int getSeasonalColor() {
        float t = getYearProgress();

        float scaled = t * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        int c1, c2;

//        System.out.println("---");
//
//        System.out.println(index);
//
//        System.out.println("---");

        switch (index) {
            case 0 -> { c1 = SPRING; c2 = SUMMER; }
            case 1 -> { c1 = SUMMER; c2 = FALL; }
            case 2 -> { c1 = FALL; c2 = WINTER; }
            default -> { c1 = WINTER; c2 = SPRING; }
        }

        return lerpColor(c1, c2, localT);
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

    private static int getTreeSpecificColor(BlockState state, float t) {

        if (state.isOf(Blocks.OAK_LEAVES) || state.isOf(Blocks.DARK_OAK_LEAVES)) {
            return getOakColor(t);
        }

        if (state.isOf(Blocks.BIRCH_LEAVES)) {
            return getBirchColor(t);
        }

        if (state.isOf(Blocks.JUNGLE_LEAVES)) {
            return getJungleColor(t);
        }

        if (state.isOf(Blocks.ACACIA_LEAVES)) {
            return getAcaciaColor(t);
        }

        if (state.isOf(Blocks.SPRUCE_LEAVES)) {
            return getSpruceColor(t);
        }

        return getSeasonalColor(); // fallback
    }

    private static int getOakColor(float t) {
        return interpolateSeasonsCustom(t,
                0x66BB44, // spring
                0x3FAF2A, // summer
                0xFF0000, // fall orange 0xD67D1F
                0x8B5A2B  // winter brown
        );
    }

    private static int getJungleColor(float t) {
        return interpolateSeasonsCustom(t,
                0x2ECC40,
                0x1FAA2E,
                0x1A8F28, // stays green
                0x3A5F3A  // slightly darker
        );
    }

    private static int getBirchColor(float t) {
        return interpolateSeasonsCustom(t,
                0x80A755,
                0x81B844,
                0xD66800, // bright yellow 🍂
                0x665026
        );
    }

    private static int getAcaciaColor(float t) {
        return interpolateSeasonsCustom(t,
                0x7FAF5F,
                0x6F9F4F,
                0xA87D3A, // dusty orange
                0x9C8F7A  // pale dry
        );
    }

    private static int getSpruceColor(float t) {
        return interpolateSeasonsCustom(t,
                0x2E5D3A,
                0x2A5535,
                0x2A5535, // no fall change
                0x3E6F5A  // slight winter tint
        );
    }

    private static int interpolateSeasonsCustom(float t, int spring, int summer, int fall, int winter) {

        float scaled = t * 4;
        int index = (int) scaled;
        float localT = scaled - index;

        int c1, c2;

        switch (index) {
            case 0 -> { c1 = spring; c2 = summer; }
            case 1 -> { c1 = summer; c2 = fall; }
            case 2 -> { c1 = fall; c2 = winter; }
            default -> { c1 = winter; c2 = spring; }
        }

        return lerpColor(c1, c2, localT);
    }
}
