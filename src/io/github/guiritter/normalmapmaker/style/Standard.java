package io.github.guiritter.normalmapmaker.style;

/**
 * Same style as used
 * {@link <a href="https://en.wikipedia.org/wiki/Normal_mapping">here</a>}:
 * x is represented by the red component,
 * y by the green component and z by the blue component.
 * @author Guilherme Alan Ritter
 */
public class Standard extends Style {

    public Standard() {
        name = "Standard";
        backgroundColor = new int[]{128, 128, 255, 0};
    }

    @Override
    public void getStyleColor(double normalX, double normalY, double normalZ, int[] color) {
        color[0] = (int) Math.round((normalX + 1) * 127.5);
        color[1] = (int) Math.round((normalY + 1) * 127.5);
        color[2] = (int) Math.round((normalZ + 1) * 127.5);
        color[3] = 255;
    }

    @Override
    public String toString() {
        return name;
    }
}
