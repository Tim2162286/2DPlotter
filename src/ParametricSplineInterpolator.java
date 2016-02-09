import java.util.ArrayList;

/**
 * Creates parametric splines from edge detection output
 * @author Jonathan Bush
 * @since 2/9/2016
 */
public class ParametricSplineInterpolator implements SplineGenerator {

    public static void main(String[] args){System.out.println("Hello World!");}

	/**
     * Receives the boolean 2D array containing information about the
     * locations of edges in the image.
     * @param edges boolean[][] true if the pixel is on an edge, false if not
     */
    public void setEdgeMatrix(boolean[][] edges){}

    /**
     * Gets an ArrayList containing the coefficients for the parametric
     * splines that compose the image.
     * @param resolution the number of pixels to skip when generating the splines
     * @return ArrayList containing one parametric spline for each edge
     */
    public ArrayList<ArrayList<Double[]>> getCoefficients(int resolution){
    	return new ArrayList<ArrayList<Double[]>>();
    }
}
