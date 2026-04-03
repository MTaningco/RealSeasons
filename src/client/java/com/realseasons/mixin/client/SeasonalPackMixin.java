package com.realseasons.mixin.client;

import com.realseasons.SeasonColorManager;
import com.realseasons.SeasonalPackHolder;
import com.realseasons.SeasonalResourcePack;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(DefaultClientResourcePackProvider.class)
public class SeasonalPackMixin {

    @Inject(
            method = "forEachProfile",
            at = @At("TAIL")
    )
    private static void addSeasonalPack(BiConsumer<String, Function<String, ResourcePackProfile>> consumer, CallbackInfo ci) {

        System.out.println("hello there");
//        if (SeasonalPackHolder.PACK == null) return;

//        ResourcePackProfile profile = factory.open(
//                new ResourcePackInfo(
//                        "realseasons:seasonal_colormap",
//                        Text.literal("Seasonal Colormap"),
//                        ResourcePackSource.BUILTIN,
//                        Optional.of(new VersionedIdentifier(
//                                "realseasons",
//                                "seasonal_colormap",
//                                "1.0.0"
//                        ))
//                )
//        );

        System.out.println("hello there2");

        ResourcePackProfile profile2 = ResourcePackProfile.create(
                new ResourcePackInfo(
                        "real-seasons:seasonal_colormap",
                        Text.literal("Seasonal Colormap"),
                        ResourcePackSource.BUILTIN,
                        Optional.of(new VersionedIdentifier(
                                "real-seasons",
                                "seasonal_colormap",
                                "1.0.0"
                        ))
                ),
                new ResourcePackProfile.PackFactory() {

                    @Override
                    public ResourcePack open(ResourcePackInfo info) {
                        try {
                            return getOrCreatePack();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                        try {
                            return getOrCreatePack(); // ignore overlays for now
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    private ResourcePack getOrCreatePack() throws IOException {
//                        if (SeasonalPackHolder.PACK == null) {
//                            System.out.println("Creating seasonal pack...");
//                            SeasonalPackHolder.PACK = new SeasonalResourcePack(
//                                    SeasonColorManager.x123()
//                            );
//                        }
//                        return SeasonalPackHolder.PACK;

                        return new SeasonalResourcePack(
                                SeasonColorManager.x123()
                        );
                    }
                },
                ResourceType.CLIENT_RESOURCES,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
        );

        if (profile2 != null) {
            consumer.accept(
                    "real-seasons:seasonal_colormap",
                    (id) -> profile2
            );
        }
    }

//    @Inject(
//            method = "*",
//            at = @At("TAIL")
//    )
//    private void addSeasonalPack(Consumer<ResourcePackProfile> consumer,
//                                 ResourcePackProfile.PackFactory factory,
//                                 CallbackInfo ci) {
//
//        if (SeasonalPackHolder.PACK == null) return;
//
////        ResourcePackProfile profile = factory.open(
////                new ResourcePackInfo(
////                        "realseasons:seasonal_colormap",
////                        Text.literal("Seasonal Colormap"),
////                        ResourcePackSource.BUILTIN,
////                        Optional.of(new VersionedIdentifier(
////                                "realseasons",
////                                "seasonal_colormap",
////                                "1.0.0"
////                        ))
////                )
////        );
//
//        ResourcePackProfile profile2 = ResourcePackProfile.create(
//                new ResourcePackInfo(
//                        "realseasons:seasonal_colormap",
//                        Text.literal("Seasonal Colormap"),
//                        ResourcePackSource.BUILTIN,
//                        Optional.of(new VersionedIdentifier(
//                                "realseasons",
//                                "seasonal_colormap",
//                                "1.0.0"
//                        ))
//                ),
//                new ResourcePackProfile.PackFactory() {
//
//                    @Override
//                    public ResourcePack open(ResourcePackInfo info) {
//                        try {
//                            return getOrCreatePack();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
//                        try {
//                            return getOrCreatePack(); // ignore overlays for now
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    private ResourcePack getOrCreatePack() throws IOException {
//                        if (SeasonalPackHolder.PACK == null) {
//                            System.out.println("Creating seasonal pack...");
//                            SeasonalPackHolder.PACK = new SeasonalResourcePack(
//                                    SeasonColorManager.x123()
//                            );
//                        }
//                        return SeasonalPackHolder.PACK;
//                    }
//                },
//                ResourceType.CLIENT_RESOURCES,
//                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
//        );
//
//        if (profile2 != null) {
//            consumer.accept(profile2);
//        }
//    }
}
