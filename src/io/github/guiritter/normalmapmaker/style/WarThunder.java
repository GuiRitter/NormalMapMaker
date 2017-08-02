package io.github.guiritter.normalmapmaker.style;

/**
 * Same style as used in
 * {@link <a href="www.warthunder.com/‎">Gaijin's War Thunder</a>}'s normal maps:
 * x is represented by the alpha component,
 * y is represented by the green component and z is not represented.
 * @author Guilherme Alan Ritter
 */
public class WarThunder extends Style {

    public WarThunder() {
        name = "War Thunder";
        backgroundColor = new int[]{0, 128, 0, 128};
    }

    @Override
    public void getStyleColor(double normalX, double normalY, double normalZ, int[] color) {
        color[0] = 0;
        color[1] = (int) Math.round((1 - normalY) * 127.5);
        color[2] = 0;
        color[3] = (int) Math.round((normalX + 1) * 127.5);
    }

    @Override
    public String toString() {
        return name;
    }
}
