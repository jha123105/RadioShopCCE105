package gui;

import database.DatabaseManager;
import models.Radio;
import models.Sale;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private boolean isAdmin;
    private JTextArea reportArea;
    private JComboBox<String> reportTypeCombo;
    private JButton generateButton, exportButton;

    public ReportsPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Dropdown: Report types including inefficient baseline demos
        reportTypeCombo = new JComboBox<>(new String[] {
                "Sales Summary",
                "Inventory Status",
                "Low Stock Alert",
                "Sales by Radio Type",
                "Top Selling Radios",
                "Most Critical Restock"
        });

        // Buttons
        generateButton = new JButton("Generate Report");
        exportButton = new JButton("Export Report");

        styleButton(generateButton, new Color(52, 152, 219));
        styleButton(exportButton, new Color(46, 204, 113));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void setupLayout() {
        // Panel: Top controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(new JLabel("Report Type:"));
        controlPanel.add(reportTypeCombo);
        controlPanel.add(generateButton);
        controlPanel.add(exportButton);

        // Panel: Scrollable report area
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportReport());
    }

    private void generateReport() {
        String selectedReport = (String) reportTypeCombo.getSelectedItem();
        StringBuilder report = new StringBuilder();

        report.append("=".repeat(80)).append("\n");
        report.append(String.format("%50s\n", "RADIOWAVE ELECTRONICS"));
        report.append(String.format("%55s\n", selectedReport.toUpperCase() + " REPORT"));
        report.append(String.format("%48s\n", "Date: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE)));
        report.append("=".repeat(80)).append("\n\n");

        switch (selectedReport) {
            case "Sales Summary":
                generateSalesSummary(report);
                break;
            case "Inventory Status":
                generateInventoryStatus(report);
                break;
            case "Low Stock Alert":
                generateLowStockAlert(report);
                break;
            case "Sales by Radio Type":
                generateSalesByType(report);
                break;
            case "Top Selling Radios":
                generateTopSelling(report);
                break;
            case "Most Critical Restock":
                generateMostCriticalRestock(report);
                break;
        }

        reportArea.setText(report.toString());
    }

    // INEFFICIENT BASELINE METHODS

    // Bubble Sort - O(n²) quadratic time.
    // Used to sort sales by total price in descending order.
    // Will be replaced by Merge Sort O(n log n) in optimized system.

    private void bubbleSortSalesByPrice(List<Sale> sales) {
        int n = sales.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sales.get(j).getTotalPrice() < sales.get(j + 1).getTotalPrice()) {
                    Sale temp = sales.get(j);
                    sales.set(j, sales.get(j + 1));
                    sales.set(j + 1, temp);
                }
            }
        }
    }

    // Linear scan to find radio with lowest stock - O(n).
    // Iterates through entire inventory list.
    // Will be replaced by Min-Heap O(1) peek in optimized system.

    private Radio findLowestStockRadio() {
        List<Radio> radios = DatabaseManager.getAllRadios();
        Radio lowest = null;
        for (Radio r : radios) {
            if (lowest == null || r.getQuantity() < lowest.getQuantity()) {
                lowest = r;
            }
        }
        return lowest;
    }

    // Linear scan to collect radios below threshold - O(n).
    // Will be replaced by AVL Tree in-order range query O(k + log n).

    private List<Radio> getLowStockRadios(int threshold) {
        List<Radio> radios = DatabaseManager.getAllRadios();
        List<Radio> result = new java.util.ArrayList<>();
        for (Radio r : radios) {
            if (r.getQuantity() <= threshold) {
                result.add(r);
            }
        }
        return result;
    }

    // REPORT GENERATION

    private void generateSalesSummary(StringBuilder report) {
        List<Sale> sales = DatabaseManager.getAllSales();
        List<Radio> radios = DatabaseManager.getAllRadios();

        double totalRevenue = sales.stream().mapToDouble(Sale::getTotalPrice).sum();
        int totalUnitsSold = sales.stream().mapToInt(Sale::getQuantity).sum();
        long totalTransactions = sales.size();

        report.append("SALES SUMMARY\n");
        report.append("-".repeat(80)).append("\n\n");
        report.append(String.format("Total Transactions: %d\n", totalTransactions));
        report.append(String.format("Total Units Sold:   %d\n", totalUnitsSold));
        report.append(String.format("Total Revenue:       $%.2f\n\n", totalRevenue));

        LocalDate today = LocalDate.now();
        double todayRevenue = sales.stream()
                .filter(s -> s.getSaleDate().toLocalDate().equals(today))
                .mapToDouble(Sale::getTotalPrice)
                .sum();

        report.append(String.format("Today's Revenue:     $%.2f\n", todayRevenue));

        double inventoryValue = radios.stream()
                .mapToDouble(r -> r.getPrice() * r.getQuantity())
                .sum();

        report.append(String.format("Current Inventory Value: $%.2f\n", inventoryValue));
    }

    private void generateInventoryStatus(StringBuilder report) {
        List<Radio> radios = DatabaseManager.getAllRadios();

        report.append("CURRENT INVENTORY STATUS\n");
        report.append("-".repeat(80)).append("\n\n");
        report.append(String.format("%-5s %-20s %-15s %-10s %-10s %-10s\n",
                "ID", "Model", "Brand", "Type", "Price", "Qty"));
        report.append("-".repeat(80)).append("\n");

        for (Radio radio : radios) {
            report.append(String.format("%-5d %-20s %-15s %-10s $%-9.2f %-10d\n",
                    radio.getId(), radio.getModel(), radio.getBrand(),
                    radio.getType().getDisplayName(), radio.getPrice(), radio.getQuantity()));
        }

        long totalItems = radios.stream().mapToInt(Radio::getQuantity).sum();
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("Total Items in Stock: %d\n", totalItems));
    }

    private void generateLowStockAlert(StringBuilder report) {
        int lowStockThreshold = 5;
        List<Radio> lowStockRadios = getLowStockRadios(lowStockThreshold);

        report.append("LOW STOCK ALERT (Threshold: ").append(lowStockThreshold).append(" units)\n");
        report.append("-".repeat(80)).append("\n\n");
        report.append(String.format("%-5s %-20s %-15s %-10s %-10s\n",
                "ID", "Model", "Brand", "Type", "Current Qty"));
        report.append("-".repeat(80)).append("\n");

        if (lowStockRadios.isEmpty()) {
            report.append("\nNo items are low in stock.\n");
        } else {
            for (Radio radio : lowStockRadios) {
                report.append(String.format("%-5d %-20s %-15s %-10s %-10d\n",
                        radio.getId(), radio.getModel(), radio.getBrand(),
                        radio.getType().getDisplayName(), radio.getQuantity()));
            }
        }
    }

    private void generateSalesByType(StringBuilder report) {
        List<Sale> sales = DatabaseManager.getAllSales();
        List<Radio> radios = DatabaseManager.getAllRadios();

        report.append("SALES BY RADIO TYPE\n");
        report.append("-".repeat(80)).append("\n\n");

        Map<String, Double> salesByType = sales.stream()
                .collect(Collectors.groupingBy(s -> {
                    Radio radio = radios.stream()
                            .filter(r -> r.getId() == s.getRadioId()).findFirst().orElse(null);
                    return radio != null ? radio.getType().getDisplayName() : "Unknown";
                }, Collectors.summingDouble(Sale::getTotalPrice)));

        report.append(String.format("%-20s %15s %15s\n", "Radio Type", "Revenue", "Percentage"));
        report.append("-".repeat(50)).append("\n");

        double totalRevenue = sales.stream().mapToDouble(Sale::getTotalPrice).sum();
        salesByType.forEach((type, revenue) -> {
            double percentage = (revenue / totalRevenue) * 100;
            report.append(String.format("%-20s $%14.2f %14.1f%%\n", type, revenue, percentage));
        });

        report.append("-".repeat(50)).append("\n");
        report.append(String.format("%-20s $%14.2f\n", "TOTAL", totalRevenue));
    }

    private void generateTopSelling(StringBuilder report) {
        List<Sale> sales = DatabaseManager.getAllSales();
        List<Radio> radios = DatabaseManager.getAllRadios();

        report.append("TOP SELLING RADIOS\n");
        report.append("-".repeat(80)).append("\n\n");

        // Use inefficient O(n²) Bubble Sort before processing
        List<Sale> sortedSales = new java.util.ArrayList<>(sales);
        bubbleSortSalesByPrice(sortedSales);

        Map<Integer, Integer> salesByRadio = sortedSales.stream()
                .collect(Collectors.groupingBy(Sale::getRadioId, Collectors.summingInt(Sale::getQuantity)));

        report.append(String.format("%-5s %-25s %-15s %-10s\n", "Rank", "Radio", "Units Sold", "Revenue"));
        report.append("-".repeat(60)).append("\n");

        salesByRadio.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    Radio radio = radios.stream()
                            .filter(r -> r.getId() == entry.getKey()).findFirst().orElse(null);
                    if (radio != null) {
                        double revenue = entry.getValue() * radio.getPrice();
                        report.append(String.format("%-5d %-25s %-15d $%.2f\n",
                                entry.getKey(), radio.getBrand() + " " + radio.getModel(),
                                entry.getValue(), revenue));
                    }
                });
    }

    // report: finds most critical restock item using O(n) linear scan
    private void generateMostCriticalRestock(StringBuilder report) {
        report.append("MOST CRITICAL RESTOCK \n");
        report.append("-".repeat(80)).append("\n\n");

        Radio mostCritical = findLowestStockRadio();

        if (mostCritical != null) {
            report.append("ITEM REQUIRING IMMEDIATE RESTOCK:\n\n");
            report.append(String.format("  ID:          %d\n", mostCritical.getId()));
            report.append(String.format("  Model:       %s\n", mostCritical.getModel()));
            report.append(String.format("  Brand:       %s\n", mostCritical.getBrand()));
            report.append(String.format("  Current Qty: %d\n", mostCritical.getQuantity()));
            report.append(String.format("  Type:        %s\n", mostCritical.getType().getDisplayName()));
            report.append(String.format("  Price:       $%.2f\n", mostCritical.getPrice()));
            report.append("\n").append("-".repeat(80)).append("\n");
            // Note: Uses full inventory scan O(n).
            // Optimized version will use Min-Heap for O(1) access.
        } else {
            report.append("No items in inventory.\n");
        }
    }

    private void exportReport() {
        if (reportArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("report_" +
                LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile());
                writer.print(reportArea.getText());
                writer.close();
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
            }
        }
    }
}