/**
 * Created by Tim on 2/8/2016.
 */
public class ParametricSplineInterpolator implements SplineGenerator {
	
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
     * @returns ArrayList containing one parametric spline for each edge
     */
    public ArrayList<ArrayList<Double[]>> getCoefficients(int resolution){
    	return new ArrayList<ArrayList<Double[]>>();
    }
}
