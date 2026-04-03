package com.realseasons;

import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;


public class SeasonalResourcePack implements ResourcePack {

    //private final byte[] foliageBytes;
    private final byte[] grassBytes;

    public SeasonalResourcePack(byte[] grassBytes) {
        this.grassBytes = grassBytes;
        //this.foliageBytes = SeasonalColormaps.foliagePng;
    }
    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... segments) {
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) return null;

        if (id.getNamespace().equals("minecraft")) {
            if (id.getPath().equals("textures/colormap/grass.png")) {
                return () -> new ByteArrayInputStream(grassBytes);
            }

            //if (id.getPath().equals("textures/colormap/foliage.png")) {
             //   return () -> new ByteArrayInputStream(SeasonalColormaps.foliagePng);
            //}
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {

    }

//    @Override
//    public boolean contains(ResourceType type, Identifier id) {
//        return type == ResourceType.CLIENT_RESOURCES &&
//                id.getNamespace().equals("minecraft") &&
//                (id.getPath().equals("textures/colormap/grass.png") ||
//                        id.getPath().equals("textures/colormap/foliage.png"));
//    }

    @Override public Set<String> getNamespaces(ResourceType type) {
        return Set.of("minecraft");
    }

    @Override public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) {
        return null;
    }

    @Override
    public ResourcePackInfo getInfo() {
        return null;
    }

    @Override public String getId() {
        return "seasonal_colormap_pack";
    }

    @Override public void close() {}
}