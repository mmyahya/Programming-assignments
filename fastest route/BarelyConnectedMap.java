import java.util.ArrayList;

/**
 * This class creates a barely connected map as described in the input file
 */
public class BarelyConnectedMap {
    private ArrayList<Path> listStore = new ArrayList<>();
    private ArrayList<String> listOfPoints = new ArrayList<>();
    private ArrayList<Path> newMap = new ArrayList<>();
    private String initialPoint = new String();
    private String previousInitial = new String();
    public BarelyConnectedMap(ArrayList<Path> paths){
        //1- Stores the points in the map
        for(Path path : paths){
            if(!listOfPoints.contains(path.getStartPoint())){
                listOfPoints.add(path.getStartPoint());
            }
            if(!listOfPoints.contains(path.getEndPoint())){
                listOfPoints.add(path.getEndPoint());
            }
        }
        
        //2- Sorts them alphabetically and take the initial point as the first point
        Utilities.sortAlphabetically(listOfPoints);
        initialPoint = listOfPoints.get(0);
        previousInitial = initialPoint;
        createBarelyConnectedMap(paths);
    }

    /**
     * This method creates the barely connected map with the suggested algorithm.
     * @param paths is the original map taken of the suggested inputs.
     */
    private void createBarelyConnectedMap(ArrayList<Path> paths){
        //3- selects connected points
        listStore.addAll(Utilities.getConnectedPoints(new ArrayList<>(paths), initialPoint, previousInitial));

        //4- sorts using distance and ID if needed
        Utilities.sortLengthID(listStore, true);

        //5- Adds them to the new map if meeting the right conditions. Otherwise current path will be removed and next element will be checked.
        while(listStore.size() > 0 && loopThroughMap(listStore.get(0).getStartPoint()) && loopThroughMap(listStore.get(0).getEndPoint())){
            listStore.remove(0);
        }
        if(listStore.size() > 0){
            Path confirmedPath =  listStore.get(0);
            newMap.add(confirmedPath);
            previousInitial = initialPoint;
            if(confirmedPath.getIndexFound() == 0){
                initialPoint = confirmedPath.getEndPoint();
            }else{
                initialPoint = confirmedPath.getStartPoint();
            }
            listStore.remove(0);
        }
        if(listStore.size() == 0){ //not linked to the previous condition because it checks after removing an element from the list.
            return;
        }
        createBarelyConnectedMap(paths);
    }

    /**
     * To check if a point is already appended in the new map.
     * @param point is the point to be checked
     * @return true if the point is appended and false otherwise
     */
    private boolean loopThroughMap(String point){
        for(Path road : newMap){
            if(road.getStartPoint().equalsIgnoreCase(point) || road.getEndPoint().equalsIgnoreCase(point)){
                return true;
            }
        }
        return false;
    }

    /**
     * a getter for the newMap variable
     * @return the barely connected map
     */
    public ArrayList<Path> getNewMap() {
        return newMap;
    }

    /**
     * Changes the barely connected mpa made by this class to a similar map like those in the input files by adding a starting point and the destination goal in the first line
     * @param goalPath is the path that stores the starting point and the destination goal
     * @return
     */
    public ArrayList<Path> editNewMap(Path goalPath) {
        newMap.add(0, goalPath);
        return newMap;
    }

    /**
     * Displays the barely connected map as shown in the output files
     */
    public void display(){
        System.out.println("Roads of Barely Connected Map is:");
        Utilities.sortLengthID(getNewMap(), true);
        for(Path list : getNewMap()){
            System.out.println(list.getStartPoint() + "\t" + list.getEndPoint() + "\t" + list.getDistance() + "\t" + list.getID());
        }
    }
}
