package gui;

import database.DatabaseManager;
import models.Radio;
import models.Sale;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalesPanel extends JPanel {
    private User currentUser;
    private boolean isAdmin;
    private JTable salesTable, availableRadiosTable;
    private DefaultTableModel salesTableModel, availableRadiosModel;
    private JTextField customerNameField, customerPhoneField;
    private JButton processSaleButton, refreshButton;
    private JLabel totalLabel;

    public SalesPanel(User user, boolean isAdmin) {
        this.currentUser = user;
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        initComponents();
        setupLayout();
        loadSalesData();
        loadAvailableRadios();
    }

    private void initComponents() {
        // Table: Sales history
        String[] salesColumns = { "Sale ID", "Date", "Customer", "Phone", "Radio Model", "Qty", "Total", "Sold By" };
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
        salesTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Table: Available radios
        String[] radioColumns = { "ID", "Model", "Brand", "Type", "Price", "Available Qty" };
        availableRadiosModel = new DefaultTableModel(radioColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableRadiosTable = new JTable(availableRadiosModel);
        availableRadiosTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Fields
        customerNameField = new JTextField(20);
        customerPhoneField = new JTextField(20);

        // Buttons
        processSaleButton = new JButton("Process Sale");
        refreshButton = new JButton("Refresh");

        // Label
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(46, 204, 113));

        styleButton(processSaleButton, new Color(46, 204, 113));
        styleButton(refreshButton, new Color(149, 165, 166));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void setupLayout() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Customer form
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        customerPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        customerPanel.add(customerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        customerPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        customerPanel.add(customerPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        customerPanel.add(processSaleButton, gbc);

        // Radios panel
        JPanel radiosPanel = new JPanel(new BorderLayout(5, 5));
        radiosPanel.setBackground(Color.WHITE);
        radiosPanel.setBorder(BorderFactory.createTitledBorder("Available Radios"));

        JScrollPane radioScrollPane = new JScrollPane(availableRadiosTable);
        radioScrollPane.setPreferredSize(new Dimension(600, 200));
        radiosPanel.add(radioScrollPane, BorderLayout.CENTER);

        JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        radioButtonPanel.setBackground(Color.WHITE);
        radioButtonPanel.add(totalLabel);
        radioButtonPanel.add(refreshButton);
        radiosPanel.add(radioButtonPanel, BorderLayout.SOUTH);

        topPanel.add(customerPanel, BorderLayout.NORTH);
        topPanel.add(radiosPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Sales History"));
        JScrollPane salesScrollPane = new JScrollPane(salesTable);
        bottomPanel.add(salesScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        add(splitPane, BorderLayout.CENTER);

        // Listeners
        processSaleButton.addActionListener(e -> processSale());
        refreshButton.addActionListener(e -> {
            loadAvailableRadios();
            loadSalesData();
        });
        availableRadiosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                updateTotal();
        });
    }

    private void loadSalesData() {
        salesTableModel.setRowCount(0);
        List<Sale> sales = DatabaseManager.getAllSales();
        List<User> users = DatabaseManager.getAllUsers();
        List<Radio> radios = DatabaseManager.getAllRadios();

        for (Sale sale : sales) {
            Radio radio = radios.stream().filter(r -> r.getId() == sale.getRadioId()).findFirst().orElse(null);
            User seller = users.stream().filter(u -> u.getId() == sale.getSoldBy()).findFirst().orElse(null);
            if (radio != null && seller != null) {
                Object[] row = { sale.getId(), sale.getFormattedDate(), sale.getCustomerName(),
                        sale.getCustomerPhone(), radio.getBrand() + " " + radio.getModel(),
                        sale.getQuantity(), String.format("$%.2f", sale.getTotalPrice()), seller.getFullName() };
                salesTableModel.addRow(row);
            }
        }
    }

    private void loadAvailableRadios() {
        availableRadiosModel.setRowCount(0);
        List<Radio> radios = DatabaseManager.getAllRadios();
        for (Radio radio : radios) {
            if (radio.getQuantity() > 0) {
                Object[] row = { radio.getId(), radio.getModel(), radio.getBrand(),
                        radio.getType().getDisplayName(), String.format("$%.2f", radio.getPrice()),
                        radio.getQuantity() };
                availableRadiosModel.addRow(row);
            }
        }
        updateTotal();
    }

    private void updateTotal() {
        int selectedRow = availableRadiosTable.getSelectedRow();
        if (selectedRow != -1) {
            String priceStr = (String) availableRadiosModel.getValueAt(selectedRow, 4);
            double price = Double.parseDouble(priceStr.substring(1));
            totalLabel.setText(String.format("Selected Price: $%.2f", price));
        } else {
            totalLabel.setText("Total: $0.00");
        }
    }

    private void processSale() {
        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();

        if (customerName.isEmpty() || customerPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer information.");
            return;
        }

        int selectedRow = availableRadiosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a radio to sell.");
            return;
        }

        int radioId = (int) availableRadiosModel.getValueAt(selectedRow, 0);
        List<Radio> radios = DatabaseManager.getAllRadios();
        Radio selectedRadio = radios.stream().filter(r -> r.getId() == radioId).findFirst().orElse(null);

        if (selectedRadio == null) {
            JOptionPane.showMessageDialog(this, "Radio not found.");
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity to sell:", "Quantity",
                JOptionPane.QUESTION_MESSAGE);
        if (quantityStr == null || quantityStr.isEmpty())
            return;

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0 || quantity > selectedRadio.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Available: " + selectedRadio.getQuantity());
                return;
            }

            double totalPrice = selectedRadio.getPrice() * quantity;
            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Confirm sale:\n\nCustomer: %s\nPhone: %s\nRadio: %s %s\nQuantity: %d\nTotal: $%.2f",
                            customerName, customerPhone, selectedRadio.getBrand(), selectedRadio.getModel(),
                            quantity, totalPrice),
                    "Confirm Sale", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int saleId = DatabaseManager.getNextSaleId();
                Sale sale = new Sale(saleId, radioId, quantity, totalPrice, customerName, customerPhone,
                        currentUser.getId());
                DatabaseManager.saveSale(sale);

                selectedRadio.setQuantity(selectedRadio.getQuantity() - quantity);
                DatabaseManager.updateRadio(selectedRadio);

                customerNameField.setText("");
                customerPhoneField.setText("");
                totalLabel.setText("Total: $0.00");
                loadAvailableRadios();
                loadSalesData();

                JOptionPane.showMessageDialog(this, "Sale processed successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
        }
    }
}