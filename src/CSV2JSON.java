import java.io.*;
import java.util.*;

class CSVFilelnvalidException extends Exception {
    public CSVFilelnvalidException(String message) {
        super(message);
    }
}

class CSVMissingDataException extends Exception {
    public CSVMissingDataException(String message) {
        super(message);
    }
}

public class CSV2JSON {
    public static String[] outputFiles = null;
    public static int count = 0;
    public static final String splitter = " DEAD5097MAN ";
    public static final String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static String dataFolder = "data/";
    public static String outputFolder = "output/";

    /**
     * Expanding outputFiles array by one element.
     */
    public static void expand() {
        String[] newValues = new String[outputFiles.length + 1];
        System.arraycopy(outputFiles, 0, newValues, 0, outputFiles.length);
        outputFiles = newValues;
    }

    /**
     * Appends the given message to the "validation.log" file.
     *
     * @param message the message to append
     * @throws IOException if there is an error writing to the file
     */
    public static void logData(String message) throws IOException {
        try (FileWriter writer = new FileWriter("validation.log", true)) {

            writer.write(message + "\n\n");
        }
    }

    /**
     * Validates the specified CSV file and converts it to JSON format.
     *
     * @param fileName the name of the CSV file to validate and convert
     * @throws CSVFilelnvalidException if the CSV file is invalid
     */
    public static void validateCSV(String fileName)
            throws CSVFilelnvalidException {

        try (Scanner reader = new Scanner(new FileReader(dataFolder + fileName))) {
            String head = reader.nextLine();
            String[] headers = head.split(",");
            String validRows = null;
            int inValidHeader = 0;

            for (int i = 0; i < headers.length; i++){
                if (headers[i].isEmpty() || headers[i].isBlank()){
                    inValidHeader++;
                    headers[i] = "***";
                }
            }

            if (inValidHeader > 0) {
                throw new CSVFilelnvalidException("File " + fileName + " is invalid: field is missing.\n" +
                        "File is not converted to JSON.\n"+splitter+"File " + fileName + " is invalid.\n" +
                        "Missing field: "+(headers.length - inValidHeader)+" detected, " + inValidHeader +" missing.\n" +
                        String.join(",", headers));
            }

            int lineCount = 0;
            while (reader.hasNext()){
                try {
                    boolean isValid = true;
                    lineCount++;
                    String data = reader.nextLine();
                    String[] rows = data.split(regex, 99);

                    for (int i = 0; i < rows.length; i++) {
                        if (rows[i].isEmpty() || rows[i].isBlank()){
                            isValid = false;
                            rows[i] = "***";
                        }
                    }

                    if (headers.length > rows.length) {
                        isValid = false;
                        rows[rows.length - 1] = "***";
                    } else if (headers.length < rows.length) {
                        throw new CSVMissingDataException("In file " + fileName
                                + " not converted to JSON: Invalid data for line "+ lineCount + splitter
                                + "In file " + fileName + " line " + lineCount + "\nLine "+lineCount+" has invalid data.\n");
                    }

                    if(!isValid) {
                        throw new CSVMissingDataException("In file " + fileName + " line " + lineCount
                                + " not converted to JSON: missing data." + splitter
                                + "In file " + fileName + " line " + lineCount + "\n" + String.join(" ", rows));
                    }

                    if (validRows == null) {
                        validRows = data;
                    } else {
                        validRows += splitter + data;
                    }
                } catch (CSVMissingDataException e) {
                    String[] log = e.getMessage().split(splitter);
                    System.err.println(log[0]+"\n");
                    logData(log[1]);
                }
            }
            JSON.JSONArray arr = new JSON.JSONArray();
            if (validRows != null)
                createJSONFile(validRows.split(splitter), headers, arr, fileName.split("\\.")[0] + ".json");

        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName + " for reading\n"+
                    "Please check if file exists! Program will terminate after closing all open files.");
        } catch (IOException e) {
            System.out.println("Error while creating JSON file.");
        }
    }

    /**
     * This method creates a JSON file from a CSV file, using the given headers and rows.
     * It takes in the rows and headers as String arrays, a JSONArray object to store the data,
     * an output file name to write the JSON data to, and throws an IOException in case of errors.
     *
     * @param rows
     * @param headers
     * @param arr
     * @param outputFile
     * @throws IOException
     */
    public static void createJSONFile(String[] rows, String[] headers, JSON.JSONArray arr, String outputFile)
            throws IOException {

        for (String reader: rows
             ) {
            String[] data = reader.split(regex, 99);
            JSON.JSONObject obj = new JSON.JSONObject();
            int count = 0;

            for (String s: data
                 ) {
                obj.put(headers[count], s);
                count++;
            }
            arr.add(obj);
        }

        JSON.JSONWriter writer = new JSON.JSONWriter();
        File file = new File(outputFolder + outputFile);

        if (file.exists()) {
            file.delete();
        }
        boolean done = writer.writeJSONArrayToFile(arr, file);
        if (done){
            if (count == outputFiles.length) {
                expand();
            }
            outputFiles[count] = outputFile;
            count++;
        }
    }

    /**
     * This method processes an array of file names by validating each one and handling exceptions.
     * It takes in an array of file names as Strings.
     *
     * @param fileNames
     * @throws IOException
     */
    public static void processFilesForValidation(String[] fileNames) throws IOException {
        for (String s: fileNames
        ) {
            try {
                validateCSV(s);
            } catch (CSVFilelnvalidException e) {
                // if the CSV file is invalid, log the error message
                String[] log = e.getMessage().split(splitter);
                System.err.println(log[0]);
                logData(log[1]);
            }
        }
    }

    /**
     * This is the main method of the program, which prompts the user for input and handles the processing of CSV files.
     * It takes in an array of command-line arguments as Strings.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of file name, you want to enter: ");
        String[] input = new String[sc.nextInt()];
        outputFiles = new String[1];

        System.out.println("Enter name of files: ");
        sc.nextLine();
        for (int i = 0; i < input.length; i++) {
            input[i] = sc.nextLine();
        }

        processFilesForValidation(input);

        if (count > 0){
            System.out.println("This are the list of generated JSON files ...\n");
            for (String s: outputFiles
                 ) {
                System.out.println(s);
            }

            System.out.println("Please enter the name of the output file to display:");
            JSON.displayJSON(sc, outputFolder);
        } else {
            System.out.println("No JSON files generated for given csv files");
        }
    }
}
