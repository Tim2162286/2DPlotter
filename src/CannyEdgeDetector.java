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

    private BufferedImage image;

    public boolean[][] getEdgeMatrix() {
        int[] dimentions  = {image.getWidth(),image.getHeight()};
        convertToGrayscale(image, dimentions);

        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){
        this.image = img;

    }
    public int[][] convertToGrayscale(BufferedImage img, int[] dims){
        int[][] grayImage = new int[dims[0]][dims[1]];
        for (int i=0; i<dims[0];i++){
            for (int j=0; j<dims[1];j++){
                int rgb = img.getRGB(i,j);
                int r = (rgb)&0xFF;
                int g = (rgb>>8)&0xFF;
                int b = (rgb>>16)&0xFF;
                grayImage[i][j] = (r+b+g)/3;
            }
        }
        return grayImage;
    }
}
