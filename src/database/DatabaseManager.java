package database;

import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import datastructures.AVLTree;
import datastructures.MinHeap;

public class DatabaseManager {
    private static final String USERS_FILE = "users.txt";
    private static final String RADIOS_FILE = "radios.txt";
    private static final String SALES_FILE = "sales.txt";

    // Optimized data structures
    private static AVLTree radioTree = new AVLTree();
    private static MinHeap restockHeap = new MinHeap();
    private static Map<String, User> userMap = new HashMap<>();
    private static Map<Integer, Sale> saleMap = new HashMap<>();
    private static List<Sale> saleList = new ArrayList<>();
    private static boolean isLoaded = false;

    // Load all data on first access
    private static void ensureLoaded() {
        if (!isLoaded) {
            loadAllData();
            isLoaded = true;
        }
    }

    private static void loadAllData() {
        // Load radios into AVL Tree
        List<String> radioLines = FileHandler.readFile(RADIOS_FILE);
        for (String line : radioLines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 9) {
                Radio radio = new Radio(
                        Integer.parseInt(parts[0]), parts[1], parts[2],
                        RadioType.valueOf(parts[3]), Double.parseDouble(parts[4]),
                        Integer.parseInt(parts[5]), parts[6], Integer.parseInt(parts[7]), parts[8]);
                radioTree.insert(radio);
                // Add low stock items to heap
                if (radio.getQuantity() <= 5) {
                    restockHeap.insert(radio);
                }
            }
        }

        // Load users into HashMap
        List<String> userLines = FileHandler.readFile(USERS_FILE);
        for (String line : userLines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                User user = new User(
                        Integer.parseInt(parts[0]), parts[1], parts[2],
                        parts[3], parts[4], parts[5]);
                user.setActive(Boolean.parseBoolean(parts[6]));
                userMap.put(user.getUsername(), user);
            }
        }

        // Load sales into List and Map
        List<String> saleLines = FileHandler.readFile(SALES_FILE);
        for (String line : saleLines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 8) {
                Sale sale = new Sale(
                        Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3]),
                        parts[4], parts[5], Integer.parseInt(parts[6]));
                sale.setSaleDate(LocalDateTime.parse(parts[7], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                saleList.add(sale);
                saleMap.put(sale.getId(), sale);
            }
        }
    }

    // Reload all data
    public static void reload() {
        radioTree.clear();
        restockHeap.clear();
        userMap.clear();
        saleMap.clear();
        saleList.clear();
        isLoaded = false;
        loadAllData();
    }

    // USER OPERATIONS (Hash Table)

    public static User authenticateUser(String username, String password) {
        ensureLoaded();
        User user = userMap.get(username); // O(1) hash lookup
        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            return user;
        }
        return null;
    }

    public static List<User> getAllUsers() {
        ensureLoaded();
        return new ArrayList<>(userMap.values());
    }

    public static User getUserById(int id) {
        ensureLoaded();
        for (User u : userMap.values()) {
            if (u.getId() == id)
                return u;
        }
        return null;
    }

    public static void saveUser(User user) {
        ensureLoaded();
        userMap.put(user.getUsername(), user); // O(1)
        saveAllUsers();
    }

    public static void updateUser(User updatedUser) {
        ensureLoaded();
        // Remove old username entry if username changed
        for (User u : userMap.values()) {
            if (u.getId() == updatedUser.getId()) {
                if (!u.getUsername().equals(updatedUser.getUsername())) {
                    userMap.remove(u.getUsername());
                }
                break;
            }
        }
        userMap.put(updatedUser.getUsername(), updatedUser); // O(1)
        saveAllUsers();
    }

    private static void saveAllUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : userMap.values()) {
            lines.add(user.toString());
        }
        FileHandler.writeFile(USERS_FILE, lines);
    }

    public static int getNextUserId() {
        ensureLoaded();
        int maxId = 0;
        for (User u : userMap.values()) {
            if (u.getId() > maxId)
                maxId = u.getId();
        }
        return maxId + 1;
    }

    // RADIO OPERATIONS (AVL Tree + MinHeap)

    public static List<Radio> getAllRadios() {
        ensureLoaded();
        return radioTree.getAll(); // O(n) in-order traversal
    }

    public static Radio getRadioById(int id) {
        ensureLoaded();
        return radioTree.search(id); // O(log n)
    }

    public static List<Radio> searchRadios(String query) {
        ensureLoaded();
        if (query == null || query.trim().isEmpty()) {
            return getAllRadios();
        }
        return radioTree.searchAllByName(query); // O(n) with name matching
    }

    public static List<Radio> getRadiosByType(RadioType type) {
        ensureLoaded();
        return radioTree.getByType(type);
    }

    public static void saveRadio(Radio radio) {
        ensureLoaded();
        radioTree.insert(radio); // O(log n)
        // Update heap if low stock
        if (radio.getQuantity() <= 5) {
            restockHeap.updatePriority(radio); // O(log n)
        } else {
            restockHeap.remove(radio.getId());
        }
        saveAllRadios();
    }

    public static void updateRadio(Radio updatedRadio) {
        ensureLoaded();
        radioTree.update(updatedRadio); // O(log n)
        // Update heap
        if (updatedRadio.getQuantity() <= 5) {
            restockHeap.updatePriority(updatedRadio); // O(log n)
        } else {
            restockHeap.remove(updatedRadio.getId());
        }
        saveAllRadios();
    }

    public static void deleteRadio(int id) {
        ensureLoaded();
        radioTree.delete(id); // O(log n)
        restockHeap.remove(id); // O(log n)
        saveAllRadios();
    }

    private static void saveAllRadios() {
        List<String> lines = new ArrayList<>();
        for (Radio radio : radioTree.getAll()) {
            lines.add(radio.toString());
        }
        FileHandler.writeFile(RADIOS_FILE, lines);
    }

    public static int getNextRadioId() {
        ensureLoaded();
        return radioTree.getNextId();
    }

    // RESTOCK PRIORITY OPERATIONS (Min-Heap)

    public static Radio getMostCriticalRestock() {
        ensureLoaded();
        return restockHeap.peek(); // O(1)
    }

    public static Radio extractMostCriticalRestock() {
        ensureLoaded();
        Radio r = restockHeap.extractMin(); // O(log n)
        return r;
    }

    public static List<Radio> getLowStockRadios(int threshold) {
        ensureLoaded();
        return radioTree.getLowStock(threshold); // O(k + log n)
    }

    public static List<Radio> getRestockPriorityList() {
        ensureLoaded();
        List<Radio> heapItems = restockHeap.getAll();
        // Sort by quantity
        heapItems.sort((a, b) -> Integer.compare(a.getQuantity(), b.getQuantity()));
        return heapItems;
    }

    // SALE OPERATIONS

    public static List<Sale> getAllSales() {
        ensureLoaded();
        return new ArrayList<>(saleList);
    }

    public static Sale getSaleById(int id) {
        ensureLoaded();
        return saleMap.get(id); // O(1)
    }

    public static void saveSale(Sale sale) {
        ensureLoaded();
        saleList.add(sale);
        saleMap.put(sale.getId(), sale);
        FileHandler.appendToFile(SALES_FILE, sale.toString());
    }

    public static int getNextSaleId() {
        ensureLoaded();
        int maxId = 0;
        for (Sale s : saleList) {
            if (s.getId() > maxId)
                maxId = s.getId();
        }
        return maxId + 1;
    }

    // INITIALIZATION

    public static void initializeDefaultData() {
        // Check if users exist
        if (getAllUsers().isEmpty()) {
            User admin = new User(1, "admin", "admin123", "System Administrator",
                    "admin@radiowave.com", "ADMIN");
            saveUser(admin);

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