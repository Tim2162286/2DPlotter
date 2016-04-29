import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Preforms Canny edge detection on an image.
 * @author Tim Cuprak
 * @since 2/8/2016
 */
public class CannyEdgeDetector implements EdgeDetector {

    private BufferedImage image;

    /**
     * Calls
     * @param blurRadius int radius of the mask used to preform gaussian blur.
     * @param blurLevel double sigma value of th gaussian blur used to generate matrix.
     * @return 2D boolean array, values are true if an edge, false if not.
     * @throws IOException
     */
    public boolean[][] getEdgeMatrix(int blurRadius,double blurLevel) throws IOException {
        int[][][] sobel = {{{-1,0,1},{-2,0,2},{-1,0,1}},
                            {{1,2,1},{0,0,0},{-1,-2,-1}}};
        int[] thresholds ={40,80};
        File output = new File("src\\ImageOut\\EdgeDetectorSteps\\Base Image.jpg");
        ImageIO.write(image, "jpg", output);
        BufferedImage grayImg = convertToGrayScale(image);
        int[][] grayArray = imageToMatrix(grayImg);
        int[][] blur = blur(blurLevel, blurRadius, grayArray);
        int[][][] gradient = gradientMagnitudes(blur,sobel);
        int[][] maximums = nonMximumSupression(gradient);
        return twoLevelHysteresis(maximums,thresholds);
    }

    /**
     * Calls getEdgeMatrix with default values of 2 and 1.41
     * @throws IOException
     */
    public boolean[][] getEdgeMatrix() throws IOException {
        return getEdgeMatrix(2, 1.41);

    }

    /**
     * Stores image to the local private variable image
     * @param img BufferedImage to process
     */

    public void loadImage(BufferedImage img){
        this.image = img;

    }

    /**
     * Copies the image to a buffered image set to greyscale.
     * @param image BufferedImage image to copy into greyscale image.
     * @return BufferedImage Greyscale copy of original image.
     * @throws IOException
     */

    private BufferedImage convertToGrayScale(BufferedImage image) throws IOException {
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        File output = new File("src\\ImageOut\\EdgeDetectorSteps\\Step 1-Grayscale.jpg");
        ImageIO.write(result, "jpg", output);
        return result;
    }

    /**
     * Converts a 2D integer matrix containing integer values between and including 0-255
     * into a buffered image object, and saves it to a new JPEG file.
     * @param matrix int[][] contains values 0-255
     * @param name name to save file as.
     * @throws IOException
     */

    private void matrixToImage(int[][] matrix,String name) throws IOException{
        int width = matrix.length;
        int height = matrix[0].length;
        int mag;
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                mag = Math.abs(matrix[i][j]);
                if (mag>255)
                    mag=255;
                Color gray = new Color(mag,mag,mag);
                img.setRGB(i,j,gray.getRGB());
            }
        }
        File output = new File("src\\ImageOut\\EdgeDetectorSteps\\"+name+".jpg");
        output.mkdirs();
        ImageIO.write(img, "jpg", output);
    }

    /**
     * Converts a BufferedImage to a 2D integer array of values 0-255
     * @param img BufferedImage image to convert to a matrix.
     * @return int[][] 2D array of values 0-255 of the same dimensions as the image.
     */

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

    /**
     * Performs gaussian blur on a 2D matrix of the image using a matrix generated based on radius and sigma parameters.
     * @param sigma double sigma value used to generate blur matrix.
     * @param radius int radius of the blur matrix to be generated.
     * @param img int[][] 2D matrix of values 0-255.
     * @return int[][] 2D matrix of values 0-255.
     */

    private int[][] blur(double sigma,int radius, int[][] img) throws IOException{
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
        matrixToImage(result,"Step 2-Blur");
        return result;
    }

    /**
     * Calculates the gradient magnitude and gradient direction of each element of the image array
     * @param img int[][] image matrix to find gradient of.
     * @param operator int[][][] the 2 operators used to find the x and y gradients.
     * @return int[][][] 3D array containing the gradient magnitude on layer 0, and gradient direction on layer 1
     * @throws IOException
     */

    private int[][][] gradientMagnitudes(int[][] img, int[][][] operator)throws IOException{
        int width = img.length;
        int height = img[0].length;
        int radius = operator[0][0].length/2;
        int diameter = operator[0][0].length;
        int[][] resultX = new int[width-2*radius][height-2*radius];
        int blockValX;
        int[][] resultY = new int[width-2*radius][height-2*radius];
        int blockValY;                                              int[][] gradX;
        int[][] gradY;
        for (int w = 0; w < width - 2 * radius; w++) {
            for (int h = 0; h < height - 2 * radius; h++) {
                blockValX = 0;
                blockValY = 0;
                for (int i = 0; i < diameter; i++) {
                    for (int j = 0; j < diameter; j++) {
                        blockValX += img[w + i][h + j] * operator[0][i][j];
                        blockValY += img[w + i][h + j] * operator[1][i][j];
                    }
                }
                resultX[w][h] = blockValX;
                resultY[w][h] = blockValY;
            }
        }
        gradX = resultX;
        gradY = resultY;
        int[][][] gradient = new int[2][][];
        int[][] gradientMag = new int[width][height];
        int[][] gradientDirection = new int[width][height];
        double direction;
        int mag;
        for (int y=0;y<height-2*radius;y++){
            for (int x = 0;x<width-2*radius;x++){
                mag = (int)Math.sqrt((double)((gradX[x][y]*gradX[x][y])+(gradY[x][y]*gradY[x][y])));
                gradientMag[x][y] = mag;
                direction = Math.round(Math.toDegrees(Math.atan2(gradY[x][y],gradX[x][y]))/45.0);
                gradientDirection[x][y] = (int)direction*45;
            }
        }
        gradient[0] = gradientMag;
        gradient[1] = gradientDirection;
        matrixToImage(gradX,"Step 3a-X Gradient");
        matrixToImage(gradY,"Step 3b-Y Gradient");
        matrixToImage(gradientMag,"Step 3d-Find Gradient");
        matrixToDirectionGradientImage(gradient,"Step 3c-Gradient Direction");
        return gradient;
    }

    /**
     * Finds the center of a line by removing all but the strongest pixel in the direction of the gradient.
     * @param gradient int[][][] 3d array, contains the gradient magnitude in position 0 and the gradient direction inn position 1
     * @return int[][] 2D array of the center of each edge line.
     * @throws IOException
     */
    int[][] nonMximumSupression(int[][][] gradient) throws IOException{
        int[][] mag = gradient[0];
        int[][] direction = gradient[1];
        int width = mag.length;
        int height = mag[0].length;
        boolean[][] maximum = new boolean[width][height];
        for(int w=1;w<width-1;w++){
            for(int h=1;h<height-1;h++){
                if ((direction[w][h]==0 || direction[w][h]==180 || direction[w][h]==-180) && mag[w][h]!=0){
                    if ((mag[w][h]>mag[w][h+1] && mag[w][h]>mag[w][h-1]) || mag[w][h]==mag[w][h+1] || mag[w][h]==mag[w][h-1])
                        maximum[w][h] = true;
                }
                else if (direction[w][h] == 45 || direction[w][h] == -135){
                    if ((mag[w][h]>mag[w+1][h+1] && mag[w][h]>mag[w-1][h-1]) || mag[w][h]==mag[w+1][h+1] || mag[w][h]==mag[w-1][h-1])
                        maximum[w][h] = true;
                }
                else if (direction[w][h] == 90 || direction[w][h] == -90){
                    if ((mag[w][h]>mag[w+1][h] && mag[w][h]>mag[w-1][h]) || mag[w][h]==mag[w+1][h] || mag[w][h]==mag[w-1][h])
                        maximum[w][h] = true;
                }
                else if (direction[w][h] == -45 || direction[w][h] == 135){
                    if ((mag[w][h]>mag[w-1][h+1] && mag[w][h]>mag[w+1][h-1]) || mag[w][h]==mag[w-1][h+1] || mag[w][h]==mag[w+1][h-1])
                        maximum[w][h] = true;
                }
            }
        }
        for(int w=0;w<width;w++){
            for(int h=0;h<height;h++){
                if(!maximum[w][h])
                    mag[w][h] = 0;
            }
        }
        matrixToImage(mag,"Step 4-Non-Maximum Suppression");
        return mag;
    }

    /**
     * Creates a colored image showing the direction of the gradients. blue is vertical, cyan is positive 45 degrees, green horizontal, and yellow, -45 degrees
     * @param gradient int[][][] contains two 2D arrays, gradient[0] contains the magnitude of the gradients, gradient[1] contains the direction of the gradient
     * @param name String name to call the image
     * @throws IOException
     */

    void matrixToDirectionGradientImage(int[][][] gradient, String name)throws IOException{
        int[][] direction = gradient[1];
        int[][] mag = gradient[0];
        int width = direction.length;
        int height = direction[0].length;
        Color color = new Color(0,0,0);
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){

                if ((direction[i][j]==0 || direction[i][j]==180 || direction[i][j]==-180) && mag[i][j]!=0)
                    color = Color.BLUE;
                else if (direction[i][j]==-135 || direction[i][j]==45)
                    color = Color.CYAN;
                else if (direction[i][j]==90 || direction[i][j]==-90)
                    color = Color.GREEN;
                else if (direction[i][j]==-45 ||direction[i][j]==135)
                    color = Color.YELLOW;
                else{
                    color = Color.BLACK;
                }
                img.setRGB(i,j,color.getRGB());
            }
        }
        File output = new File("src\\ImageOut\\EdgeDetectorSteps\\"+name+".jpg");
        ImageIO.write(img, "jpg", output);
    }

    boolean[][] twoLevelHysteresis(int[][] matrix, int[] thresholds) throws IOException{
        int width = matrix.length;
        int height = matrix[0].length;
        boolean[][] results = new boolean[width][height];
        boolean pixelsAdded;
        for(int w=0;w<width;w++){
            for(int h=0;h<height;h++) {
                if (matrix[w][h] < thresholds[0])
                    matrix[w][h] = 0;
                else if (matrix[w][h] > thresholds[1])
                    results[w][h] = true;
            }
        }
        do {
            pixelsAdded = false;
            for (int w=1;w<width-1;w++){
                for (int h=1;h<height-1;h++){
                    if (!results[w][h] && matrix[w][h]>=thresholds[0])
                    for (int i=-1;i<2;i++){
                        for (int j=-1;j<2;j++){
                            if (results[w+i][h+j]){
                                results[w][h] = true;
                                pixelsAdded = true;
                            }
                        }
                    }
                }
            }
        } while (pixelsAdded);
        matrixToImage(matrix,"test1");
        for (int w=0;w<width;w++){
            for (int h=0;h<height;h++){
                if (results[w][h])
                    matrix[w][h] = 255;
                else
                    matrix[w][h] = 0;
            }
        }
        matrixToImage(matrix,"test2");
        return results;
    }
    //int[] getThreshold(int[][]){}
}