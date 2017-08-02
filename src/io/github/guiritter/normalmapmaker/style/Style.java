package io.github.guiritter.normalmapmaker.style;

import java.util.ArrayList;
import java.util.List;

/**
 * How to translate each normal's components into color.
 * @author Guilherme Alan Ritter
 */
public abstract class Style {

    public int backgroundColor[];

    /**
     * List of available styles.
     */
    public static final List<Style> list = new ArrayList<>();

    public String name;

    public Style getInstance() {
        return null;
    }

    public int[] getStyleColor(double normalX,
            double normalY, double normalZ) {
        int color[] = new int[4];
        getStyleColor(normalX, normalY, normalZ, color);
        return color;
    }

    public abstract void getStyleColor(double normalX,
            double normalY, double normalZ, int color[]);
}
