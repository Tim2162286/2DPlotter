/**
 * The ToolpathGenerator class is used to generate
 * a toolpath consisting of points from a JPEG image 
 * and save it to a text file.
 * @author Jonathan Bush
 * @version 2015.5.17
 */

import java.io.*;
import java.util.ArrayList;

public class ParametricSplineInterpolator implements SplineGenerator {

    private int maxDist;
    private int edgeDist;
    private int xDim;
    private int yDim;
    private double pathScale;
    private boolean[][] bwState;

    /**
     * Construct a new ParametricSplineInterpolator object
     * @param edgeDist color difference to define an edge
     * @param maxDist maximum line segment length
     */
    public ParametricSplineInterpolator(int edgeDist, int maxDist){
        this.maxDist = maxDist;
        this.edgeDist = edgeDist;
    }

    /**
     * Construct a new ParametricSplineInterpolator object
     * @param edgeDist color difference to define and edge
     * @param maxDist maximum line segment length
     * @param xDim X dimension of the plotter bed or desired size.
     * @param yDim Y dimension of the plotter bed or desired size.
     */
    public ParametricSplineInterpolator(int edgeDist, int maxDist, int xDim, int yDim){
        this(edgeDist, maxDist);
        this.xDim = xDim;
        this.yDim = yDim;
        pathScale = 1.0;
    }

    /**
     * Default constructor
     * maxDist = 10
     * edgeDist = 15
     */
    public ParametricSplineInterpolator(){
        this.maxDist = 10;
        this.edgeDist = 15;
    }

    /**
     * Generate a toolpath from image and save it to pathFile
     * @param image the Picture to be processed
     * @param pathFile the File where the toolpath should be saved
     */
    public ArrayList<ArrayList<Double[]>> getCoefficients(int resolution) {
        pathScale = (double)yDim/(double)bwState.length;
        if(pathScale*bwState[0].length > xDim){
            pathScale = (double)xDim/(double)bwState[0].length;
        }
        removeSingle(bwState);
        ArrayList<Integer[]> toolpath = generateToolpathRecursive(findLinesSpiral(bwState));
        writeToolpath(toolpath);
        return new ArrayList<ArrayList<Double[]>>();
    }

    public void setEdgeMatrix(boolean[][] matrix){
        bwState = matrix;
    }

    /**
     * Count the number of pixels that have not been
     * added to the toolpath.
     * @param bwState a boolean 2D array containing the black/white pixel states
     * @return number of true pixels (elements)
     */
    private int countTrue(boolean[][] bwState){
        int count = 0;
        for(boolean[] row : bwState){
            for(boolean element : row){
                if(element){
                    count++;
                }
            }
        }
        return count;
        //System.out.println(count);
    }

    /**
     * Write the complete toolpath ArrayList to a file
     * @param xyz ArrayList<Integer[]> containing ordered coordinates of the path
     */
    private ArrayList<ArrayList<Double[]>> writeToolpath(ArrayList<Integer[]> xyz) {

        int count = 0;
        for(Integer[] step : xyz){
            for(int i = 0; i < 2; i++){
                int n = (int)(pathScale*step[i]);

            }
        }
        return new ArrayList<ArrayList<Double[]>>();
    }

    /**
     * Generate the toolpath from the output of the findLinesRecursive method.
     * @param pointSets ArrayList<ArrayList<Integer[]>> containing sets of Integer {x,y} coordinates
     *    for each point in each line.
     * @return ArrayList<Integer[]> of {x,y,z} coordinates
     */
    private ArrayList<Integer[]> generateToolpathRecursive(ArrayList<ArrayList<Integer[]>> pointSets){
        //ArrayList<Integer[]> path = new ArrayList<Integer[]>();

        ArrayList<Integer[]> sub = null;
        for(int i = 0; i < pointSets.size(); i++){
            int count = 0;
            sub = pointSets.get(i);
            for(int j = 1; j < pointSets.get(i).size() - 1; j++){
                count++;
                if(count % maxDist != 0){
                    sub.remove(j);
                    j--;
                }
            }
        }

        removeIntermediate(pointSets);  //remove unnecessary points

        ArrayList<Integer[]> path = new ArrayList<Integer[]>();

        for(int i = 0; i < pointSets.size(); i++){
            sub = pointSets.get(i);
            Integer[] temp = new Integer[3];
            temp[0] = sub.get(0)[0];
            temp[1] = sub.get(0)[1];
            temp[2] = 0;
            path.add(temp);
            //System.out.println("0: " + Arrays.toString(temp));
            for(int j = 1; j <pointSets.get(i).size(); j++){
                temp = new Integer[3];
                temp[0] = sub.get(j)[0];
                temp[1] = sub.get(j)[1];
                temp[2] = 1;
                path.add(temp);
                //System.out.println(j + ": " + Arrays.toString(temp));
            }
        }
        return path;
    }

    /**
     * Finds a point adjacent to (x,y) in the bwState
     * @param bwState The 2D boolean array representing the states of pixels
     * @param x column coordinate to be checked
     * @param y row coordinate to be checked
     * @return int array containing {x,y} if a neighboring pixel is true
    returns {-1,-1} if no neighboring pixel is false
     */
    private int[] findAdjacent(boolean[][] bwState, int x, int y){
        for(int dx = -1; dx < 2; dx++){
            for(int dy = -1; dy < 2; dy++){
                if(!(dx == 0 && dy == 0)){
                    if(x + dx >= 0 && x + dx < bwState[0].length && y + dy >= 0 && y + dy < bwState.length){
                        if(bwState[y + dy][x + dx]){
                            int[] adj = new int[2];
                            adj[0] = x + dx;
                            adj[1] = y + dy;
                            return adj;
                        }
                    }
                }
            }
        }
        int[] adj = {-1,-1};
        return adj;
    }

    /**
     * Changes pixels with no neighboring true pixels to false
     * this way the plotter won't plot individual points or anomalies.
     * @param bwState the 2D array representing the states of the pixels
     */
    private void removeSingle(boolean[][] bwState){
        for(int y = 0; y < bwState.length; y++){
            for(int x = 0; x < bwState[0].length; x++){
                if(bwState[y][x]){
                    int[] temp = findAdjacent(bwState, x, y);
                    if(temp[0] == -1){
                        bwState[y][x] = false;
                    }
                }
            }
        }
    }

    /**
     * Removes pixels with 9 true neighbors
     * @param bwState 2D array representing states of pixels
     * @return modified bwState after changes
     */
    private boolean[][] removeEncased(boolean[][] bwState){
        boolean[][] state = new boolean[bwState.length][bwState[0].length];
        System.arraycopy(bwState, 0, state, 0, bwState.length);
        int count;
        for(int x = 1; x < state[0].length; x++){
            for(int y = 1; y < state.length; y++){
                count = 0;
                for(int dx = -1; dx < 2; dx++){
                    for(int dy = -1; dy < 2; dy++){
                        if(dx != 0 || dy != 0){
                            if(x + dx >= 0 && x + dx < bwState[0].length
                                    && y + dy >= 0 && y + dy < bwState.length){
                                if(state[y + dy][x + dx]){
                                    //System.out.println("here");
                                    count++;
                                }
                            }
                        }
                    }
                }
                if(count == 9){
                    bwState[y][x] = false;
                }
            }
        }
        return bwState;
    }

    /**
     * Recursively finds all points in a line starting from (x,y)
     *    to one end of the line.
     * @param bwState 2D array representing the states of pixels in the image
     * @param points ArrayList<Integer[]> containing points in this line
     * @param x coordinate to add to points and search from
     * @param y coordinate to add to points and search from
     * @return ArrayList<Integer[]> containing all points from starting (x,y) to one end of the line.
     */
    public ArrayList<Integer[]> findLine(boolean[][] bwState, ArrayList<Integer[]> points, int x, int y){
        Integer[] adjObj = new Integer[2];
        adjObj[0] = x;
        adjObj[1] = y;
        points.add(adjObj);
        bwState[y][x] = false;
        int[] adj = findAdjacent(bwState, x, y);
        if(adj[0] != -1){
            return findLine(bwState, points,adj[0], adj[1]);
        } else {
            return points;
        }
    }

    /**
     * Start the findLinesSpiralHelper method from the center of the bwState
     * @param bwState 2D array representing the pixel states in the image
     */
    public ArrayList<ArrayList<Integer[]>> findLinesSpiral(boolean[][] bwState){
        return findLinesSpiralHelper(bwState, new ArrayList<ArrayList<Integer[]>>(), bwState[0].length/2,
                bwState.length/2);
    }

    /**
     * Search for the next point not already in a line in an outward spiral pattern.
     * This finds the nearest next line as opposed to starting from the upper left again,
     * thus minimizing the amount of pen movement required. Call recursively until all lines
     * have been found and added to the point sets.
     * @param bwState 2D array representing the pixel states in the image
     * @param pointSets ArrayList<ArrayList<Integer[]>> object for storing lines and their points
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @return pointSets after all lines have been found
     */
    public ArrayList<ArrayList<Integer[]>> findLinesSpiralHelper(boolean[][] bwState,
                                                                 ArrayList<ArrayList<Integer[]>> pointSets, int x, int y){

        //int pixelsTested;
        boolean pixelOn = false;
        int length = 2;
        int pixelsTested = 0;
        do {
            int i = 0;
            int count = 0;
            x--;
            y++;
            do {
                count++;
                switch(i){
                    case 0:
                        x++;
                        break;
                    case 1:
                        y--;
                        break;
                    case 2:
                        x--;
                        break;
                    case 3:
                        y++;
                        break;
                }

                //System.out.println("(" + x + ", " + y + ") " + i);
                if(x >= 0 && y >= 0 && x < bwState[0].length && y < bwState.length){
                    pixelsTested++;
                    pixelOn = bwState[y][x];
                }
                if(count == length){
                    count = 0;
                    i++;
                }
            } while(i < 4  && !pixelOn);
            length += 2;
        } while(pixelsTested < bwState.length*bwState[0].length && !pixelOn);
        if(pixelOn){
            //System.out.println("lljljkljlj");
            pointSets.add(findLine(bwState, new ArrayList<Integer[]>(), x, y));
            removeSingle(bwState);
            Integer[] temp = pointSets.get(pointSets.size() - 1).get(pointSets.get(pointSets.size() - 1).size() -1);
            //System.out.println("(" + temp[0] + ", " + temp[1] + ") " + countTrue(bwState));
        }
        if(countTrue(bwState) != 0){
            Integer[] temp = pointSets.get(pointSets.size() - 1).get(pointSets.get(pointSets.size() - 1).size() -1);
            return findLinesSpiralHelper(bwState, pointSets, temp[0], temp[1]);
        } else {
            return pointSets;
        }
    }

    /**
     * Removes intermediate points on each straight line.
     * @param pointSets The collection of lines composed of points.
     */
    private void removeIntermediate(ArrayList<ArrayList<Integer[]>> pointSets){
        for(ArrayList<Integer[]> points : pointSets){
            for(int i = 1; i < points.size() - 1; i++){
                int dx0 = points.get(i)[0] - points.get(i-1)[0];
                int dy0 = points.get(i)[1] - points.get(i-1)[1];
                int dx1 = points.get(i+1)[0] - points.get(i)[0];
                int dy1 = points.get(i+1)[1] - points.get(i)[1];
                double mag0 = Math.sqrt(Math.pow(dx0,2) + Math.pow(dy0,2));
                double mag1 = Math.sqrt(Math.pow(dx1,2) + Math.pow(dy1,2));
                double i0 = dx0/mag0;
                double j0 = dy0/mag0;
                double i1 = dx1/mag1;
                double j1 = dy1/mag1;
                if(Math.abs(i1 - i0) < .001 && Math.abs(j1 - j0) < .001){
                    points.remove(i);
                    i--;
                }
            }
        }
    }

}