import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
     * @param imageFile the file to save the generated image to
     * @param format  the format to save the image as ("gif", "png", "jpg")
     */
    public void splineToImage(ArrayList<ArrayList<Double[]>> splines, File imageFile, String format) {

        BufferedImage img = new BufferedImage(768, 1024, BufferedImage.TYPE_BYTE_GRAY);
        for(ArrayList<Double[]> line : splines){
            System.out.println("coefficients: ");//Draw the splines onto the image
        }
        try {
            ImageIO.write(img, "jpg", imageFile);
        } catch(IOException e){
            System.out.println("There was an error saving the image.");
        }
    }

}
