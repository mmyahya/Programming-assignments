import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class holds all methods that are shared between the BarelyConnectedMap Class and FastestRoute Class
 */
public class Utilities {

    /**
     * Reads the file at the given path and returns contents of it in a string array.
     *
     * @param path              Path to the file that is going to be read.
     * @param discardEmptyLines If true, discards empty lines with respect to trim; else, it takes all the lines from the file.
     * @param trim              Trim status; if true, trims each line; else, it leaves each line as-is.
     * @return Contents of the file as a string array, returns null if there is not such a file or this program does not have sufficient permissions to read that file.
     */
    public static ArrayList<Path> readFile(String path, boolean discardEmptyLines, boolean trim) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path)); //Gets the content of file to the list.
            ArrayList<Path> returned = new ArrayList<>();
            if (discardEmptyLines) { //Removes the lines that are empty with respect to trim.
                lines.removeIf(line -> line.trim().equals(""));
            }
            if (trim) { //Trims each line.
                lines.replaceAll(String::trim);
            }
            String[] originalReturned = lines.toArray(new String[0]);
            returned.add(new Path(null, null, null, lines.get(0).split("\t")[0], lines.get(0).split("\t")[1], null));
            for(int line = 1; line <  originalReturned.length; line++){
                String[] split = originalReturned[line].split("\t");
                returned.add(new Path(Integer.parseInt(split[3]), Integer.parseInt(split[2]), null, split[0], split[1], null));
            }
            return returned;
        } catch (IOException e) { //Returns null if there is no such a file.
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This methods looks for the connected paths of a point for both the barelyConnectedMap and the fastest route.
     * @param paths is the map(a copy of the original argument). It stores all Path elements taken from the map.
     * @param currentInitial is the point that we are using to look for its connected paths
     * @param previousInitial is the previous point that to make sure not to add the previous route
     * @return all the paths connected to the currentInitial
     */
    public static ArrayList<Path> getConnectedPoints(ArrayList<Path> paths, String currentInitial, String previousInitial){
        ArrayList<Path> listToStoreConnectedPoints = new ArrayList<>();
        for(int i = 1; i < paths.size(); i++){
            Path split = paths.get(i);
            if(currentInitial.equalsIgnoreCase(split.getStartPoint()) && !split.getEndPoint().equalsIgnoreCase(previousInitial)){
                split.setIndexFound(0); //the position of the point found: 0 for left and 1 for right
                listToStoreConnectedPoints.add(split);
            } 
            else if(currentInitial.equalsIgnoreCase(split.getEndPoint()) && !split.getStartPoint().equalsIgnoreCase(previousInitial)){
                split.setIndexFound(1);
                listToStoreConnectedPoints.add(split);
            }
        }
        return listToStoreConnectedPoints;
    }

    /**
     * Sorts the a list of paths based on their distance and ID if necessary
     * @param listToSort is the list of the paths
     * @param withID is false when sorting only by distance and true if ID is also desired if necessary
     */
    public static void sortLengthID(ArrayList<Path> listToSort, boolean withID){
        Collections.sort(listToSort, new Comparator<Path>(){
            @Override 
            public int compare(Path pathOne, Path pathTwo){
                int difference = Integer.compare(pathOne.getDistance(), pathTwo.getDistance());
                if(withID){
                    if(difference != 0){
                        return difference;
                    }else{
                        return Integer.compare(pathOne.getID(), pathTwo.getID());
                    }
                }else{
                    return difference;
                }
            }
        });
    }
    
    /**
     * Sorts a list of strings based on the alphabet
     * @param listToSort is the list of strings
     */
    public static void sortAlphabetically(ArrayList<String> listToSort){
        Collections.sort(listToSort, new Comparator<String>(){
            @Override
            public int compare(String pathOne, String pathTwo){
                return pathOne.compareTo(pathTwo);
            }
        });
    }

    /**
     * Sums the distances of paths in a map.
     * Precondition: the map needs to have a starting point and a destination followed by paths in each line with integer distances
     * Postconidition: an integer holding the summed distances
     * @param map is the map
     * @return the sum of distances in a map
     */
    public static int getMapSum(ArrayList<Path> map){
        int returned = 0;
        for(int path = 1; path < map.size(); path++){//start from one because each map
            returned += map.get(path).getDistance();
        }
        return returned;
    }

    /**
     * The "main method" of the whole code. Initiates the main classes and prints everything.
     * @param paths is the map taken from the input file as an ArrayList of Path.
     */
    public static void start(ArrayList<Path> paths){
        FastestRoute fastestRoute = new FastestRoute(paths);
        System.out.println("Fastest Route from "+ paths.get(0).getStartPoint() +" to "+ paths.get(0).getEndPoint() + " ("+ fastestRoute.getDictionary().get(paths.get(0).getEndPoint()).getDistance() +" KM):");
        fastestRoute.display(paths);

        BarelyConnectedMap barelyConnectedMap = new BarelyConnectedMap(paths);
        barelyConnectedMap.display();

        FastestRoute fastestRouteBarelyConnectedMap = new FastestRoute(barelyConnectedMap.editNewMap(paths.get(0)));
        System.out.println("Fastest Route from "+ paths.get(0).getStartPoint() +" to "+ paths.get(0).getEndPoint() + " on Barely Connected Map ("+ fastestRouteBarelyConnectedMap.getDictionary().get(paths.get(0).getEndPoint()).getDistance() +" KM):");
        fastestRouteBarelyConnectedMap.display(barelyConnectedMap.getNewMap());
        
        System.out.println("Analysis:");
        System.out.println("Ratio of Construction Material Usage Between Barely Connected and Original Map: " + String.format("%.2f",((double)Utilities.getMapSum(barelyConnectedMap.getNewMap()) / Utilities.getMapSum(paths))));
        System.out.println("Ratio of Fastest Route Between Barely Connected and Original Map: " + String.format("%.2f",((double)fastestRouteBarelyConnectedMap.getDictionary().get(paths.get(0).getEndPoint()).getDistance() / fastestRoute.getDictionary().get(paths.get(0).getEndPoint()).getDistance())));
    }
}
