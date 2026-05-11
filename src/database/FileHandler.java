package database;

import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data/";

    static {
        // Create data directory if it doesn't exist
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + " - " + e.getMessage());
        }
        return lines;
    }

    public static void writeFile(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + filename + " - " + e.getMessage());
        }
    }

    public static void appendToFile(String filename, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + filename, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filename + " - " + e.getMessage());
        }
    }

    public static int getNextId(String filename) {
        List<String> lines = readFile(filename);
        int maxId = 0;
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > 0) {
                try {
                    int id = Integer.parseInt(parts[0]);
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        return maxId + 1;
    }
}