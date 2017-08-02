package io.github.guiritter.normalmapmaker;

import javafx.geometry.Point3D;

/**
 * Polygon in 3D space with utilities for projection in a 2D plane.
 * @author Guilherme Alan Ritter
 */
public final class Polygon {

    public final int boundingBox[] = new int[4];

    private final double d;

    private static double d0;

    private static double d1;

    private static double d2;

    private static double d01;

    private static double d02;

    private static double d12;

    private final double denominator;

    private double λ0 = 0;

    private double λ1 = 0;

    private double λ2 = 0;

    public static final int maximumX = 0;

    public static final int maximumY = 1;

    public static final int minimumX = 2;

    public static final int minimumY = 3;

    /**
     * Also coefficients a, b and c.
     */
    public final Point3D normal;

    public final Point3D normalUnit;

    private static final String toStringFormat;

    public final Point3D vertices[] = new Point3D[3];

    private final double x0mx2;

    private final double x2mx1;

    private final double y0my2;

    private final double y1my2;

    private final double y2my0;

    public boolean hasOverlappingVertices() {
        return ((vertices[0].distance(vertices[1]) == 0d)
         || (vertices[0].distance(vertices[2]) == 0d)
         || (vertices[1].distance(vertices[2]) == 0d));
    }

    /**
     * Returns whether the vertices of this polygon
     * form a straight line in space.
     * @return
     */
    public boolean isLine() {
        d01 = vertices[0].distance(vertices[1]);
        d02 = vertices[0].distance(vertices[2]);
        d12 = vertices[1].distance(vertices[2]);
        d0 = d01 + d02;
        d1 = d01 + d12;
        d2 = d02 + d12;
        // check who's in the middle
        if ((d0 < d1) && (d0 < d2)) {
            return d01 + d02 == d12;
        } else if ((d1 < d0) && (d1 < d2)) {
            return d01 + d12 == d02;
        } else {
            return d02 + d12 == d01;
        }
    }

    /**
     * Whether the given point's X and Y coordinates are located within
     * the triangle formed by the polygon's projection in the XY plane.
     * @param point
     * @return
     */
    public boolean isPointInsideXY(Point3D point) {
        λ0 = ((
         y1my2 * (point.getX() - vertices[2].getX())) + (
         x2mx1 * (point.getY() - vertices[2].getY()))) / denominator;
        λ1 = ((
         y2my0 * (point.getX() - vertices[2].getX())) + (
         x0mx2 * (point.getY() - vertices[2].getY()))) / denominator;
        λ2 = 1 - λ0 - λ1;
        return (λ0 >= 0) && (λ1 >= 0) && (λ2 >= 0);
    }

    /**
     * Whether the projection of the polygon in the XY plane
     * forms a straight line, i.e. if the polygon's normal's Z is zero.
     * @return
     */
    public boolean isUpright() {
        return normalUnit.getZ() == 0d;
    }

    /**
     *
     * @param point
     * @return the <code>point</code> translated in Z to intersect the polygon
     */
    public Point3D setZ (Point3D point) {
        return new Point3D(point.getX(), point.getY(),
         -(((normal.getX() * point.getX())
          + (normal.getY() * point.getY()) + d) / normal.getZ()));
    }

    @Override
    public String toString() {
        return String.format(toStringFormat,
         vertices[0].getX(), vertices[0].getY(), vertices[0].getZ(),
         vertices[1].getX(), vertices[1].getY(), vertices[1].getZ(),
         vertices[2].getX(), vertices[2].getY(), vertices[2].getZ(),
         normal.getX(), normal.getY(), normal.getZ(),
         normalUnit.getX(), normalUnit.getY(), normalUnit.getZ(),
         boundingBox[minimumX], boundingBox[maximumX],
         boundingBox[minimumY], boundingBox[maximumY], d);
    }

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("vertex 0:\t%f\t\t%f\t\t%f\n");
        builder.append("vertex 1:\t%f\t\t%f\t\t%f\n");
        builder.append("vertex 2:\t%f\t\t%f\t\t%f\n");
        builder.append("normal:\t%f\t\t%f\t\t%f\n");
        builder.append("normalUnit:\t%f\t\t%f\t\t%f\n");
        builder.append("bounding box x:\t%d\t%d\n");
        builder.append("bounding box y:\t%d\t%d\n");
        builder.append("d:\t%f");
        toStringFormat = builder.toString();
    }

    public Polygon(double normalUnit[], double polygon[][]) {
        int vertexI;
        boundingBox[maximumX] = Integer.MIN_VALUE;
        boundingBox[maximumY] = Integer.MIN_VALUE;
        boundingBox[minimumX] = Integer.MAX_VALUE;
        boundingBox[minimumY] = Integer.MAX_VALUE;
        for (vertexI = 0; vertexI < 3; vertexI++) {
            vertices[vertexI] = new Point3D(
             polygon[vertexI][0], polygon[vertexI][1], polygon[vertexI][2]);
            boundingBox[maximumX] = Math.max(boundingBox[maximumX],
             (int) Math.round(Math.floor(vertices[vertexI].getX())));
            boundingBox[maximumY] = Math.max(boundingBox[maximumY],
             (int) Math.round(Math.floor(vertices[vertexI].getY())));
            boundingBox[minimumX] = Math.min(boundingBox[minimumX],
             (int) Math.round(Math.floor(vertices[vertexI].getX())));
            boundingBox[minimumY] = Math.min(boundingBox[minimumY],
             (int) Math.round(Math.floor(vertices[vertexI].getY())));
        }
        normal = vertices[0].subtract(vertices[1])
         .crossProduct(vertices[2].subtract(vertices[1]));
        d = -(
         (normal.getX() * vertices[1].getX()) +
         (normal.getY() * vertices[1].getY()) +
         (normal.getZ() * vertices[1].getZ()));
        this.normalUnit = new Point3D(
         normalUnit[0], normalUnit[1], normalUnit[2]);
        x0mx2 = vertices[0].getX() - vertices[2].getX();
        x2mx1 = vertices[2].getX() - vertices[1].getX();
        y0my2 = vertices[0].getY() - vertices[2].getY();
        y1my2 = vertices[1].getY() - vertices[2].getY();
        y2my0 = vertices[2].getY() - vertices[0].getY();
        denominator = (y1my2 * x0mx2) + (x2mx1 * y0my2);
    }
}
