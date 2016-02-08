import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Tim on 2/8/2016.
 */
public class EdgeDetectorControl {
    public static void main(String[] args) throws IOException {

        CannyEdgeDetector detector = new CannyEdgeDetector();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        //String directory = stdin.readLine();
        //String file = stdin.readLine();
        //File imgPath = new File(directory+"/"+file);
        File imgPath = new File("C:/Users/Tim/Pictures/Caves.jpg");
        BufferedImage img = ImageIO.read(imgPath);
        detector.loadImage(img);
        detector.getEdgeMatrix();
    }
}
