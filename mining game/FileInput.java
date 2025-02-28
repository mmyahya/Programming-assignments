import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * File Input Class for reading file inputs
 * Explanation One Location: readFile method
 */
public class FileInput {
    /**
     * Reads the file at the given path and returns contents of it in a string array. 
     * EXPLANATION: Array is the best choice because the goal here is to distinguish each line, just simple iterations over the input file's content lines, no more control is needed.
     *
     * @param path              Path to the file that is going to be read.
     * @param discardEmptyLines If true, discards empty lines with respect to trim; else, it takes all the lines from the file.
     * @param trim              Trim status; if true, trims (strip in Python) each line; else, it leaves each line as-is.
     * @return Contents of the file as a string array, returns null if there is not such a file or this program does not have sufficient permissions to read that file.
     */
    public String[] readFile(String path, boolean discardEmptyLines, boolean trim){
        try {
            List<String> lines = Files.readAllLines(Paths.get(path)); //Gets the content of file to the list.
            List<String> linesExceptFirst = lines.subList(1, lines.size());
            if (discardEmptyLines) { //Removes the lines that are empty with respect to trim.
                linesExceptFirst.removeIf(line -> line.trim().equals(""));
            }
            if (trim) { //Trims each line.
                linesExceptFirst.replaceAll(String::trim);
            }
            return linesExceptFirst.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}