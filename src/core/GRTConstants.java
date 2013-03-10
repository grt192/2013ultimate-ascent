package core;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.microedition.io.FileConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import logger.GRTLogger;

/**
 * Keeps track of constants from a file. Constants are stored as a lookup table,
 * with a string id corresponding to a numeric constant Constants file should be
 * formatted as a list, with a single id/constant pair per line. Each line
 * should be formatted as [String ID],[double constant]
 * '#' indicates commented lines.
 *
 * @author Calvin
 */
public class GRTConstants {

    private static final String FILE_LOC = "file:///constants.txt";
    private static final char DELIMITER = ',';
    private static final Hashtable table = new Hashtable();
   

    static {
        updateConstants();
        GRTLogger.logSuccess("Constants loaded");
    }

    private GRTConstants() {
    }

    /**
     * Reloads constants from the file, and notifies listeners.
     */
    private static void updateConstants() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ((FileConnection) Connector.open(FILE_LOC)).
                    openInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("") || line.startsWith("#"))
                    continue;
                
                int separatorIndex = line.indexOf(DELIMITER);
                String id = line.substring(0, separatorIndex);
                double data =
                        Double.parseDouble(line.substring(separatorIndex + 1));

                table.put(id, new Double(data));
            }

        } catch (IOException e) {
            GRTLogger.logError("Constants file IO Error--probably nonexistent: "
                    + e.getMessage());
            throw new Error("Constants file IO Error--probably nonexistent");
        } catch (NumberFormatException e) {
            GRTLogger.logError("Malformed constants file: "
                    + e.getMessage());
            throw new Error("Malformed constants file");
        } catch (IndexOutOfBoundsException e) {
            GRTLogger.logError("Malformed constants file"
                    + e.getMessage());
            throw new Error("Mailformed constants file");
        }
    }

    /**
     * Get the constants value for a particular id.
     *
     * @param id String ID for a constant
     * @return the constant associated with this id, or Double.NaN if no such
     * key exists
     */
    public static double getValue(String id) {
        if (!table.containsKey(id)) {
            System.out.println("Constants string \"" + id + "\" not found");
        }
        return ((Double) table.get(id)).doubleValue();
    }
}