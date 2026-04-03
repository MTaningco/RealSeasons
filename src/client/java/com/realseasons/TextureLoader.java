package com.realseasons;

import net.minecraft.client.texture.NativeImage;

import java.awt.image.BufferedImage;

public class TextureLoader {

    public static NativeImage convert(BufferedImage img) {

        NativeImage nativeImg = new NativeImage(256, 256, false);

        for (int x = 0; x < 256; x++) {

            for (int y = 0; y < 256; y++) {

                int color = img.getRGB(x, y);

                nativeImg.setColor(x, y, color);
            }
        }

        return nativeImg;
    }

}
