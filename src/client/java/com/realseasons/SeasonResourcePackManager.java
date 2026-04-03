package com.realseasons;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeasonResourcePackManager {

    public static void applySeason(Season season) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null) return;

        var manager = client.getResourcePackManager();

        List<String> enabled = manager.getEnabledProfiles().stream()
                .map(ResourcePackProfile::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        manager.getProfiles().forEach(profile -> {
            System.out.println(profile.getId());
        });

        System.out.println(enabled);
        System.out.println("---");

        enabled.removeIf(name -> name.startsWith("real-seasons:season_"));

        System.out.println(enabled);
        System.out.println("---");

        switch (season) {

//            case SPRING -> enabled.add("real-seasons:season_spring");
            case SUMMER -> enabled.add("real-seasons:season_summer");
            case FALL -> enabled.add("real-seasons:season_fall");
            case WINTER -> enabled.add("real-seasons:season_winter");

        }

        manager.setEnabledProfiles(enabled);
        System.out.println(enabled);
        client.options.resourcePacks = enabled;
        client.options.incompatibleResourcePacks = List.of();

        manager.scanPacks();

        client.reloadResources();

    }

}