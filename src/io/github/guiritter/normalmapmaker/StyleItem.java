package io.github.guiritter.normalmapmaker;

import io.github.guiritter.normalmapmaker.style.Style;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Styles combo box item that links
 * {@link io.github.guiritter.normalmapmaker.style.Style} to its
 * {@link java.lang.String} representation.
 * @author Guilherme Alan Ritter
 */
public class StyleItem {

    private static final StyleItem array[];

    private final Style value;

    public final Style getValue() {
        return value;
    }

    public static StyleItem[] getArray() {
        return Arrays.copyOf(array, array.length);
    }

    public static StyleItem getByValue(Style value) {
        for (StyleItem item : array) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public StyleItem(Style value) {
        this.value = value;
    }

    static {
        List<StyleItem> styleItemList = new ArrayList<>();
        styleItemList.add(new StyleItem(Style.list.get(0)));
        styleItemList.add(new StyleItem(Style.list.get(1)));
        array = styleItemList.toArray(new StyleItem[]{});
    }
}
