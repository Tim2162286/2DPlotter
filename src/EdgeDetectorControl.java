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
        /*
        String directory = stdin.readLine();
        String file = stdin.readLine();
        File imgPath = new File(directory+"/"+file);
        */
        File imgPath = new File("C:/Users/Tim/Pictures/butterfly.jpg");
        BufferedImage img = ImageIO.read(imgPath);
        detector.loadImage(img);
        System.out.println("Use default args (2, 1.41)? y/n");
        String useDefault = "Y";//stdin.readLine().toUpperCase();
        if (useDefault.equals("Y") || useDefault.equals("YES"))
            detector.getEdgeMatrix();
        else{
            System.out.println("Enter the radius and sigma values separated by a space (ex. 2 1.41)");
            String[] blurInf = stdin.readLine().split("\\s");
            detector.getEdgeMatrix(Integer.parseInt(blurInf[0]),Double.parseDouble(blurInf[1]));
        }

    }
}
