package com.realseasons;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RealSeasonsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		InputStream stream = RealSeasonsClient.class.getResourceAsStream(
				                "/assets/real-seasons/seasonsGrassConfig.json"
		);
		Reader reader = new InputStreamReader(stream);

		Gson gson = new Gson();

		// 👇 parse array
		List<GrassConfigEntry> entries = gson.fromJson(
				reader,
				new TypeToken<List<GrassConfigEntry>>() {}.getType()
		);

		// 👇 convert to your structure
		ColorMapConfig2 config = new ColorMapConfig2();
		config.defaultGrassColorToBiomeGroupMap = new HashMap<>();
		config.biomeIdToSeasonArrayMap = new HashMap<>();

		for (GrassConfigEntry entry : entries) {

			// biome → seasonal colors
			config.biomeIdToSeasonArrayMap.put(
					entry.biomeId,
					Arrays.stream(entry.seasonColors)
							.mapToInt(Integer::decode)
							.toArray()
			);

			// optional default color → biome group
			if (entry.defaultDecimalColor != null) {
				config.defaultGrassColorToBiomeGroupMap.put(
						entry.defaultDecimalColor,
						entry.biomeId
				);
			}
		}


		InputStream stream2 = RealSeasonsClient.class.getResourceAsStream(
				"/assets/real-seasons/seasonsLeafConfig.json"
		);
		Reader reader2 = new InputStreamReader(stream2);

		Gson gson2 = new Gson();

		// 👇 parse array
		List<FoliageConfigEntry> entries2 = gson2.fromJson(
				reader2,
				new TypeToken<List<FoliageConfigEntry>>() {}.getType()
		);

		// 👇 convert to your structure
		ColorMapConfig3 config2 = new ColorMapConfig3();
		config2.defaultGrassColorToBiomeGroupMap = new HashMap<>();
		config2.blockTypeToBiomeSeasonMap = new HashMap<>();

		for (FoliageConfigEntry entry : entries2) {

			// biome → seasonal colors
			HashMap<String, int[]> oakHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("oak", new HashMap<>());
			oakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.oak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("oak", oakHashMap);

			HashMap<String, int[]> spruceHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("spruce", new HashMap<>());
			spruceHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.spruce)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("spruce", spruceHashMap);

			HashMap<String, int[]> birchHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("birch", new HashMap<>());
			birchHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.birch)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("birch", birchHashMap);

			HashMap<String, int[]> jungleHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("jungle", new HashMap<>());
			jungleHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.jungle)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("jungle", jungleHashMap);

			HashMap<String, int[]> acaciaHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("acacia", new HashMap<>());
			acaciaHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.acacia)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("acacia", acaciaHashMap);

			HashMap<String, int[]> darkOakHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("darkOak", new HashMap<>());
			darkOakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.darkOak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("darkOak", darkOakHashMap);

			HashMap<String, int[]> mangroveHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("mangrove", new HashMap<>());
			mangroveHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.mangrove)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("mangrove", mangroveHashMap);

			HashMap<String, int[]> paleOakHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("paleOak", new HashMap<>());
			paleOakHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.paleOak)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("paleOak", paleOakHashMap);

			HashMap<String, int[]> azaleaHashMap = config2.blockTypeToBiomeSeasonMap.getOrDefault("azalea", new HashMap<>());
			azaleaHashMap.put(
					entry.biomeId,
					Arrays.stream(entry.azalea)
							.mapToInt(Integer::decode)
							.toArray()
			);
			config2.blockTypeToBiomeSeasonMap.put("azalea", azaleaHashMap);


			// optional default color → biome group
			if (entry.defaultDecimalColor != null) {
				config2.defaultGrassColorToBiomeGroupMap.put(
						entry.defaultDecimalColor,
						entry.biomeId
				);
			}
		}



//		System.out.println(config.toString());

		// 🌿 GRASS
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {

                    try {
                        return SeasonColorManager.getBlendedGrassColor(world, pos, config);
                    } catch (Exception e) {
						return 0xFF0000;
//                        throw new UnsupportedOperationException (e);
                    }
                },
		Blocks.GRASS_BLOCK,
		Blocks.SHORT_GRASS,
		Blocks.TALL_GRASS,
		Blocks.BAMBOO,
		Blocks.FERN,
		Blocks.LARGE_FERN,
		Blocks.POTTED_FERN,
		Blocks.SUGAR_CANE
		);

		// 🍃 LEAVES
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
//					return SeasonColorManager.getLeafColor(state, world, pos);
					return SeasonColorManager.getBlendedFoliageColor(world, pos, state, config2);
				},
				Blocks.OAK_LEAVES,
				Blocks.VINE,
				Blocks.SPRUCE_LEAVES,
				Blocks.BIRCH_LEAVES,
				Blocks.JUNGLE_LEAVES,
				Blocks.ACACIA_LEAVES,
				Blocks.DARK_OAK_LEAVES,
				Blocks.PALE_OAK_LEAVES,
				Blocks.AZALEA_LEAVES,
				Blocks.MANGROVE_LEAVES
		);

//		ResourceManagerHelper.registerBuiltinResourcePack(
//				Identifier.of("realseasons", "seasonal"),
//				FabricLoader.getInstance().getModContainer("realseasons").get(),
//				Text.literal("Seasonal Colormap"),
//				ResourcePackActivationType.DEFAULT_ENABLED
//		);

		//SeasonalPackHolder.PACK = new SeasonalResourcePack();

//		BufferedImage map = SeasonColormapGenerator.generateGrassMap();
//
//		NativeImage img = TextureLoader.convert(map);
//
//		GrassColors.setColorMap(img);
//		FoliageColors.setColorMap(img);
	}
}
