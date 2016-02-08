import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by Tim on 2/8/2016.
 */
public class CannyEdgeDetector implements EdgeDetector {

    private BufferedImage image;

    public boolean[][] getEdgeMatrix() throws IOException {
        BufferedImage grayImg = convertToGrayScale(image);
        int[][] grayArray = imageToMatrix(grayImg);
        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){
        this.image = img;

    }

    public static BufferedImage convertToGrayScale(BufferedImage image) throws IOException {
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        File output = new File("GrayScaled.jpg");
        ImageIO.write(result, "jpg", output);
        return result;
    }

    public BufferedImage matrixToImage(int[][] matrix,String name) throws IOException{
        int width = matrix.length;
        int height = matrix[0].length;
        BufferedImage img = new BufferedImage(height,width,BufferedImage.TYPE_BYTE_GRAY);
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                Color gray = new Color(matrix[i][j],matrix[i][j],matrix[i][j]);
                img.setRGB(j,i,gray.getRGB());
            }
        }
        File output = new File(name+".jpg");
        ImageIO.write(img, "jpg", output);
        return img;
    }
    private int[][] imageToMatrix(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] matrix = new int[height][width];
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                Color c = new Color(img.getRGB(i,j));
                matrix[j][i] = c.getRed();
            }
        }
        return matrix;
    }
    private void printMatrix(int[][] matrix){
        for (int[] i:matrix){
            for (int j:i)
                System.out.print(j+" ");
            System.out.print("\n");
        }
    }

    private int[][] convolve(int[][] mask, int[][] img){
        int radius = mask.length/2;
        int width = img
    }
}
