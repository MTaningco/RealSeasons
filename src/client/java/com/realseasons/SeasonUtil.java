package com.realseasons;

import java.time.LocalDate;

public class SeasonUtil {

//    public static Season getCurrentSeason() {
//
//        int month = LocalDate.now().getMonthValue();
//
//        if (month >= 3 && month <= 5) return Season.WINTER;
//        if (month >= 6 && month <= 8) return Season.SUMMER;
//        if (month >= 9 && month <= 11) return Season.FALL;
//
//        return Season.SPRING;
//
//    }

    public static float getYearProgress() {

        //full spring ~ may 5
        // full summer ~ jul 7
        // full fall ~ oct 10 => 10/12 5/6 -> 0.5
        // full winter ~ late jan 1

        //0.5 is fall -> oct
        LocalDate now = LocalDate.now();
        int day = now.getDayOfYear();
        int total = now.lengthOfYear();
        //return (day / (float) total) + 0.67f; // offset to match real life better
        return 0.5f;
    }

    public static int getSeasonIndex(float t) {
        float scaled = t * 4;
        return (int) scaled % 4;
    }

}