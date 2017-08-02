package io.github.guiritter.normalmapmaker;

import io.github.guiritter.normalmapmaker.GUI.Components;
import static io.github.guiritter.normalmapmaker.GUI.Components.INPUT_TEXT_FIELD;
import static io.github.guiritter.normalmapmaker.GUI.Components.OUTPUT_TEXT_FIELD;
import java.io.File;
import java.io.IOException;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import org.j3d.loaders.InvalidFormatException;
import static io.github.guiritter.normalmapmaker.Algorithm.IGNORED_INVALID;
import static io.github.guiritter.normalmapmaker.Algorithm.IGNORED_UPRIGHT;
import static io.github.guiritter.normalmapmaker.Algorithm.ERROR_NO_POLYGONS;
import static io.github.guiritter.normalmapmaker.Algorithm.ERROR_NO_VALID;
import static io.github.guiritter.normalmapmaker.GUI.ERROR_DIALOG_TITLE;
import static io.github.guiritter.normalmapmaker.GUI.WARNING_DIALOG_TITLE;
import static io.github.guiritter.normalmapmaker.Algorithm.ERROR_NO_SURFACES;

/**
 * Topmost class in the hierarchy.
 * @author Guilherme Alan Ritter
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class Main {

    private static final Algorithm algorithm;

    private static final String EXCEPTION_NO_FACETS
     = "File has no valid surfaces.";

    private static final String EXCEPTION_NO_POLYGONS
     = "File has no polygons.";

    private static final String EXCEPTION_NO_VALID
     = "File has no valid polygons.";

    private static final String FILE_EXTENSION = ".png";

    private static final GUI<StyleItem> gui;

    private static final Wrapper<File> inputFile = new Wrapper<>();

    private static final Wrapper<File> outputFile = new Wrapper<>();

    /**
     * Attempts to return a file's canonical path.
     * If not possible, returns a file's absolute path.
     * @param file
     * @return
     * @see java.io.File#getCanonicalPath()
     * @see java.io.File#getAbsolutePath()
     */
    public static final String getFilePath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            return file.getAbsolutePath();
        }
    }

    private static void treatFile(
     Wrapper<File> whichFile, Components textField) {
        File file = gui.showOpenSaveDialog(textField == INPUT_TEXT_FIELD);
        if (file == null) {
            return;
        }
        String path = getFilePath(file);
        if ((textField == OUTPUT_TEXT_FIELD)
         && (!path.endsWith(FILE_EXTENSION))) {
            path += FILE_EXTENSION;
            file = new File(path);
        }
        whichFile.o = file;
        gui.setFieldText(textField, path);
    }

    static {
        algorithm = new Algorithm() {

            @Override
            public void setProgressValue(int index, int value) {
                gui.setProgressBarValue(index, value);
            }

            @Override
            public void setProgressMaximum(int index, int maximumValue) {
                gui.setProgressBarMaximumValue(index, maximumValue);
            }
        };
        gui = new GUI<StyleItem>(StyleItem.getArray()) {

            @Override
            public void onInputButtonPressed() {
                treatFile(inputFile, INPUT_TEXT_FIELD);
            }

            @Override
            public void onOutputButtonPressed() {
                treatFile(outputFile, OUTPUT_TEXT_FIELD);
            }

            @Override
            public void onMakeButtonPressed() {
                if ((inputFile.o == null) || (outputFile.o == null)) {
                    gui.showMessageDialog("File(s) not set.",
                     ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                    return;
                }
                (new Thread(() -> {
                    try {
                        long[] ignoredPolygons = algorithm.make(inputFile.o,
                         outputFile.o, gui.getWidth(), gui.getHeight(),
                         gui.getStyle().getValue());
                        if (ignoredPolygons[IGNORED_INVALID]
                         == ERROR_NO_SURFACES) {
                            gui.showMessageDialog(EXCEPTION_NO_FACETS,
                             ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                        } else if (ignoredPolygons[IGNORED_INVALID]
                         == ERROR_NO_POLYGONS) {
                            gui.showMessageDialog(EXCEPTION_NO_POLYGONS,
                             ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                        } else if (ignoredPolygons[IGNORED_INVALID]
                         == ERROR_NO_VALID) {
                            gui.showMessageDialog(EXCEPTION_NO_VALID,
                             ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                        } else if ((ignoredPolygons[IGNORED_INVALID] > 0)
                         || ignoredPolygons[IGNORED_UPRIGHT] > 0) {
                            gui.showMessageDialog(
                             ignoredPolygons[IGNORED_INVALID]
                              + " invalid polygon(s) ignored,\n"
                              + ignoredPolygons[IGNORED_UPRIGHT]
                              + " upright polygon(s) ignored.",
                             WARNING_DIALOG_TITLE, WARNING_MESSAGE);
                        }
                    } catch (InvalidFormatException ex) {
                        ex.printStackTrace();
                        gui.showMessageDialog(
                         "STL file not properly formatted.",
                         ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        gui.showMessageDialog("File system error.",
                         ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                    } catch (OutOfMemoryError err) {
                        err.printStackTrace();
                        gui.showMessageDialog("File is too big.",
                         ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        gui.showMessageDialog("Unknown error.",
                         ERROR_DIALOG_TITLE, ERROR_MESSAGE);
                    } finally {
                        gui.setProgressBarReset();
                    }
                })).start();
            }
        };
    }

    /**
     * Shows the graphics user interface and makes it work.
     * @param args
     */
    public static final void main(String args[]) {}
}
