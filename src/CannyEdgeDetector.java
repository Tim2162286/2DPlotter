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
        File output = new File("src\\Images\\Base Image.jpg");
        ImageIO.write(image, "jpg", output);
        BufferedImage grayImg = convertToGrayScale(image);
        int[][] sobelX = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] sobelY = {{1,2,1},{0,0,0},{-1,-2,-1}};
        int[][] grayArray = imageToMatrix(grayImg);
        int[][] blur = blur(1.5, 2, grayArray);
        matrixToImage(blur,"Post Gaussian Blur");
        int[][] xGradient = convolution(sobelX, blur);
        int[][] yGradient = convolution(sobelY, blur);
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
        File output = new File("src\\Images\\GrayScaled.jpg");
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
        File output = new File("src\\Images\\"+name+".jpg");
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
        int[][] blurMask = new int[diameter][diameter];
        double multiplier = 2/((1/(2*Math.PI*sigma*sigma))*Math.exp(-(((-radius)*(-radius)+
                (-radius)*(-radius))/(2*sigma*sigma))));
        int sum = 0;

        int val;
        for (int i =0;i<diameter;i++){
            for (int j=0;j<diameter;j++){
                val = (int)Math.round(((1/(2*Math.PI*sigma*sigma))*Math.exp(-(((i-radius)*(i-radius)+
                        (j-radius)*(j-radius))/(2*sigma*sigma))))*multiplier);
                blurMask[i][j] = val;
                sum += val;
            }
        }

        printMatrix(blurMask);
        System.out.println(sum);
        int width = img.length;
        int height = img[0].length;
        int[][] result = new int[width-2*radius][height-2*radius];
        int blockVal;
        for (int w=0;w<width-2*radius;w++){
            for (int h=0;h<height-2*radius;h++){
                blockVal=0;
                for (int i=0;i<diameter;i++){
                    for (int j=0;j<diameter;j++){
                        blockVal += img[w+i][h+j]*blurMask[i][j];
                    }
                }
                result[w][h] = blockVal/sum;

            }
        }
        return result;
    }

    private int[][] convolution(int[][] mask, int[][] img){
        int radius = mask.length/2;
        int diameter = (2*radius)+1;
        int width = img.length;
        int height = img[0].length;
        int[][] result = new int[width-2*radius][height-2*radius];
        int blockVal;
        for (int w=0;w<width-2*radius;w++){
            for (int h=0;h<height-2*radius;h++){
                blockVal=0;
                for (int i=0;i<diameter;i++){
                    for (int j=0;j<diameter;j++){
                        blockVal += img[w+i][h+j]*mask[i][j];
                    }
                }
                result[w][h] = blockVal;

            }
        }
        return result;
    }
    private int[][] gradientMagnitudes(){
        
    }
}
