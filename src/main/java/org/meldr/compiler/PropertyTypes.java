package org.meldr.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertyTypes {
    private static final Set<String> modelTypes = new HashSet<>();
    private static  final Map<String, String> colorTypes = new HashMap<>();
    static {
        // Model Types
        modelTypes.add("SPHERE");
        modelTypes.add("BALL");
        modelTypes.add("CUBE");
        modelTypes.add("BOUNCE_PAD");
        modelTypes.add("ROCKET");
        modelTypes.add("GOLF_BALL");
        modelTypes.add("MONKEY");
        modelTypes.add("CYLINDER");

        // Default Colors
        colorTypes.put("BLACK", "(0.0, 0.0, 0.0, 1.0)");
        colorTypes.put("WHITE", "(1.0, 1.0, 1.0, 1.0)");
        colorTypes.put("RED", "(1.0, 0.0, 0.0, 1.0)");
        colorTypes.put("GREEN", "(0.0, 1.0, 0.0, 1.0)");
        colorTypes.put("BLUE", "(0.0, 0.0, 1.0, 1.0)");
        colorTypes.put("YELLOW", "(1.0, 1.0, 0.0, 1.0)");
        colorTypes.put("CYAN", "(0.0, 1.0, 1.0, 1.0)");
        colorTypes.put("MAGENTA", "(1.0, 0.0, 1.0, 1.0)");
        colorTypes.put("ORANGE", "(1.0, 0.5, 0.0, 1.0)");
        colorTypes.put("PURPLE", "(0.5, 0.0, 0.5, 1.0)");
        colorTypes.put("PINK", "(1.0, 0.75, 0.8, 1.0)");
        colorTypes.put("BROWN", "(0.6, 0.3, 0.0, 1.0)");
        colorTypes.put("GRAY", "(0.5, 0.5, 0.5, 1.0)");
        colorTypes.put("LIGHTGRAY", "(0.75, 0.75, 0.75, 1.0)");
        colorTypes.put("DARKGRAY", "(0.25, 0.25, 0.25, 1.0)");

        // Blues
        colorTypes.put("NAVY", "(0.0, 0.0, 0.5, 1.0)");
        colorTypes.put("ROYALBLUE", "(0.25, 0.41, 0.88, 1.0)");
        colorTypes.put("SKYBLUE", "(0.53, 0.81, 0.92, 1.0)");
        colorTypes.put("STEELBLUE", "(0.27, 0.51, 0.71, 1.0)");

        // Greens
        colorTypes.put("LIME", "(0.0, 1.0, 0.0, 1.0)");
        colorTypes.put("FORESTGREEN", "(0.13, 0.55, 0.13, 1.0)");
        colorTypes.put("SEAGREEN", "(0.18, 0.55, 0.34, 1.0)");
        colorTypes.put("OLIVE", "(0.5, 0.5, 0.0, 1.0)");

        // Reds / warm tones
        colorTypes.put("MAROON", "(0.5, 0.0, 0.0, 1.0)");
        colorTypes.put("CRIMSON", "(0.86, 0.08, 0.24, 1.0)");
        colorTypes.put("SALMON", "(0.98, 0.5, 0.45, 1.0)");
        colorTypes.put("CORAL", "(1.0, 0.5, 0.31, 1.0)");

        // Purples / violets
        colorTypes.put("VIOLET", "(0.93, 0.51, 0.93, 1.0)");
        colorTypes.put("INDIGO", "(0.29, 0.0, 0.51, 1.0)");
        colorTypes.put("PLUM", "(0.87, 0.63, 0.87, 1.0)");

        // Yellows / gold
        colorTypes.put("GOLD", "(1.0, 0.84, 0.0, 1.0)");
        colorTypes.put("KHAKI", "(0.94, 0.9, 0.55, 1.0)");
        colorTypes.put("TAN", "(0.82, 0.71, 0.55, 1.0)");
    }

    public static boolean modelTypeExists(String type) {
        return modelTypes.contains(type.toUpperCase());
    }

    public static boolean colorTypeExists(String type) {
        return colorTypes.containsKey(type.toUpperCase());
    }

    public static String getColorFromType(String type) {
        return colorTypes.get(type.toUpperCase());
    }
}
