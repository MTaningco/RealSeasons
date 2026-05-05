package com.realseasons;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class RealSeasonsConfig {
    public static ConfigClassHandler<RealSeasonsConfig> HANDLER = ConfigClassHandler.createBuilder(RealSeasonsConfig.class)
            .id(Identifier.fromNamespaceAndPath("real-seasons", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("real_seasons.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "Setting this to false will use real time. Setting this to true will use in-game time.")
    public boolean isGameDays = false;

    @SerialEntry(comment = "Defines how many subdivisions there are per season, with 4 seasons total.")
    public int subdivisionsPerSeason = 3;

    @SerialEntry(comment = "Defines how long a subdivision is in game days.")
    public int subdivisionLength = 5;

}
