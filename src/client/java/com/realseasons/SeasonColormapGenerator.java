package com.realseasons;

import java.awt.image.BufferedImage;

import static com.realseasons.SeasonUtil.getYearProgress;

public class SeasonColormapGenerator {

    public static BufferedImage generateGrassMap() {

        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        float season = getYearProgress();

        for (int x = 0; x < 256; x++) {

            for (int y = 0; y < 256; y++) {

                float temperature = x / 255f;
                float humidity = y / 255f;

                int color = computeColor(temperature, humidity, season);

                img.setRGB(x, y, color);
            }
        }

        return img;
    }

    private static int computeColor(float temp, float humidity, float season) {

        int spring = 0x66BB44;
        int summer = 0x3FAF2A;
        int fall = 0xD67D1F;
        int winter = 0xCFE8FF;

        int a;
        int b;
        float blend;

        if (season < 0.25f) {
            a = winter;
            b = spring;
            blend = season / 0.25f;

        } else if (season < 0.5f) {
            a = spring;
            b = summer;
            blend = (season - 0.25f) / 0.25f;

        } else if (season < 0.75f) {
            a = summer;
            b = fall;
            blend = (season - 0.5f) / 0.25f;

        } else {
            a = fall;
            b = winter;
            blend = (season - 0.75f) / 0.25f;
        }

        int base = lerpColor(a, b, blend);

        float biomeFactor = (temp * 0.7f + humidity * 0.3f);

        return adjustBrightness(base, biomeFactor);
    }

    private static int adjustBrightness(int color, float factor) {

        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;

        r = (int)(r * factor);
        g = (int)(g * factor);
        b = (int)(b * factor);

        return (r << 16) | (g << 8) | b;
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