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

    public boolean[][] getEdgeMatrix(int blurRadius,double blurLevel) throws IOException {
        int[][][] sobel = {{{-1,0,1},{-2,0,2},{-1,0,1}},{{1,2,1},{0,0,0},{-1,-2,-1}}};

        File output = new File("src\\Images\\Base Image.jpg");
        ImageIO.write(image, "jpg", output);
        BufferedImage grayImg = convertToGrayScale(image);
        int[][] grayArray = imageToMatrix(grayImg);
        int[][] blur = blur(blurLevel, blurRadius, grayArray);
        matrixToImage(blur,"Step 2-Blur");
        int[][][] gradient = gradientMagnitudes(blur,sobel);
        //printMatrix(gradient);
        return new boolean[0][];
    }

    public boolean[][] getEdgeMatrix() throws IOException {
        int[][][] sobel = {{{-1,0,1},{-2,0,2},{-1,0,1}},{{1,2,1},{0,0,0},{-1,-2,-1}}};

        File output = new File("src\\Images\\Base Image.jpg");
        ImageIO.write(image, "jpg", output);
        BufferedImage grayImg = convertToGrayScale(image);
        int[][] grayArray = imageToMatrix(grayImg);
        int[][] blur = blur(1.41, 2, grayArray);
        matrixToImage(blur,"Step 2-Blur");
        int[][][] gradient = gradientMagnitudes(blur,sobel);
        //printMatrix(gradient);
        return new boolean[0][];
    }

    public void loadImage(BufferedImage img){
        this.image = img;

    }

    private BufferedImage convertToGrayScale(BufferedImage image) throws IOException {
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        File output = new File("src\\Images\\Step 1-Grayscale.jpg");
        ImageIO.write(result, "jpg", output);
        return result;
    }

    private BufferedImage matrixToImage(int[][] matrix,String name) throws IOException{
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
        for (int y=0;y<height;y++){
            for (int x=0;x<width;x++){
                Color c = new Color(img.getRGB(x,y));
                matrix[x][y] = c.getRed();
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
        int width = img.length;
        int height = img[0].length;
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

    private int[][][] gradientMagnitudes(int[][] img, int[][][] operator)throws IOException{
        int width = img.length;
        int height = img[0].length;
        int radius = operator[0][0].length/2;
        int diameter = operator[0][0].length;
        int[][] result = new int[width-2*radius][height-2*radius];
        int blockVal;
        int[][] gradX;
        int[][] gradY;
        for (int w = 0; w < width - 2 * radius; w++) {
            for (int h = 0; h < height - 2 * radius; h++) {
                blockVal = 0;
                for (int i = 0; i < diameter; i++) {
                    for (int j = 0; j < diameter; j++) {
                        blockVal += img[w + i][h + j] * operator[0][i][j];
                        }
                    }
                    result[w][h] = blockVal;
                }
            }
        gradX = result;
        for (int w = 0; w < width - 2 * radius; w++) {
            for (int h = 0; h < height - 2 * radius; h++) {
                blockVal = 0;
                for (int i = 0; i < diameter; i++) {
                    for (int j = 0; j < diameter; j++) {
                        blockVal += img[w + i][h + j] * operator[1][i][j];
                    }
                }
                result[w][h] = blockVal;
            }
        }
        gradY = result;
        int[][][] gradient = new int[2][][];
        int[][] gradientMag = new int[width][height];
        int[][] gradientDirection = new int[width][height];
        double direction;
        int mag;
        for (int y=0;y<height-2*radius;y++){
            for (int x = 0;x<width-2*radius;x++){
                mag = (int)Math.sqrt((double)((gradX[x][y]*gradX[x][y])+(gradY[x][y]*gradY[x][y])));
                if (mag>255)
                    mag = 255;
                gradientMag[x][y] = mag;
                direction = (int)Math.abs(Math.toDegrees(Math.atan2(gradX[x][y],gradY[x][y]))/45);
                gradientDirection[x][y] = (int)direction*45;

            }
        }
        gradient[0] = gradientMag;
        gradient[1] = gradientDirection;
        matrixToImage(gradientMag,"Step 3-Find Gradient");
        return gradient;
    }

}