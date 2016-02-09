import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * An EdgeDetector will accept an image in a standart format and
 * generate a boolean 2D array indicating which pixels are on an
 * edge in the image.
 */
public interface EdgeDetector {

    /**
     * Sets the image to be processed by the edge detection algorithm
     * @param img BufferedImage to process
     */
    public void loadImage(BufferedImage img);

    /**
     * Get a boolean 2D array indicating whether each pixel in the
     * image is on a detected edge. Calling this method will run the
     * edge detection algorithm.
     * @return boolean[][] true if the pixel is on an edge, false if not
     */
    public boolean[][] getEdgeMatrix() throws IOException;

}