import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class's purpose is to find the shortest path from a starting point to the destination goal using the map passed to the constructor.
 */
public class FastestRoute{
    private String currentInitial;
    private String previousInitial;
    private Path thePreviousPath;
    private ArrayList<Path> beforeObjectList = new ArrayList<>();
    private ArrayList<Path> list = new ArrayList<>();
    private LinkedHashMap<String, Path> dictionary = new LinkedHashMap<>(); //holds the confirmed steps. But only the last step matter because it holds all the previous steps that led to it.


    public FastestRoute(ArrayList<Path> paths){
        currentInitial = paths.get(0).getStartPoint();
        previousInitial = currentInitial;
        algo(paths, null);
    }

    /**
     * This methods implements the described algorithm for finding the fastest path.
     * @param paths is the "map". It groups the simple roads that connectes two points - taken from the input - in an array list. Each simple road is of type Path
     * @param previousPath is the previous road of type Path.
     */
    private void algo(ArrayList<Path> paths, Path previousPath){
        //1- look for roads coming from starting point
        beforeObjectList = Utilities.getConnectedPoints(new ArrayList<>(paths), currentInitial, previousInitial);

        //2- Sort those roads by distance and ID if needed.
        Utilities.sortLengthID(beforeObjectList, true);

        // Add previous roads to the potential roads
        ArrayList<Path> previousPathsOfPreviousPath;
        if(previousPath != null){ //it can be null for the very first start
            previousPathsOfPreviousPath = new ArrayList<Path>(previousPath.getRoad());
        }else{
            previousPathsOfPreviousPath = new ArrayList<Path>();
        }
        previousPathsOfPreviousPath.add(previousPath);
        //Add previous roads here of course; 
        for(Path directRoad : beforeObjectList){
            list.add(
                new Path(
                    directRoad.getID(),
                    directRoad.getDistance(), 
                    previousPathsOfPreviousPath, 
                    directRoad.getStartPoint(), 
                    directRoad.getEndPoint(), directRoad.getIndexFound())
            );
        }
        beforeObjectList.clear();

        //4- Sort potential roads in the list by distance
        Utilities.sortLengthID(list, false);
        
        //5- Select the smallest and add it to dictionary.
        while(list.size() > 0){
            Path potentialPath = list.get(0);
            if(//checks the position of the found point to set the next initial point
                (potentialPath.getIndexFound() == 0 && !dictionary.containsKey(potentialPath.getEndPoint())) ||
                (potentialPath.getIndexFound() == 1 && !dictionary.containsKey(potentialPath.getStartPoint()))
                ){
                    previousInitial = currentInitial;
                    currentInitial = potentialPath.getIndexFound() == 0 ? potentialPath.getEndPoint() : potentialPath.getStartPoint();
                    dictionary.put(currentInitial, potentialPath);
                    thePreviousPath = potentialPath;
                    list.remove(0);
                    break;
            }else{
                list.remove(0);
            }
        }
        if(currentInitial.equalsIgnoreCase(paths.get(0).getEndPoint())){
            return;
        }
        algo(paths, thePreviousPath);
    }

    /**
     * A getter for the dictionary variable
     * @return the LinkedHashMap or dictionary that stores all accepted paths. However, of course, only the last step is the complete one.
     */
    public LinkedHashMap<String, Path> getDictionary(){
        return dictionary;
    }
    
    /**
     * Displays the path as desired in the output file.
     * @param paths is the list storing all of the shortest paths to reach the destination goal.
     */
    public void display(ArrayList<Path> paths){
        String goalDestination = paths.get(0).getEndPoint();
        Path lastPath = getDictionary().get(goalDestination);
        ArrayList<Path> roads = lastPath.getRoad();
        String finalToBeAdded = new String();

        for(int recordedPath = 1; recordedPath < roads.size(); recordedPath++){//Start from one because first road is always null
            for(int pathIndex = 1; pathIndex < paths.size(); pathIndex++){
                Path pathInput = paths.get(pathIndex);
                int inputID = pathInput.getID();
                if(inputID == roads.get(recordedPath).getID()){
                System.out.println(pathInput.getStartPoint() + "\t" + pathInput.getEndPoint() + "\t" + pathInput.getDistance() + "\t" + pathInput.getID());
                }
                if(finalToBeAdded.length() == 0 && lastPath.getID() == inputID){
                    finalToBeAdded = pathInput.getStartPoint() + "\t" + pathInput.getEndPoint() + "\t" + pathInput.getDistance() + "\t" + pathInput.getID();
                }
            }
        }
        System.out.println(finalToBeAdded);
    }
}
