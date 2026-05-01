package com.realseasons;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class RealSeasonsClient implements ClientModInitializer {

	public static final boolean isGameDays = false;
	public static final int subdivisionsPerSeason = 3;
	public static final int yearLength = 60;

	public static final int MIN_SUBDIVISION_LENGTH = 5;
	public static final String MOD_ID = "real-seasons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final HashSet<Integer> unknownOriginalGrassColorSet = new HashSet<>();
	public static final HashSet<String> unknownBiomeIdsSet = new HashSet<>();

	public static float currentInGameYearProgress = 0f;

	@Override
	public void onInitializeClient() {
		if (isGameDays && (yearLength / (subdivisionsPerSeason * 4)) < MIN_SUBDIVISION_LENGTH) {
			RealSeasonsClient.LOGGER.error("[Real Seasons]: Current year length and subdivisions per season configs imply subdivision length of less than 5 in game days. Please change configs such that a subdivision is more than or equal to 5 days.", new UnsupportedOperationException());
			throw new UnsupportedOperationException("Year length and subdivision configs will cause too frequent of a reload.");
		}

		if (subdivisionsPerSeason < 1) {
			RealSeasonsClient.LOGGER.error("[Real Seasons]: Subdivisions per seasons config must be 1 or greater.", new UnsupportedOperationException());
			throw new UnsupportedOperationException("Subdivisions per seasons config must be 1 or greater.");
		}

		ColorMapConfig foliageConfig = generateColorMapConfig();

		// 🌿 GRASS
		BlockColorRegistry.register(List.of(new BlockTintSource() {
				@Override
				public int color(BlockState state) {
					return 0; // Dummy override just to satisfy interface
				}

				@Override
				public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
					try {
						return SeasonColorManager.getBlendedGrassColor(level, pos, foliageConfig.defaultGrassColorToBiomeGroupMap, foliageConfig.blockTypeToBiomeSeasonMap.get("grass"));
					} catch (Exception e) {
						return 0xFFFF00FF; // Obvious magenta color to make issue visible
					}
				}
			}),
			Blocks.GRASS_BLOCK,
			Blocks.SHORT_GRASS,
			Blocks.TALL_GRASS,
			Blocks.BAMBOO,
			Blocks.FERN,
			Blocks.LARGE_FERN,
			Blocks.POTTED_FERN,
			Blocks.SUGAR_CANE,
			Blocks.BUSH
		);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isGameDays && client.level != null) {
                long currentTicks = client.level.getOverworldClockTime();
				float currentClampedYearProgress = SeasonUtil.getClampedInGameYearProgress(currentTicks, subdivisionsPerSeason, yearLength);
                if (currentInGameYearProgress != currentClampedYearProgress) {
                    currentInGameYearProgress = currentClampedYearProgress;
                    Minecraft.getInstance().levelRenderer.allChanged();
                }
            }
        });

		BlockColorRegistry.register(List.of(new BlockTintSource() {

				@Override
				public int color(BlockState state) {
					return 0; // Dummy override just to satisfy interface
				}

				@Override
				public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
					try {
						return SeasonColorManager.getBlendedFoliageColor(level, pos, state, foliageConfig);
					} catch (Exception e) {
						return 0xFFFF00FF; // Obvious magenta color to make issue visible
					}
				}
			}),
			Blocks.OAK_LEAVES,
			Blocks.VINE,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.ACACIA_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.PALE_OAK_LEAVES,
			Blocks.MANGROVE_LEAVES
		);
	}

	private ColorMapConfig generateColorMapConfig() {
		InputStream stream = RealSeasonsClient.class.getResourceAsStream(
				"/assets/real-seasons/seasonsConfig.json"
		);
		Reader reader = new InputStreamReader(stream);

		Gson gson = new Gson();

		// parse array
		List<FoliageConfigEntry> entries = gson.fromJson(
				reader,
				new TypeToken<List<FoliageConfigEntry>>() {}.getType()
		);

		// convert to your structure
		ColorMapConfig config = new ColorMapConfig();
		config.defaultGrassColorToBiomeGroupMap = new HashMap<>();
		config.blockTypeToBiomeSeasonMap = new HashMap<>();

		for (FoliageConfigEntry entry : entries) {

			// biome -> seasonal colors
			HashMap<String, int[]> oakHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("oak", new HashMap<>());
			oakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.oak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("oak", oakHashMap);

			HashMap<String, int[]> spruceHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("spruce", new HashMap<>());
			spruceHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.spruce)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("spruce", spruceHashMap);

			HashMap<String, int[]> birchHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("birch", new HashMap<>());
			birchHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.birch)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("birch", birchHashMap);

			HashMap<String, int[]> jungleHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("jungle", new HashMap<>());
			jungleHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.jungle)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("jungle", jungleHashMap);

			HashMap<String, int[]> acaciaHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("acacia", new HashMap<>());
			acaciaHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.acacia)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("acacia", acaciaHashMap);

			HashMap<String, int[]> darkOakHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("darkOak", new HashMap<>());
			darkOakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.darkOak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("darkOak", darkOakHashMap);

			HashMap<String, int[]> mangroveHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("mangrove", new HashMap<>());
			mangroveHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.mangrove)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("mangrove", mangroveHashMap);

			HashMap<String, int[]> paleOakHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("paleOak", new HashMap<>());
			paleOakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.paleOak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("paleOak", paleOakHashMap);

			HashMap<String, int[]> azaleaHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("azalea", new HashMap<>());
			azaleaHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.azalea)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("azalea", azaleaHashMap);

			HashMap<String, int[]> grassHashMap = config.blockTypeToBiomeSeasonMap.getOrDefault("grass", new HashMap<>());
			grassHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.grass)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config.blockTypeToBiomeSeasonMap.put("grass", grassHashMap);


			// optional default color -> biome group
			if (entry.defaultDecimalColor != null) {
				config.defaultGrassColorToBiomeGroupMap.put(
						entry.defaultDecimalColor,
						entry.biomeId
				);
			}
		}
		return config;
	}
}
