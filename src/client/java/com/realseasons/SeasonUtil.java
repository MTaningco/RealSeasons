package com.realseasons;

import java.time.LocalDate;

public class SeasonUtil {

    public static float getYearProgress() {
        LocalDate now = LocalDate.now();
        int day = now.getDayOfYear();
        int total = now.lengthOfYear();
        /*
        * Offset of 0.67 is added to emulate better alignment of seasons to real life
        * 0.0f => Spring
        * 0.25f => Summer
        * 0.5f => Fall
        * 0.75f => Winter
        * */
        return (day / (float) total) + 0.67f;
    }

    public static int getSeasonIndex(float t) {
        float scaled = t * 4;
        return (int) scaled % 4;
    }
}