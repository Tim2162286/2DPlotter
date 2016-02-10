import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates parametric splines from edge detection output
 * @author Jonathan Bush
 * @since 2/9/2016
 */
public class ParametricSplineInterpolator implements SplineGenerator {

    private final int SENSITIVITY = 2;   //Number of pixels to search out for next point
    private final int MIN_LINE_PX = 4;   //Disregard lines with fewer than this number of pixels

    private boolean[][] edgeMatrix;

    public static void main(String[] args){System.out.println("Hello World!");}

	/**
     * Receives the boolean 2D array containing information about the
     * locations of edges in the image.
     * @param edgeMatrix boolean[][] true if the pixel is on an edge, false if not
     */
    public void setEdgeMatrix(boolean[][] edgeMatrix){
        this.edgeMatrix = edgeMatrix;
        System.out.println("Edge matrix loaded.");
    }

    /**
     * Gets an ArrayList containing the coefficients for the parametric
     * splines that compose the image.
     * @param resolution the number of pixels to skip when generating the splines
     * @return ArrayList containing one parametric spline for each edge
     */
    public ArrayList<ArrayList<Double[]>> getCoefficients(int resolution){
        int x = edgeMatrix.length/2;
        int y = edgeMatrix[0].length/2;
    	return new ArrayList<>();
    }

    /**
     * Gets a list of edges and the points that compose them
     * @return ArrayList containing one list of points for each line in the matrix
     */
    private ArrayList<ArrayList<int[]>> getEdgePoints(){
        boolean[][] available = Arrays.copyOf(edgeMatrix, edgeMatrix.length);
        getSurrounding(0,0,available);
        return new ArrayList<>();

    }

    /**
     * Gets a list of points that are withing the sensitivity limit around a point
     * @param x x coordinate to search around
     * @param y y coordinate to search around
     * @param available 2D array containing the available pixels
     * @return list of points in the surrounding area
     */
    private ArrayList<int[]> getSurrounding(int x, int y, boolean[][] available){
        ArrayList<int[]> surrounding = new ArrayList<>();
        for(int i = x - SENSITIVITY; i <= x + SENSITIVITY; x++){
            for(int j = y - SENSITIVITY; j <= y + SENSITIVITY; y++){
                if(available[i][j]) {
                    int point[] = {x, y};
                    surrounding.add(point);
                }
            }
        }
        return surrounding;
    }


}
