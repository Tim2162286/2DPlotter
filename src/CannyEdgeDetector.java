import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by Tim on 2/8/2016.
 */
public class CannyEdgeDetector implements EdgeDetector {

    public static void main(String[] args) throws IOException {

        CannyEdgeDetector detector = new CannyEdgeDetector();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        File imgPath = new File("C:/Users/Tim/Pictures/generator_pic");
        BufferedImage img = ImageIO.read(imgPath);

    }
    public boolean[][] getEdgeMatrix() {
        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){

    }
}
