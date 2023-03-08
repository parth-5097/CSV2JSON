import java.io.*;
import java.util.Scanner;

public class JSON {

    public static class JSONWriter {

        /**
         * Provides a utility method to write a JSONArray to a file.
         * @param jsonArray
         * @param f
         * @return
         * @throws IOException
         */
        public boolean writeJSONArrayToFile(JSONArray jsonArray, File f) throws IOException {

            PrintWriter pw;
            try {
                FileWriter file = new FileWriter(f);
                pw = new PrintWriter(file);

                pw.println(jsonArray.toString());

                pw.close();
            } catch (FileNotFoundException e) {
                System.out.println("jSON file " + f + " could not be created: error occured");
            }
            return true;
        }
    }

    /**
     * Represents a JSONArray object that can store an arbitrary number of values.
     */
    public static class JSONArray {
        private Object[] values;
        private int size;

        public JSONArray() {
            this.values = new Object[10];
            this.size = 0;
        }

        public void expand() {
            Object[] newValues = new Object[this.values.length * 2];
            System.arraycopy(this.values, 0, newValues, 0, this.values.length);
            this.values = newValues;
        }

        /**
         * Adds a value to the end of the JSONArray.
         * @param value
         */
        public void add(Object value) {
            if (this.size == this.values.length) {
                expand();
            }
            this.values[this.size] = value;
            this.size++;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            for (int i = 0; i < this.size; i++) {
                Object value = this.values[i];
                if (value instanceof String) {
                    sb.append("\"").append(value).append("\"");
                } else {
                    sb.append(value);
                }

                if (i != this.size - 1) {
                    sb.append(",");
                }
            }

            sb.append("]");

            return sb.toString();
        }
    }

    /**
     * A class representing a JSON object with key-value pairs.
     */
    public static class JSONObject {
        private String[] keys;
        private Object[] values;
        private int size;

        public JSONObject() {
            this.keys = new String[10];
            this.values = new Object[10];
            this.size = 0;
        }


        /**
         * Expands the capacity of the JSONObject by doubling the size of its key-value arrays.
         */
        public void expand(){
            String[] newKeys = new String[this.keys.length * 2];
            Object[] newValues = new Object[this.values.length * 2];
            for (int i = 0; i < this.keys.length; i++) {
                newKeys[i] = this.keys[i];
                newValues[i] = this.values[i];
            }
            this.keys = newKeys;
            this.values = newValues;
        }

        /**
         * Adds a new key-value pair to the JSONObject.
         * @param key
         * @param value
         */
        public void put(String key, Object value) {
            if (this.size == this.keys.length) {
                expand();
            }
            this.keys[this.size] = key;
            this.values[this.size] = value;
            this.size++;
        }

        @Override
        public String toString() {
            String result = "{";

            for (int i = 0; i < this.size; i++) {
                String key = this.keys[i];
                Object value = this.values[i];
                result += "\"" + key + "\":";
                if (value instanceof String) {
                    result += "\"" + value + "\"";
                } else {
                    result += value;
                }

                if (i != this.size - 1) {
                    result += ",";
                }
            }

            result += "}";

            return result;
        }
    }

    /**
     * Displays the contents of a JSON file with the given file name.
     * @param sc
     * @param outputFolder
     */
    public static void displayJSON(Scanner sc, String outputFolder) {
        String outputFileName = sc.nextLine();

        File outputFile = new File(outputFolder + outputFileName);
        int tries = 1; // Number of tries remaining

        while (!outputFile.exists()) {
            if (tries == 0) {
                System.out.println("Invalid file name. Exiting program.");
                return;
            }
            System.out.println("File not found. Please enter a valid file name:");
            outputFileName = sc.nextLine();
            outputFile = new File(outputFolder + outputFileName);
            tries--;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(outputFile));
            reader.lines().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            System.out.println("JSON file is not exist.");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
