package database;

import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseManager {
    private static final String USERS_FILE = "users.txt";
    private static final String RADIOS_FILE = "radios.txt";
    private static final String SALES_FILE = "sales.txt";

    // User operations
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileHandler.readFile(USERS_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                User user = new User(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5]);
                user.setActive(Boolean.parseBoolean(parts[6]));
                users.add(user);
            }
        }
        return users;
    }

    public static User authenticateUser(String username, String password) {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username) &&
                        u.getPassword().equals(password) &&
                        u.isActive())
                .findFirst()
                .orElse(null);
    }

    public static void saveUser(User user) {
        FileHandler.appendToFile(USERS_FILE, user.toString());
    }

    public static void updateUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.toString());
        }
        FileHandler.writeFile(USERS_FILE, lines);
    }

    public static void updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updatedUser.getId()) {
                users.set(i, updatedUser);
                break;
            }
        }
        updateUsers(users);
    }

    // Radio operations
    public static List<Radio> getAllRadios() {
        List<Radio> radios = new ArrayList<>();
        List<String> lines = FileHandler.readFile(RADIOS_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 9) {
                Radio radio = new Radio(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        RadioType.valueOf(parts[3]),
                        Double.parseDouble(parts[4]),
                        Integer.parseInt(parts[5]),
                        parts[6],
                        Integer.parseInt(parts[7]),
                        parts[8]);
                radios.add(radio);
            }
        }
        return radios;
    }

    public static void saveRadio(Radio radio) {
        FileHandler.appendToFile(RADIOS_FILE, radio.toString());
    }

    public static void updateRadios(List<Radio> radios) {
        List<String> lines = new ArrayList<>();
        for (Radio radio : radios) {
            lines.add(radio.toString());
        }
        FileHandler.writeFile(RADIOS_FILE, lines);
    }

    public static void updateRadio(Radio updatedRadio) {
        List<Radio> radios = getAllRadios();
        for (int i = 0; i < radios.size(); i++) {
            if (radios.get(i).getId() == updatedRadio.getId()) {
                radios.set(i, updatedRadio);
                break;
            }
        }
        updateRadios(radios);
    }

    public static void deleteRadio(int id) {
        List<Radio> radios = getAllRadios();
        radios.removeIf(r -> r.getId() == id);
        updateRadios(radios);
    }

    // Sale operations
    public static List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        List<String> lines = FileHandler.readFile(SALES_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 8) {
                Sale sale = new Sale(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]),
                        parts[4],
                        parts[5],
                        Integer.parseInt(parts[6]));
                sale.setSaleDate(LocalDateTime.parse(parts[7], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                sales.add(sale);
            }
        }
        return sales;
    }

    public static void saveSale(Sale sale) {
        FileHandler.appendToFile(SALES_FILE, sale.toString());
    }

    public static int getNextUserId() {
        return FileHandler.getNextId(USERS_FILE);
    }

    public static int getNextRadioId() {
        return FileHandler.getNextId(RADIOS_FILE);
    }

    public static int getNextSaleId() {
        return FileHandler.getNextId(SALES_FILE);
    }

    // Initialize default data
    public static void initializeDefaultData() {
        // Check if users file exists
        if (getAllUsers().isEmpty()) {
            // Create default admin user
            User admin = new User(1, "admin", "admin123", "System Administrator",
                    "admin@radiowave.com", "ADMIN");
            saveUser(admin);

            // Create default staff user
            User staff = new User(2, "staff", "staff123", "Staff Member",
                    "staff@radiowave.com", "STAFF");
            saveUser(staff);
        }

        // Add sample radios if none exist
        if (getAllRadios().isEmpty()) {
            saveRadio(new Radio(1, "PR-100", "Sony", RadioType.PORTABLE,
                    49.99, 10, "FM/AM", 5, "Compact portable radio"));
            saveRadio(new Radio(2, "BR-2000", "Kenwood", RadioType.BASE,
                    299.99, 5, "HF/VHF/UHF", 100, "Professional base station"));
            saveRadio(new Radio(3, "PR-200", "Panasonic", RadioType.PORTABLE,
                    79.99, 15, "FM/AM/SW", 7, "Multi-band portable radio"));
            saveRadio(new Radio(4, "BR-3000", "Yaesu", RadioType.BASE,
                    499.99, 3, "HF/50MHz", 200, "Amateur radio base station"));
            saveRadio(new Radio(5, "PR-300", "Motorola", RadioType.PORTABLE,
                    129.99, 8, "UHF", 4, "Two-way portable radio"));
        }
    }
}