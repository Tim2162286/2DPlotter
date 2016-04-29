import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Requests and processes and image file into an animated GIF drawing and a sketch.
 * The folder "EdgeDetectorSteps" also contains snapshots of each intermediate step.
 *
 * CannyEdgeDetector and EdgeDetectorControl by Timothy Cuprak
 * ParametricSplineInterpolator and ParametricSplineWriter by Jonathan Bush
 *
 * @author Jonathan Bush
 * @since 2016-04-28
 */
public class MasterControl {
    /**
     * Test the ParametricSplineInterpolator class
     *
     * @param args Arguments for running the program
     */
    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Image URI: ");
        String imgPath = stdin.readLine();
        File imgFile = new File(imgPath);
        String imgType = "";
        while ((!imgFile.exists() || imgFile.isDirectory())
                || ( !((imgType = Files.probeContentType(Paths.get(imgPath))).equals("image/png"))
                && !imgType.equals("image/jpeg") && !(imgType.equals("image/gif"))) ) {
            //System.out.println(imgType);
            System.out.println("\nInvalid file, must be JPG, PNG, or GIF. Please try again.");
            System.out.print("Image URI: ");
            imgPath = stdin.readLine();
            imgFile = new File(imgPath);
        }
        EdgeDetector detector = new CannyEdgeDetector();
        BufferedImage img = ImageIO.read(imgFile);
        detector.loadImage(img);
        boolean[][] edges = detector.getEdgeMatrix();
        SplineGenerator parametric = new ParametricSplineInterpolator(2, edges.length, edges[0].length);
        parametric.setEdgeMatrix(edges);
        ParametricSplineWriter psWriter = new ParametricSplineWriter();
        File image = new File("src\\ImageOut\\SplineOutput.png");
        image.getParentFile().mkdirs();
        image.createNewFile();
        psWriter.splineToImage(parametric.getSpline(), image, "png", edges.length, edges[0].length);
    }
}
