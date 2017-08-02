package io.github.guiritter.normalmapmaker;

import static io.github.guiritter.normalmapmaker.Polygon.maximumX;
import static io.github.guiritter.normalmapmaker.Polygon.maximumY;
import static io.github.guiritter.normalmapmaker.Polygon.minimumX;
import static io.github.guiritter.normalmapmaker.Polygon.minimumY;
import io.github.guiritter.normalmapmaker.style.Standard;
import io.github.guiritter.normalmapmaker.style.Style;
import io.github.guiritter.normalmapmaker.style.WarThunder;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.geometry.Point3D;
import javax.imageio.ImageIO;
import org.j3d.loaders.InvalidFormatException;
import org.j3d.loaders.stl.STLFileReader;

/**
 * Creates a normal map image out of an STL file.
 * Uses libraries from {@link <a href="https://www.j3d.org/">www.j3d.org</a>}.
 * @author Guilherme Alan Ritter
 */
public abstract class Algorithm {

    /**
     * Error message ID for when there are no surfaces in the STL file.
     */
    public static final long ERROR_NO_SURFACES = -1;

    /**
     * Error message ID for when there are no polygons in the STL file.
     */
    public static final long ERROR_NO_POLYGONS = -2;

    /**
     * Error message ID for when there are no valid polygons in the STL file.
     */
    public static final long ERROR_NO_VALID = -3;

    /**
     * Return array index for the amount of polygons
     * with at least two vertices in the same position.
     */
    public static final int IGNORED_INVALID = 0;

    /**
     * Return array index for the amount of polygons that form a straight line
     * in the XY projection.
     */
    public static final int IGNORED_UPRIGHT = 1;

    public static final int PROGRESS_BAR_AMOUNT = 4;

    /**
     * Executes the processing. The image will be scaled accordingly,
     * while keeping the aspect ratio. So, unless both output dimensions
     * are the same, only one of them will be as intended.
     * Only considers the first surface in the STL file.
     * Ignores polygons where at least 2 vertices occupy the same position
     * in space, those where the vertices form a straight line in space,
     * and those where their projection in the XY plane form a straight line.
     * The behavior for polygons with more than 3 vertices is undefined.
     * @param inputFile path to the input STL
     * @param outputFile path to the output PNG(s)
     * @param outputWidth intended output width
     * @param outputHeight intended output height
     * @param style {@link nmm.Algorithm.Style}
     * @return the count of polygons ignored
     * @throws InvalidFormatException thrown by
     * {@link org.j3d.loaders.stl.STLFileReader}.
     * @throws IOException also thrown by
     * {@link org.j3d.loaders.stl.STLFileReader}
     * and by {@link javax.imageio.ImageIO}
     */
    public long[] make(File inputFile, File outputFile,
     int outputWidth, int outputHeight, Style style)
     throws InvalidFormatException, IOException {
        STLFileReader reader = new STLFileReader(inputFile);
        long returnArray[] = new long[2];
        Arrays.fill(returnArray, 0);
        if (reader.getNumOfFacets().length < 1) {
            returnArray[IGNORED_INVALID] = ERROR_NO_SURFACES;
            return returnArray;
        }
        int polygonCount = reader.getNumOfFacets()[0];
        if (polygonCount < 1) {
            returnArray[IGNORED_INVALID] = ERROR_NO_POLYGONS;
            return returnArray;
        }
        // progress bar initialization
        {
            int i = 0;
            setProgressMaximum(i++, polygonCount - 1);
            setProgressMaximum(i++, polygonCount - 1);
            setProgressMaximum(i++, 1);
            setProgressMaximum(i  , polygonCount - 1);
            for (i = 0; i < PROGRESS_BAR_AMOUNT; i++) {
                setProgressValue(i, 0);
            }
        }
        System.out.println("facet array length: "
         + reader.getNumOfFacets().length + "\n");
        System.out.println(polygonCount + " facets\n");
        Polygon polygons[] = new Polygon[polygonCount];
        // reads the polygons and saves them as matrices of doubles
        // for later processing
        {
            double polygon[][] = new double[3][3];
            double normal[] = new double[3];
            int polygonI;
            int vertexI;
            double limits[] = new double[4];
            limits[maximumX] = Double.NEGATIVE_INFINITY;
            limits[maximumY] = Double.NEGATIVE_INFINITY;
            limits[minimumX] = Double.POSITIVE_INFINITY;
            limits[minimumY] = Double.POSITIVE_INFINITY;
            double polygonsTemporary[][][] = new double[polygonCount][3][3];
            double normalsTemporary[][] = new double[polygonCount][3];
            Polygon polygonTemporary;
            boolean valid = false;
            for (polygonI = 0; polygonI < polygonCount; polygonI++) {
                reader.getNextFacet(normal, polygon);
                polygonTemporary = new Polygon(normal, polygon);
                if (polygonTemporary.hasOverlappingVertices()
                 || polygonTemporary.isLine()) {
                    normalsTemporary[polygonI] = null;
                    polygonsTemporary[polygonI] = null;
                    returnArray[IGNORED_INVALID]++;
                } else {
                    System.arraycopy(normal, 0,
                     normalsTemporary[polygonI], 0, 3);
                    for (vertexI = 0; vertexI < 3; vertexI++) {
                        limits[maximumX] = Math.max(limits[maximumX],
                         polygon[vertexI][0]);
                        limits[maximumY] = Math.max(limits[maximumY],
                         polygon[vertexI][1]);
                        limits[minimumX] = Math.min(limits[minimumX],
                         polygon[vertexI][0]);
                        limits[minimumY] = Math.min(limits[minimumY],
                         polygon[vertexI][1]);
                        System.arraycopy(polygon[vertexI], 0,
                         polygonsTemporary[polygonI][vertexI], 0, 3);
                    }
                    valid = true;
                }
                setProgressValue(0, polygonI);
            }
            if (!valid) {
                for (int i = 0; i < PROGRESS_BAR_AMOUNT; i++) {
                    setProgressMaximum(i, 1);
                    setProgressValue(i, 1);
                }
                returnArray[IGNORED_INVALID] = ERROR_NO_VALID;
                return returnArray;
            }
            System.out.println("STL minimum X: " + limits[minimumX]);
            System.out.println("STL minimum Y: " + limits[minimumY]);
            System.out.println("STL maximum X: " + limits[maximumX]);
            System.out.println("STL maximum Y: " + limits[maximumY] + "\n");
            // scale calculation. keeps the aspect ratio
            double scale;
            {
                double lastColumn = limits[maximumX] - limits[minimumX];
                double lastLine   = limits[maximumY] - limits[minimumY];
                double scaleX = (((double) outputWidth ) - 1.0) / lastColumn;
                double scaleY = (((double) outputHeight) - 1.0) / lastLine  ;
                if (scaleX > scaleY) {
                    if (!(Math.round(scaleX * lastLine) > (outputHeight - 1))) {
                        scale = scaleX;
                        outputHeight = (int)
                         (Math.round(Math.floor(lastLine * scale))) + 1;
                    } else {
                        scale = scaleY;
                        outputWidth = (int)
                         (Math.round(Math.floor(lastColumn * scale))) + 1;
                    }
                } else {
                    if (!(Math.round(scaleY * lastColumn) > (outputWidth - 1))) {
                        scale = scaleY;
                        outputWidth = (int)
                         (Math.round(Math.floor(lastColumn * scale))) + 1;
                    } else {
                        scale = scaleX;
                        outputHeight = (int)
                         (Math.round(Math.floor(lastLine * scale))) + 1;
                    }
                }
            }
            // creates the polygon objects while scaling and translating them,
            // so their coordinates match the available pixel indexes
            System.out.println("scale: " + scale + "\n");
            for (polygonI = 0; polygonI < polygonCount; polygonI++) {
                if (polygonsTemporary[polygonI] == null) {
                    polygons[polygonI] = null;
                } else {
                    for (vertexI = 0; vertexI < 3; vertexI++) {
                        polygonsTemporary[polygonI][vertexI][0]
                         = (polygonsTemporary[polygonI][vertexI][0]
                         - limits[minimumX]) * scale;
                        polygonsTemporary[polygonI][vertexI][1]
                         = (polygonsTemporary[polygonI][vertexI][1]
                         - limits[minimumY]) * scale;
                        polygonsTemporary[polygonI][vertexI][2]
                         =  polygonsTemporary[polygonI][vertexI][2]
                         * scale;
                    }
                    polygons[polygonI] = new Polygon(
                     normalsTemporary[polygonI],
                     polygonsTemporary[polygonI]);
                }
                setProgressValue(1, polygonI);
            }
        }
        setProgressMaximum(2, outputHeight - 1);
        BufferedImage image = new BufferedImage(
         outputWidth, outputHeight, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        double zBuffer[][] = new double[outputWidth][outputHeight];
        int x;
        int y;
        int color[] = new int[]{0, 0, 0, 0};
        Point3D point;
        // initializes the output image with the background color
        int backgroundColor[] = style.backgroundColor;
        for (y = 0; y < outputHeight; y++) {
            for (x = 0; x < outputWidth; x++) {
                raster.setPixel(x, y, backgroundColor);
                zBuffer[x][y] = Double.NEGATIVE_INFINITY;
            }
            setProgressValue(2, y);
        }
        // iterate through the polygons
        for (int i = 0; i < polygonCount; i++) {
            if ((polygons[i] == null)) {
                continue;
            } else if (polygons[i].isUpright()) {
                returnArray[IGNORED_UPRIGHT]++;
                continue;
            }
            // iterate through the pixels
            // that will possibly be painted by this polygon
            for (y = polygons[i].boundingBox[minimumY];
             y <= polygons[i].boundingBox[maximumY]; y++) {
                for (x = polygons[i].boundingBox[minimumX];
                 x <= polygons[i].boundingBox[maximumX]; x++) {
                    point = new Point3D(x, y, 0);
                    if (!polygons[i].isPointInsideXY(point)) {
                        continue;
                    }
                    // only paints the pixel
                    // if it appears above the last painted one
                    point = polygons[i].setZ(point);
                    if (zBuffer[x][y] < point.getZ()) {
                        zBuffer[x][y] = point.getZ();
                    } else {
                        continue;
                    }
                    style.getStyleColor(
                     polygons[i].normalUnit.getX(),
                     polygons[i].normalUnit.getY(),
                     polygons[i].normalUnit.getZ(), color);
                    raster.setPixel(x, outputHeight - y - 1, color);
                }
            }
            setProgressValue(3, i);
        }
        ImageIO.write(image, "png", outputFile);
        return returnArray;
    }

    /**
     * Sets the maximum value for each progress bar.
     * @param index
     * @param maximumValue
     */
    public abstract void setProgressMaximum(int index, int maximumValue);

    /**
     * When the progress in one of the operations changes.
     * @param index
     * @param value
     */
    public abstract void setProgressValue(int index, int value);

    static {
        Style.list.add(new Standard());
        Style.list.add(new WarThunder());
    }
}
