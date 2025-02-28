import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Map Analyze class finds the fastest route in a map, and creates a barely connected map. 
 * It also analyzes material used for creating both maps and analyzes the fastest routes through both maps.
 * 
 * Terminologies in this code: 
 * map = normal map and not the java map or dictionary
 */
public class MapAnalyzer{
    public static void main(String[] args){
        Locale.setDefault(Locale.US);
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(args[1]); //Creates the output file
            PrintStream printStream = new PrintStream(fileOutputStream, true, "UTF-8");
            System.setOut(printStream); // Redirect the System.out to the output file
            ArrayList<Path> paths = Utilities.readFile(args[0], false, true);
            Utilities.start(paths);
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
    }
}