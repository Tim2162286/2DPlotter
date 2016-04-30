import java.util.ArrayList;

/**
 * The ParametricSplineInterpolator class is used to generate
 * a toolpath consisting of points taken from an edge detector.
 * @author Jonathan Bush
 * @since 2016-01-20
 */

public class ParametricSplineInterpolator implements SplineGenerator {

    private int minDist;
    private int xDim;
    private int yDim;
    private double pathScale;
    private boolean[][] bwState;

    /**
     * Construct a new ParametricSplineInterpolator object
     * @param resolution minimum line segment length
     */
    public ParametricSplineInterpolator(int resolution) {
        this.minDist = resolution;
        this.xDim = 1024;  // default dimensions
        this.yDim = 1024;
    }

    /**
     * Construct a new ParametricSplineInterpolator object
     * @param minDist maximum line segment length
     * @param xDim X dimension of the plotter bed or desired size.
     * @param yDim Y dimension of the plotter bed or desired size.
     */
    public ParametricSplineInterpolator(int minDist, int xDim, int yDim) {
        this(minDist);
        this.xDim = xDim;
        this.yDim = yDim;
        pathScale = 1.0;
    }

    /**
     * Default constructor
     * minDist = 10
     */
    public ParametricSplineInterpolator(){
        this.minDist = 10;
    }

    /**
     * Generate a toolpath from image and return the result
     * @return The processed toolpath
     */
    public ArrayList<ArrayList<Integer[]>> getSpline() {
        pathScale = (double)yDim / (double)bwState.length;  // automatically set the scale to fit in the plotter
        if(pathScale * bwState[0].length > xDim) {
            pathScale = (double)xDim/(double)bwState[0].length;
        }
        removeSingle(bwState);  // remove any points in the matrix

        return generateToolpathRecursive(findLinesSpiral(bwState));  // generate the toolpath
    }

    /**
     * Sets the matrix containing the edges to be processed
     * @param matrix Boolean matrix with true on edges
     */
    public void setEdgeMatrix(boolean[][] matrix) {
        bwState = new boolean[matrix.length][matrix[0].length];
        /* deep copy the matrix because bwState will be destroyed */
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                bwState[i][j] = matrix[i][j];
            }
        }
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
     * Generate the toolpath from the output of the findLinesRecursive method.
     * @param pointSets ArrayList<ArrayList<Integer[]>> containing sets of Integer {x,y} coordinates
     *    for each point in each line.
     * @return ArrayList of {x,y,z} coordinates
     */
    private ArrayList<ArrayList<Integer[]>> generateToolpathRecursive(ArrayList<ArrayList<Integer[]>> pointSets){

        for (ArrayList<Integer[]> sub : pointSets) {  // for each line in the set of points
            int count = 0;
            for (int j = 1; j < sub.size() - 1; j++) {  // don't remove the first and last points
                if (++count % minDist != 0) {
                    sub.remove(j);
                    j--;
                }
            }
        }

        removeIntermediate(pointSets);  //remove unnecessary points on straight lines

        return pointSets;

    }

    /**
     * Finds a point adjacent to (x,y) in the bwState
     * @param bwState The 2D boolean array representing the states of pixels
     * @param x column coordinate to be checked
     * @param y row coordinate to be checked
     * @return int array containing {x,y} if a neighboring pixel is true, {-1,-1} if no neighboring pixel is true
     */
    private int[] findAdjacent(boolean[][] bwState, int x, int y) {
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                if (!(dx == 0 && dy == 0)) {  // If this is not the center point
                    if (x + dx >= 0 && x + dx < bwState[0].length && y + dy >= 0 && y + dy < bwState.length) {
                        if (bwState[y + dy][x + dx]) {
                            int[] adj = new int[2];
                            adj[0] = x + dx;
                            adj[1] = y + dy;
                            return adj;  // return the point and exit immediately
                        }
                    }
                }
            }
        }
        return new int[] {-1,-1};  // return the default point if none were found
    }

    /**
     * Changes pixels with no neighboring true pixels to false
     * this way the plotter won't plot individual points or anomalies.
     * @param bwState the 2D array representing the states of the pixels
     */
    private void removeSingle(boolean[][] bwState) {
        for (int y = 0; y < bwState.length; y++) {
            for (int x = 0; x < bwState[0].length; x++) {
                if (bwState[y][x]){
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
    private boolean[][] removeEncased(boolean[][] bwState) {
        boolean[][] state = new boolean[bwState.length][bwState[0].length];
        System.arraycopy(bwState, 0, state, 0, bwState.length);
        int count;
        for (int x = 1; x < state[0].length; x++) {
            for (int y = 1; y < state.length; y++) {
                count = 0;
                for (int dx = -1; dx < 2; dx++){
                    for (int dy = -1; dy < 2; dy++) {
                        if (dx != 0 || dy != 0) {
                            if (x + dx >= 0 && x + dx < bwState[0].length
                                    && y + dy >= 0 && y + dy < bwState.length) {
                                if (state[y + dy][x + dx]) {
                                    count++;
                                }
                            }
                        }
                    }
                }
                if (count == 9) {
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

        return (adj[0] != -1) ? findLine(bwState, points, adj[0], adj[1]) : points;
    }

    /**
     * Start the findLinesSpiralHelper method from the center of the bwState
     * @param bwState 2D array representing the pixel states in the image
     */
    public ArrayList<ArrayList<Integer[]>> findLinesSpiral(boolean[][] bwState){
        return findLinesSpiralIterative(bwState, new ArrayList<ArrayList<Integer[]>>(), bwState[0].length/2,
                bwState.length/2);
    }

    /**
     * Search for the next point not already in a line in an outward spiral pattern.
     * This finds the nearest next line as opposed to starting from the upper left again,
     * thus minimizing the amount of pen movement required. Call recursively until all lines
     * have been found and added to the point sets.
     *
     * @param bwState 2D array representing the pixel states in the image
     * @param pointSets ArrayList<ArrayList<Integer[]>> object for storing lines and their points
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @return pointSets after all lines have been found
     */
    /*public ArrayList<ArrayList<Integer[]>> findLinesSpiralRecursive(boolean[][] bwState,
                                                                 ArrayList<ArrayList<Integer[]>> pointSets,
                                                                 int x,
                                                                 int y) {
        boolean pixelOn = false;
        int length = 2;
        int pixelsTested = 0;
        do {
            int i = 0;
            int count = 0;
            x--;
            y++;
            do {  // this loops outward in a square spiral pattern
                count++;
                switch (i) {
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
                if (x >= 0 && y >= 0 && x < bwState[0].length && y < bwState.length) {  // (x,y) is inside the image
                    pixelsTested++;  // count the number of pixels tested so we know when we are done
                    pixelOn = bwState[y][x];   // set this for use in the loop condition
                }
                if(count == length){  // once the current side length is reached
                    count = 0;  // reset the count and increment to the next side
                    i++;
                }
            } while(i < 4  && !pixelOn);  // go around the whole square, unless a point is found
            length += 2;  // increase the spiral side length by two
        } while(pixelsTested < bwState.length*bwState[0].length && !pixelOn);
        if(pixelOn){
            pointSets.add(findLine(bwState, new ArrayList<Integer[]>(), x, y));
            removeSingle(bwState);  // remove any points that are by themselves
        }
        if (countTrue(bwState) != 0) {  // Double-tail recursion
            Integer[] temp = pointSets.get(pointSets.size() - 1).get(pointSets.get(pointSets.size() - 1).size() -1);
            return findLinesSpiralRecursive(bwState, pointSets, temp[0], temp[1]);
        } else {
            return pointSets;   // when there are no more points, return the result
        }
        /*
         * Note, this method could have been implemented iteratively, which would have most likely been more efficient.
         * Due to time constraints, I have decided to retain the recursive method, which may limit the maximum line
         * length depending on memory availability. This is not ideal, but should work in the vast majority of cases.
         */
    //}

    private ArrayList<ArrayList<Integer[]>> findLinesSpiralIterative(boolean[][] bwState,
                                                                     ArrayList<ArrayList<Integer[]>> pointSets,
                                                                     int x,
                                                                     int y) {
        while (countTrue(bwState) != 0) {
            boolean pixelOn = false;
            int length = 2;
            int pixelsTested = 0;
            do {
                int i = 0;
                int count = 0;
                x--;
                y++;
                do {  // this loops outward in a square spiral pattern
                    count++;
                    switch (i) {
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
                    if (x >= 0 && y >= 0 && x < bwState[0].length && y < bwState.length) {  // (x,y) is inside the image
                        pixelsTested++;  // count the number of pixels tested so we know when we are done
                        pixelOn = bwState[y][x];   // set this for use in the loop condition
                    }
                    if (count == length) {  // once the current side length is reached
                        count = 0;  // reset the count and increment to the next side
                        i++;
                    }
                } while (i < 4 && !pixelOn);  // go around the whole square, unless a point is found
                length += 2;  // increase the spiral side length by two
            } while (pixelsTested < bwState.length * bwState[0].length && !pixelOn);
            if (pixelOn) {
                pointSets.add(findLine(bwState, new ArrayList<Integer[]>(), x, y));
                removeSingle(bwState);  // remove any points that are by themselves
            }
            Integer[] temp = pointSets.get(pointSets.size() - 1).get(pointSets.get(pointSets.size() - 1).size() - 1);
            x = temp[0];
            y = temp[1];
        }
        return pointSets;
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
                double mag0 = Math.sqrt(dx0*dx0 + dy0*dy0);
                double mag1 = Math.sqrt(dx1*dx1 + dy1*dy1);
                double i0 = dx0/mag0;
                double j0 = dy0/mag0;
                double i1 = dx1/mag1;
                double j1 = dy1/mag1;
                if (Math.abs(i1 - i0) < .01 && Math.abs(j1 - j0) < .01) {
                    points.remove(i);
                    i--;
                }
            }
        }
    }

}