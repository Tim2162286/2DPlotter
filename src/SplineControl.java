/**
 * Class for testing the spine interpolation
 * This class should contain all necessary methods to demonstrate functionality
 */
public class SplineControl {

    /**
     * Test the ParametricSplineInterpolator class
     * @param args Arguments for running the program
     */
    public static void main(String[] args){
        SplineGenerator parametric = new ParametricSplineInterpolator();
        parametric.setEdgeMatrix(new boolean[4][4]);
    }
}
