package com.realseasons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ColorPoint {
    public int x;
    public int y;
    public int color;
}

class ColorMapConfig {
    public List<ColorPoint> values;
}

class ColorMapConfig2 {
    public Map<Integer, String> defaultGrassColorToBiomeGroupMap;
    public Map<String, int[]> biomeIdToSeasonArrayMap;

//    @Override
//    public String toString() {
//        return "ColorMapConfig2{" +
//                "defaultGrassColorToBiomeGroupMap=" + defaultGrassColorToBiomeGroupMap +
//                ", biomeIdToSeasonArrayMap=" + biomeIdToSeasonArrayMap.; +
//                '}';
//    }
}

class ColorMapConfig3{

    public Map<Integer, String> defaultGrassColorToBiomeGroupMap;
    public Map<String, HashMap<String, int[]>> blockTypeToBiomeSeasonMap;
}

