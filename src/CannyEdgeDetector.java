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
        int[][] sobelX = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] grayArray = imageToMatrix(grayImg);
        int[][] blur = blur(1.41, 2, grayArray);
        //matrixToImage(blur,"test");
        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){
        this.image = img;

    }

    private static BufferedImage convertToGrayScale(BufferedImage image) throws IOException {
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
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                Color gray = new Color(matrix[i][j],matrix[i][j],matrix[i][j]);
                img.setRGB(i,j,gray.getRGB());
            }
        }
        File output = new File(name+".jpg");
        ImageIO.write(img, "jpg", output);
        return img;
    }
    private int[][] imageToMatrix(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] matrix = new int[width][height];
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                Color c = new Color(img.getRGB(i,j));
                matrix[i][j] = c.getRed();
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

    private int[][] blur(double sigma,int radius, int[][] img){
        int diameter = (2*radius)+1;
        double[][] blurRaw = new double[diameter][diameter];
        double sum = 0;
        double val;
        for (int i =0;i<diameter;i++){
            for (int j=0;j<diameter;j++){
                val = ((1/(2*Math.PI*sigma*sigma))*Math.exp(-(((i-radius)*(i-radius)+
                        (j-radius)*(j-radius))/(2*sigma*sigma))));
                blurRaw[i][j] = val;
                sum += val;
                System.out.print(val+" ");
            }
            System.out.print("\n");
        }
        double multiplier = 2/blurRaw[0][0];
        int [][] blurMask = new int[diameter][diameter];
        int cell;
        int total=0;
        for (int i =0;i<diameter;i++) {
            for (int j = 0; j < diameter; j++) {
                cell = (int)Math.round(blurRaw[i][j]*multiplier);
                blurMask[i][j] = cell;
                total += cell;
            }
        }
        printMatrix(blurMask);
        int width = img.length;
        int height = img[0].length;
        int[][] result = new int[width-2*radius][height-2*radius];
        for (int w=0;w<width-2*radius;w++){
            for (int h=0;h<height-2*radius;h++){
                for (int i=0;i<diameter;i++){
                    for (int j=0;j<diameter;j++){

                    }
                }
            }
        }
        return result;
    }
}
