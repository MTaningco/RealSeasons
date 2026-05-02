package com.realseasons;

import java.time.LocalDate;

public class SeasonUtil {

    public static float getRealYearProgress() {
        LocalDate now = LocalDate.now();
        int day = now.getDayOfYear();
        int total = now.lengthOfYear();
        float yearProgress = day / (float) total;

        return getSkewedYearProgress(yearProgress);
    }

    /**
     * Introduces a function to coincide the max season effects and speed during the right time of the year
     * @param yearProgress
     * @return
     */
    private static float getSkewedYearProgress(float yearProgress) {
        float yearProgress1 = yearProgress < (float) 0.27 ? yearProgress + (float) 1.0 : yearProgress;
        if (yearProgress1 > 0.27 && yearProgress1 <= 0.4) {
            return (float)(-7.2182 * Math.pow(yearProgress1, 3) + 2.1143 * Math.pow(yearProgress1, 2) + 2.6595 * yearProgress1 -0.73012);
        } else if (yearProgress1 > 0.4 && yearProgress1 <= 0.54) {
            return (float)(16.132 * Math.pow(yearProgress1, 3) - 25.906 * Math.pow(yearProgress1, 2) + 13.867 * yearProgress1 - 2.2245);
        } else if (yearProgress1 > 0.54 && yearProgress1 <= 0.78) {
            return (float)(9.8771 * Math.pow(yearProgress1, 3) - 15.773 * Math.pow(yearProgress1, 2) + 8.396 * yearProgress1 - 1.2397);
        } else if (yearProgress1 > 0.78 && yearProgress1 <= 0.83) {
            return (float)(-73.788 * Math.pow(yearProgress1, 3) + 180.0 * Math.pow(yearProgress1, 2) - 144.31 * yearProgress1 + 38.464);
        } else if (yearProgress1 > 0.83 && yearProgress1 <= 0.95) {
            return (float)(-3.5107 * Math.pow(yearProgress1, 3) + 5.0127 * Math.pow(yearProgress1, 2) + 0.93252 * yearProgress1 - 1.7199);
        } else if (yearProgress1 > 0.95 && yearProgress1 <= 1.08) {
            return (float)(13.97 * Math.pow(yearProgress1, 3) - 44.808 * Math.pow(yearProgress1, 2) + 48.262 * yearProgress1 - 16.708);
        } else if (yearProgress1 > 1.08 && yearProgress1 <= 1.2) {
            return (float)(28.962 * Math.pow(yearProgress1, 3) - 93.381 * Math.pow(yearProgress1, 2) + 100.72 * yearProgress1 - 35.593);
        } else {
            return (float)(-69.593 * Math.pow(yearProgress1, 3) + 261.42 * Math.pow(yearProgress1, 2) - 325.03 * yearProgress1 + 134.7);
        }
    }

    public static float getClampedInGameYearProgress(long worldTime, int subdivisionsPerSeason, int yearLength) {
        int totalSubdivisions = subdivisionsPerSeason * 4;
        float inGameYearProgress = (((float) worldTime / 24000L) % yearLength) / yearLength;
        int currentCalculatedSubdivision = (int) (inGameYearProgress * totalSubdivisions);
        return getSkewedYearProgress(((float) currentCalculatedSubdivision / totalSubdivisions) + (float) 0.27);
    }

    public static int getSeasonIndex(float t) {
        float scaled = t * 4;
        return (int) scaled % 4;
    }
}