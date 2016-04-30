import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Simple class to accept image path as argument. Meant to be used with php script to support
 * online processing of uploaded images.
 */
public class SplineCLI {
    public static void main(String[] args) throws IOException {
        String imgPath = args[0];
        File imgFile = new File(imgPath);
        String imgType = "";
        EdgeDetector detector = new CannyEdgeDetector();
        BufferedImage img = ImageIO.read(imgFile);
        detector.loadImage(img);
        boolean[][] edges = detector.getEdgeMatrix();
        SplineGenerator parametric = new ParametricSplineInterpolator(2, edges.length, edges[0].length);
        parametric.setEdgeMatrix(edges);
        ParametricSplineWriter psWriter = new ParametricSplineWriter();
        File image = new File("src/ImageOut/SplineOutput.png");
        image.getParentFile().mkdirs();
        image.createNewFile();
        psWriter.splineToImage(parametric.getSpline(), image, "png", edges.length, edges[0].length);
    }
}
