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
        File imgPath = new File("C:/Users/Tim/Pictures/2014 Swim and Dive/2014 Swim and Dive/Charger Invite Swim 10.23.14/IMG_0242.jpg");
        BufferedImage img = ImageIO.read(imgPath);
        detector.loadImage(img);
        detector.getEdgeMatrix();
    }
}
