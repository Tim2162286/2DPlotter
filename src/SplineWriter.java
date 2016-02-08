import java.io.File;
import java.util.ArrayList;

/**
 * A SplineWriter will accept information about the splines that approximate
 * the edges in an image and will save that information as viewable image file.
 */
public interface SplineWriter {

    /**
     * Generates an image from a series of parametric splines and saves it
     * to a file for easy evaluation.
     * @param splines ArrayList containing the coefficients for each spline
     * @param image the file to save the generated image to
     * @param format the format to save the image as ("gif", "png", "jpg")
     */
    public void splineToImage(ArrayList<ArrayList<Double[]>> splines, File image, String format);

}