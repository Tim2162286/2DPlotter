import java.io.File;
import java.util.ArrayList;

/**
 * Draws out the spline to simulate plotting
 * @author Jonathan Bush
 * @since 2/16/2016.
 */
public class ParametricSplineWriter implements SplineWriter{

    /**
     * Generates an image from a series of parametric splines and saves it
     * to a file for easy evaluation.
     *
     * @param splines ArrayList containing the coefficients for each spline
     * @param image   the file to save the generated image to
     * @param format  the format to save the image as ("gif", "png", "jpg")
     */
    public void splineToImage(ArrayList<ArrayList<Double[]>> splines, File image, String format) {

    }

}
