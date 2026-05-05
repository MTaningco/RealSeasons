package com.realseasons;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Real Seasons Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Real Seasons Settings"))
                        .tooltip(Component.literal("Sets the configuration of Real Seasons."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Seasons Mode"))
                                .description(OptionDescription.of(Component.literal("Sets the mode for with seasons will progress. Checking this box will let it progress in real-time, otherwise in-game time will be used.")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Real Time Mode"))
                                        .description(OptionDescription.of(Component.literal("Setting this to true will use real time. Setting this to false will use in-game time.")))
                                        .binding(true, () -> !RealSeasonsConfig.HANDLER.instance().isGameDays, newVal -> RealSeasonsConfig.HANDLER.instance().isGameDays = !newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("In-Game Seasons Mode Settings"))
                                .description(OptionDescription.of(Component.literal("Settings for when the mode used is \"in-game days\". If the mode is then real time, this group of configurations is not necessary.")))
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Subdivision Length"))
                                        .description(OptionDescription.of(Component.literal("Defines how long a subdivision is in game days.")))
                                        .binding(5, () -> RealSeasonsConfig.HANDLER.instance().subdivisionLength, newVal -> RealSeasonsConfig.HANDLER.instance().subdivisionLength = newVal)
                                        .controller(
                                                opt ->  IntegerSliderControllerBuilder.create(opt).range(5, 200).step(1).formatValue(val -> Component.literal(val + " days"))
                                        )
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Subdivisions Per Season"))
                                        .description(OptionDescription.of(Component.literal("Defines how many subdivisions there are per season, with 4 seasons total.")))
                                        .binding(3, () -> RealSeasonsConfig.HANDLER.instance().subdivisionsPerSeason, newVal -> RealSeasonsConfig.HANDLER.instance().subdivisionsPerSeason = newVal)
                                        .controller(
                                                opt ->  IntegerSliderControllerBuilder.create(opt).range(1, 100).step(1).formatValue(val -> Component.literal(val + " subdivisions per season"))
                                        )
                                        .build())
                                .build())
                        .build())
                .save(() -> RealSeasonsConfig.HANDLER.save())
                .build()
                .generateScreen(parentScreen);
    }
}