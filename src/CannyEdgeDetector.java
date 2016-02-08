import javax.imageio.ImageIO;
import java.awt.*;
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

    public boolean[][] getEdgeMatrix() throws IOException {
        int[][] grayImage = convertToGrayscale(image);
        matrixToImage(grayImage);

        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){
        this.image = img;

    }
    public int[][] convertToGrayscale(BufferedImage img) throws IOException{
        File output = new File("original.jpg");
        ImageIO.write(img, "jpg", output);
        int[] dims = {img.getHeight(),img.getWidth()};
        int[][] grayImage = new int[dims[0]][dims[1]];
        for (int i=0; i<dims[1];i++){
            for (int j=0; j<dims[0];j++){
                int rgb = img.getRGB(i,j);
                int r = (rgb)&0xFF;
                int g = (rgb>>8)&0xFF;
                int b = (rgb>>16)&0xFF;
                grayImage[j][i] = (r+g+b)/3;
            }
        }
        return grayImage;
    }
    public BufferedImage matrixToImage(int[][] matrix) throws IOException{
        int width = matrix.length;
        int height = matrix[0].length;
        BufferedImage img = new BufferedImage(height,width,BufferedImage.TYPE_BYTE_GRAY);
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                Color gray = new Color(matrix[i][j],matrix[i][j],matrix[i][j]);
                img.setRGB(j,i,gray.getRGB());
            }
        }
        File output = new File("grayscale.jpg");
        ImageIO.write(img, "jpg", output);
        return img;
    }
}
