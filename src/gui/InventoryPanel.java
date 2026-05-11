package gui;

import database.DatabaseManager;
import models.Radio;
import models.RadioType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private boolean isAdmin;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public InventoryPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        initComponents();
        setupLayout();
        loadInventoryData();
    }

    private void initComponents() {
        // Table setup
        String[] columns = { "ID", "Model", "Brand", "Type", "Price", "Qty", "Frequency", "Power (W)", "Description" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        inventoryTable.setRowHeight(25);

        // Search components
        searchField = new JTextField(20);
        typeFilter = new JComboBox<>(new String[] { "All", "Portable Radio", "Base Radio" });

        // Buttons
        addButton = new JButton("Add Radio");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        // Style buttons
        styleButton(addButton, new Color(46, 204, 113));
        styleButton(editButton, new Color(52, 152, 219));
        styleButton(deleteButton, new Color(231, 76, 60));
        styleButton(refreshButton, new Color(149, 165, 166));

        // Set admin-only components
        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void setupLayout() {
        // Top panel with controls
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type:"));
        searchPanel.add(typeFilter);

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, new Color(52, 152, 219));
        searchPanel.add(searchButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add listeners
        searchButton.addActionListener(e -> searchInventory());
        searchField.addActionListener(e -> searchInventory());
        typeFilter.addActionListener(e -> searchInventory());

        addButton.addActionListener(e -> addRadio());
        editButton.addActionListener(e -> editRadio());
        deleteButton.addActionListener(e -> deleteRadio());
        refreshButton.addActionListener(e -> loadInventoryData());
    }

    private void loadInventoryData() {
        tableModel.setRowCount(0);
        List<Radio> radios = DatabaseManager.getAllRadios();

        for (Radio radio : radios) {
            Object[] row = {
                    radio.getId(),
                    radio.getModel(),
                    radio.getBrand(),
                    radio.getType().getDisplayName(),
                    String.format("$%.2f", radio.getPrice()),
                    radio.getQuantity(),
                    radio.getFrequency(),
                    radio.getPowerOutput() + "W",
                    radio.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    private void searchInventory() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        String selectedType = (String) typeFilter.getSelectedItem();

        tableModel.setRowCount(0);
        List<Radio> radios = DatabaseManager.getAllRadios();

        for (Radio radio : radios) {
            boolean matchesSearch = searchTerm.isEmpty() ||
                    radio.getModel().toLowerCase().contains(searchTerm) ||
                    radio.getBrand().toLowerCase().contains(searchTerm) ||
                    radio.getDescription().toLowerCase().contains(searchTerm);

            boolean matchesType = selectedType.equals("All") ||
                    radio.getType().getDisplayName().equals(selectedType);

            if (matchesSearch && matchesType) {
                Object[] row = {
                        radio.getId(),
                        radio.getModel(),
                        radio.getBrand(),
                        radio.getType().getDisplayName(),
                        String.format("$%.2f", radio.getPrice()),
                        radio.getQuantity(),
                        radio.getFrequency(),
                        radio.getPowerOutput() + "W",
                        radio.getDescription()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addRadio() {
        if (!isAdmin)
            return;

        JDialog dialog = createRadioDialog(null);
        dialog.setVisible(true);
    }

    private void editRadio() {
        if (!isAdmin)
            return;

        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a radio to edit.");
            return;
        }

        int radioId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Radio> radios = DatabaseManager.getAllRadios();
        Radio radioToEdit = radios.stream().filter(r -> r.getId() == radioId).findFirst().orElse(null);

        if (radioToEdit != null) {
            JDialog dialog = createRadioDialog(radioToEdit);
            dialog.setVisible(true);
        }
    }

    private void deleteRadio() {
        if (!isAdmin)
            return;

        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a radio to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this radio?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int radioId = (int) tableModel.getValueAt(selectedRow, 0);
            DatabaseManager.deleteRadio(radioId);
            loadInventoryData();
            JOptionPane.showMessageDialog(this, "Radio deleted successfully!");
        }
    }

    private JDialog createRadioDialog(Radio radio) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                radio == null ? "Add New Radio" : "Edit Radio",
                true);

        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        JTextField modelField = new JTextField(20);
        JTextField brandField = new JTextField(20);
        JComboBox<RadioType> typeCombo = new JComboBox<>(RadioType.values());
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField frequencyField = new JTextField(20);
        JTextField powerField = new JTextField(20);

        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descScroll = new JScrollPane(descriptionArea);

        // If editing existing radio
        if (radio != null) {
            modelField.setText(radio.getModel());
            brandField.setText(radio.getBrand());
            typeCombo.setSelectedItem(radio.getType());
            priceField.setText(String.valueOf(radio.getPrice()));
            quantityField.setText(String.valueOf(radio.getQuantity()));
            frequencyField.setText(radio.getFrequency());
            powerField.setText(String.valueOf(radio.getPowerOutput()));
            descriptionArea.setText(radio.getDescription());
        }

        // Add fields
        int row = 0;

        addFormField(dialog, gbc, row++, "Model:", modelField);
        addFormField(dialog, gbc, row++, "Brand:", brandField);
        addFormField(dialog, gbc, row++, "Type:", typeCombo);
        addFormField(dialog, gbc, row++, "Price ($):", priceField);
        addFormField(dialog, gbc, row++, "Quantity:", quantityField);
        addFormField(dialog, gbc, row++, "Frequency:", frequencyField);
        addFormField(dialog, gbc, row++, "Power Output (W):", powerField);

        // Description label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        dialog.add(new JLabel("Description:"), gbc);

        row++;

        // Description area
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        dialog.add(descScroll, gbc);

        row++;

        // Reset fill
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton, new Color(46, 204, 113));
        styleButton(cancelButton, new Color(149, 165, 166));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        dialog.add(buttonPanel, gbc);

        // SAVE ACTION
        saveButton.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                String brand = brandField.getText().trim();
                RadioType type = (RadioType) typeCombo.getSelectedItem();

                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                String frequency = frequencyField.getText().trim();

                int power = Integer.parseInt(powerField.getText().trim());

                String description = descriptionArea.getText().trim();

                // Validation
                if (model.isEmpty() ||
                        brand.isEmpty() ||
                        frequency.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Please fill in all required fields.");
                    return;
                }

                if (radio == null) {

                    // ADD NEW RADIO
                    int newId = DatabaseManager.getNextRadioId();

                    Radio newRadio = new Radio(
                            newId,
                            model,
                            brand,
                            type,
                            price,
                            quantity,
                            frequency,
                            power,
                            description);

                    DatabaseManager.saveRadio(newRadio);

                } else {

                    // UPDATE EXISTING RADIO
                    radio.setModel(model);
                    radio.setBrand(brand);
                    radio.setType(type);
                    radio.setPrice(price);
                    radio.setQuantity(quantity);
                    radio.setFrequency(frequency);
                    radio.setPowerOutput(power);
                    radio.setDescription(description);

                    DatabaseManager.updateRadio(radio);
                }

                loadInventoryData();

                JOptionPane.showMessageDialog(
                        this,
                        "Radio saved successfully!");

                dialog.dispose();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter valid numbers for:\n" +
                                "- Price\n" +
                                "- Quantity\n" +
                                "- Power Output");
            }
        });

        // CANCEL ACTION
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setMinimumSize(new Dimension(500, 600));
        dialog.setLocationRelativeTo(this);

        return dialog;
    }

    private void addFormField(JDialog dialog, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(field, gbc);
    }
}