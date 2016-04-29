import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
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
     * @param gifFile File to save the animated gif to
     * @param format  the format to save the image as ("gif", "png", "jpg")
     */
    public void splineToImage(ArrayList<ArrayList<Integer[]>> splines, File imageFile, File gifFile, String format,
                              int width, int height) throws IOException {

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = img.createGraphics();
        ImageOutputStream gifOut = new FileImageOutputStream(gifFile);
        GifSequenceWriter gifWriter = new GifSequenceWriter(gifOut, img.getType(), 10/*ms*/, true);
        for(ArrayList<Integer[]> line : splines) {
            //System.out.println("line -----------------------------------");//Draw the splines onto the image
            for (int i = 1; i < line.size(); i++) {
                Integer[] pt1 = line.get(i - 1);
                Integer[] pt2 = line.get(i);
                graphics.drawLine(pt1[0], pt1[1], pt2[0], pt2[1]);
            }
            gifWriter.writeToSequence(img);
            /*for(Integer[] point : line) {
                System.out.println("(" + point[0] + ", " + point[1] + ")");
            }*/
        }
        ImageIO.write(img, format, imageFile);
    }

    /**
     * Generates an image from a series of parametric splines and saves it
     * to a file for easy evaluation.
     *
     * @param splines ArrayList containing the coefficients for each spline
     * @param imageFile the file to save the generated image to
     * @param format  the format to save the image as ("gif", "png", "jpg")
     */
    public void splineToImage(ArrayList<ArrayList<Integer[]>> splines, File imageFile, String format, int width, int height) {
        try {
            File animated = new File("src\\ImageOut\\SplineAnimation.gif");
            //animated.mkdirs();
            splineToImage(splines, imageFile, animated, format, width, height);
        } catch (IOException e) {
            System.out.println("There was an error creating the image file.");
        }
    }


}
