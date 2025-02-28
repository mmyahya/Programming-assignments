import java.util.ArrayList;
/**
 * This is class is a representation of a simple road that has a starting point to its end point.
 * This class also stores the previous roads that brought us to this current road.
 */
public class Path {
    private String startPoint;
    private String endPoint;
    private Integer distance = 0;
    private Integer ID;
    private Integer indexFound;
    private ArrayList<Path> road = new ArrayList<>();
    public Path(Integer ID, Integer distance, ArrayList<Path> road, String startPoint, String endPoint, Integer indexFound){
        this.ID = ID;
        if(road != null){ 
            this.road = road;
            if(road.size() > 1){ //to sum distances travelled
                this.distance = road.get(road.size() - 1).getDistance(); //always add the last distance
            }
        }
        if(distance != null)
            this.distance += distance; //always add current distance of the current path
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.indexFound = indexFound;
    }

    /**
     * A getter for the ID variable
     * @return the ID of a path
     */
    public int getID() {
        return ID;
    }
    /**
     * A getter for the distance variable
     * @return the distance travelled in a path
     */
    public Integer getDistance() {
        return distance;
    }

    /**
     * A getter for the road variable
     * @return all roads travelled in an array list
     */
    public ArrayList<Path> getRoad() {
        return road;
    }

    /**
     * A getter for the start point of a path
     * @return the starting point in a path
     */
    public String getStartPoint() {
        return startPoint;
    }

    /**
     * A getter for the end point of a path
     * @return the end point in a path
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * A getter for the indexFound variable
     * @return the position of the end point of the current path
     */
    public Integer getIndexFound() {
        return indexFound;
    }

    /**
     * A setter for the indexFound variable
     * @param indexFound the position of the end point of the current path
     */
    public void setIndexFound(Integer indexFound) {
        this.indexFound = indexFound;
    }
}