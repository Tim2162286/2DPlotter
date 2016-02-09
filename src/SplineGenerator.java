import java.util.ArrayList;

/**
 * A SplineGenerator will accept information about the location of edges
 * in an image and generate a series of parametric splines (smooth lines
 * that pass through points on the edges) that can be subsequently used
 * to draw an approximation of the edges, either through software
 * simulation or with a physical pen plotter.
 */
public interface SplineGenerator {

    /**
     * Receives the boolean 2D array containing information about the
     * locations of edges in the image.
     * @param edges boolean[][] true if the pixel is on an edge, false if not
     */
    public void setEdgeMatrix(boolean[][] edges);

    /**
     * Gets an ArrayList containing the coefficients for the parametric
     * splines that compose the image.
     * @param resolution the number of pixels to skip when generating the splines
     * @return ArrayList containing one parametric spline for each edge
     */
    public ArrayList<ArrayList<Double[]>> getCoefficients(int resolution);

}