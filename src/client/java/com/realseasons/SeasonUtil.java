package com.realseasons;

import java.time.LocalDate;

public class SeasonUtil {

    public static float getRealYearProgress() {
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

    public static float getClampedInGameYearProgress(long worldTime, int subdivisionsPerSeason, int yearLength) {
        int totalSubdivisions = subdivisionsPerSeason * 4;
        float inGameYearProgress = (((float) worldTime / 24000L) % yearLength) / yearLength;
        int currentCalculatedSubdivision = (int) (inGameYearProgress * totalSubdivisions);
        return (float) currentCalculatedSubdivision / totalSubdivisions;
    }

    public static int getSeasonIndex(float t) {
        float scaled = t * 4;
        return (int) scaled % 4;
    }
}